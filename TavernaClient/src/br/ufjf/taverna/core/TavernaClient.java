/*
 * The MIT License
 *
 * Copyright 2014 Pós-Graduação em Ciência da Computação UFJF.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.ufjf.taverna.core;

import br.ufjf.taverna.exception.TavernaException;
import br.ufjf.taverna.model.input.TavernaExpectedInput;
import br.ufjf.taverna.model.input.TavernaInput;
import br.ufjf.taverna.model.output.TavernaOutput;
import br.ufjf.taverna.model.run.TavernaRun;
import br.ufjf.taverna.model.run.TavernaRuns;
import br.ufjf.taverna.model.output.TavernaWorkflowOutput;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author vitorfs
 */
public class TavernaClient extends TavernaClientBase implements TavernaServices {

    /**
     * Perform a POST to the TavernaServer endpoint <strong>/runs</strong>
     * @param  filePath the absolute file path of the t2flow file
     * @throws TavernaException in case of invalid response code
     * @return the UUID of the workflow run
     */
    @Override
    public String create(String filePath) throws TavernaException {
        String url = "/runs";
        HttpURLConnection response = request(url, TavernaServerMethods.POST, HttpURLConnection.HTTP_CREATED, "application/json", "application/vnd.taverna.t2flow+xml", filePath);
        String uuid = null;
        String location = response.getHeaderField("Location");
        if (location != null) {
            uuid = location.split("/runs/")[1];
        }
        response.disconnect();
        return uuid;
    }
    
    /**
     * Perform a GET to the TavernaServer endpoint <strong>/runs</strong>
     * @return an <tt>ArrayList&lt;TavernaRun&gt;</tt> of current runs
     * @throws TavernaException 
     */
    @Override
    public ArrayList<TavernaRun> getRuns() throws TavernaException {
        String url = "/runs";
        HttpURLConnection response = request(url, TavernaServerMethods.GET, HttpURLConnection.HTTP_OK, "application/json");
        String content = parseResponse(response);
        content = content.replace("@", "");
        content = content.replace("$", "uuid");
        Gson gson = new Gson();
        TavernaRuns tavernaRuns = null;
        
        try {
            tavernaRuns = gson.fromJson(content, TavernaRuns.class);
        } catch (JsonSyntaxException arrayException) { // Expected array, found single result
            try {
                content = content.replace("\"run\"", "\"singleRun\"");
                tavernaRuns = gson.fromJson(content, TavernaRuns.class);
            } catch (JsonSyntaxException singleResultException) { // Expected single result, found none
                
            }
        } 
        finally {
            if (response != null) {
                response.disconnect();
            }            
        }
        
        if (tavernaRuns != null && tavernaRuns.getRunList() != null) {
            return tavernaRuns.getRunList().getRun();
        }
        return new ArrayList<>();
    }
    
    @Override
    public String getStatus(String uuid) throws TavernaException {
        String url = String.format("/runs/%s/status", uuid);
        HttpURLConnection response = request(url, TavernaServerMethods.GET, HttpURLConnection.HTTP_OK, null, "text/plain");
        String content = parseResponse(response);
        if (response != null) {
            response.disconnect();
        }
        if (content != null) {
            content = content.replace("\n", "");
        }
        return content;
    }
    
    @Override
    public void setStatus(String uuid, TavernaServerStatus status) throws TavernaException {
        String url = String.format("/runs/%s/status", uuid);
        String data = status.getStatus();
        HttpURLConnection response = request(url, TavernaServerMethods.PUT, HttpURLConnection.HTTP_ACCEPTED, "text/plain", "text/plain",  null, data);
        response.disconnect();        
    }
    
    public void start(String uuid) throws TavernaException {
        setStatus(uuid, TavernaServerStatus.OPERATING);
    }
    
    public void cancel(String uuid) throws TavernaException {
        setStatus(uuid, TavernaServerStatus.FINISHED);
    }
    
    @Override
    public ArrayList<TavernaInput> getExpectedInputs(String uuid) throws TavernaException {
        String url = String.format("/runs/%s/input/expected", uuid);
        HttpURLConnection response = request(url, TavernaServerMethods.GET, HttpURLConnection.HTTP_OK, "application/json");
        String content = parseResponse(response);
        content = content.replace("@", "");
        Gson gson = new Gson();
        TavernaExpectedInput input = gson.fromJson(content, TavernaExpectedInput.class);
        if (response != null) {
            response.disconnect();
        }
        if (input != null && input.getInputDescription() != null && input.getInputDescription().getInput() != null) {
            return input.getInputDescription().getInput();
        }
        return new ArrayList<>();
    }
    
