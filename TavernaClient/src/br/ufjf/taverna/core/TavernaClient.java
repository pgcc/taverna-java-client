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
import br.ufjf.taverna.model.run.TavernaRun;
import br.ufjf.taverna.model.run.TavernaRuns;
import br.ufjf.taverna.model.output.TavernaWorkflowOutput;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 *
 * @author vitorfs
 */
public class TavernaClient extends TavernaClientBase implements TavernaServices {

    /**
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
    public TavernaWorkflowOutput getOutput(String uuid) throws TavernaException {
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
        return output;
    }
    
    @Override
    public TavernaExpectedInput getExpectedInputs(String uuid) throws TavernaException {
        String url = String.format("/runs/%s/input/expected", uuid);
        HttpURLConnection response = request(url, TavernaServerMethods.GET, HttpURLConnection.HTTP_OK, "application/json");
        String content = parseResponse(response);
        content = content.replace("@", "");
        Gson gson = new Gson();
        TavernaExpectedInput input = gson.fromJson(content, TavernaExpectedInput.class);
        if (response != null) {
            response.disconnect();
        }
        return input;        
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
    public void start(String uuid) throws TavernaException {
        String url = String.format("/runs/%s/status", uuid);
        String data = TavernaServerStatus.OPERATING.getStatus();
        HttpURLConnection response = request(url, TavernaServerMethods.PUT, HttpURLConnection.HTTP_ACCEPTED, "text/plain", "text/plain",  null, data);
        response.disconnect();
    }
    
    @Override
    public void destroy(String uuid) throws TavernaException {
        String url = String.format("/runs/%s", uuid);
        HttpURLConnection response = request(url, TavernaServerMethods.DELETE, HttpURLConnection.HTTP_NO_CONTENT, null, "application/x-www-form-urlencoded");
        response.disconnect();
    }
    
}