    @Override
    public String setInputValue(String uuid, String inputName, String inputValue) throws TavernaException {
        String value = String.format("<t2sr:runInput xmlns:t2sr=\"http://ns.taverna.org.uk/2010/xml/server/rest/\">"+
                                        "<t2sr:value>%s</t2sr:value>" +
                                    "</t2sr:runInput>", inputValue);
        String url = String.format("/runs/%s/input/input/%s", uuid, inputName);
        HttpURLConnection response = request(url, TavernaServerMethods.PUT, HttpURLConnection.HTTP_OK, "application/json", "application/xml", null, value);
        String content = parseResponse(response);
        content = content.replace("@", "");
        return content;
    }
    
    @Override
    public void destroy(String uuid) throws TavernaException {
        String url = String.format("/runs/%s", uuid);
        HttpURLConnection response = request(url, TavernaServerMethods.DELETE, HttpURLConnection.HTTP_NO_CONTENT, null, "application/x-www-form-urlencoded");
        response.disconnect();
    }
    
    @Override
    public ArrayList<TavernaOutput> getOutput(String uuid) throws TavernaException {
        String url = String.format("/runs/%s/output", uuid);
        HttpURLConnection response = request(url, TavernaServerMethods.GET, HttpURLConnection.HTTP_OK, "application/json");
        String content = parseResponse(response);
        content = content.replace("@", "");
        Gson gson = new Gson();
        TavernaWorkflowOutput output = null;
        
        try {
            output = gson.fromJson(content, TavernaWorkflowOutput.class);
        } catch (JsonSyntaxException e) { // TavernaServer returned only one output tag
            content = content.replace("output", "singleOutput");
            output = gson.fromJson(content, TavernaWorkflowOutput.class);
        } catch (Exception e) {
            
        } finally {
            if (response != null) {
                response.disconnect();
            }   
        }
        
        if (output != null && output.getWorkflowOutputs() != null && output.getWorkflowOutputs().getOutput() != null) {
            output.getWorkflowOutputs().getOutput();
        }
        return new ArrayList<>();
    }

    @Override
    public String getEnabledNotificationFabrics() throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPermittedListenerTypes() throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPermittedWorkflows() throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getRunLimit() throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSubResources(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date getExpiry(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setExpiry(String uuid, Date time) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date getCreateTime(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date getFinishTime(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date getStartTime(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public File getWorkflow(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getInput(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getBaclavaInput(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String setBaclavaInput(String uuid, String file) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getInputValue(String uuid, String inputName) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBaclavaOutput(String uuid, String file) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getListeners(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setListeners(String uuid, String listener) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getListener(String uuid, String listener) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getListenerConfiguration(String uuid, String listener) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getListenerProperties(String uuid, String listener) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getListenerProperty(String uuid, String listener, String propertyName) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSecurity(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSecurityCredentials(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSecurityCredentials(String uuid, String credential) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSecurityCredentials(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSecurityCredential(String uuid, String credential) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String updateSecurityCredential(String uuid, String credential, String newCredential) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String removeSecurityCredential(String uuid, String credential) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSecurityOwner(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSecurityPermissions(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSecurityPermissions(String uuid, String userName, String permission) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSecurityPermission(String uuid, String userName) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSecurityTrusts(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSecurityTrusts(String uuid, String certificateFile, String fileType, String certificateBytes) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSecurityTrusts(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSecurityTrust(String uuid, String trustId) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateSecurityTrust(String uuid, String trustId, String certificateFile, String fileType, String certificateBytes) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSecurityTrust(String uuid, String trustId) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public File getWorkingDirectoryCompressed(String uuid) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public File getWorkingDirectoryCompressed(String uuid, String filePath) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getWorkingDirectoryFileContent(String uuid, String filePath) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setWorkingDirectoryFileContent(String uuid, File file) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setWorkingDirectoryDirectory(String uuid, String directoryName) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeWorkingDirectoryFileOrDirectory(String uuid, String path) throws TavernaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
