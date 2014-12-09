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
import br.ufjf.taverna.model.input.TavernaInput;
import br.ufjf.taverna.model.output.TavernaOutput;
import br.ufjf.taverna.model.run.TavernaRun;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author vitorfs
 */
public interface TavernaServices {
    
    // Main Server Resource
    public String create(String filePath) throws TavernaException;
    public ArrayList<TavernaRun> getRuns() throws TavernaException;
    public String getEnabledNotificationFabrics() throws TavernaException;
    public String getPermittedListenerTypes() throws TavernaException;
    public String getPermittedWorkflows() throws TavernaException;
    public String getRunLimit() throws TavernaException;
    
    // Per-Workflow Run Resource
    public String getSubResources(String uuid) throws TavernaException;
    public void destroy(String uuid) throws TavernaException;
    public Date getExpiry(String uuid) throws TavernaException;
    public void setExpiry(String uuid, Date time) throws TavernaException;
    public Date getCreateTime(String uuid) throws TavernaException;
    public Date getFinishTime(String uuid) throws TavernaException;
    public Date getStartTime(String uuid) throws TavernaException;
    public String getStatus(String uuid) throws TavernaException;
    public void setStatus(String uuid, TavernaServerStatus status) throws TavernaException;
    public File getWorkflow(String uuid) throws TavernaException;
    public String getInput(String uuid) throws TavernaException;
    public String getBaclavaInput(String uuid) throws TavernaException;
    public String setBaclavaInput(String uuid, String file) throws TavernaException;
    public ArrayList<TavernaInput> getExpectedInputs(String uuid) throws TavernaException;
    public String getInputValue(String uuid, String inputName) throws TavernaException;
    public String setInputValue(String uuid, String inputName, String inputValue) throws TavernaException;
    public ArrayList<TavernaOutput> getOutput(String uuid) throws TavernaException;
    public void setBaclavaOutput(String uuid, String file) throws TavernaException;
    public String getListeners(String uuid) throws TavernaException;
    public void setListeners(String uuid, String listener) throws TavernaException;
    public String getListener(String uuid, String listener) throws TavernaException;
    public String getListenerConfiguration(String uuid, String listener) throws TavernaException;
    public String getListenerProperties(String uuid, String listener) throws TavernaException;
    public String getListenerProperty(String uuid, String listener, String propertyName) throws TavernaException;
    public String getSecurity(String uuid) throws TavernaException;
    public String getSecurityCredentials(String uuid) throws TavernaException;
    public void setSecurityCredentials(String uuid, String credential) throws TavernaException;
    public void removeSecurityCredentials(String uuid) throws TavernaException;
    public String getSecurityCredential(String uuid, String credential) throws TavernaException;
    public String updateSecurityCredential(String uuid, String credential, String newCredential) throws TavernaException;
    public String removeSecurityCredential(String uuid, String credential) throws TavernaException;
    public String getSecurityOwner(String uuid) throws TavernaException;
    public String getSecurityPermissions(String uuid) throws TavernaException;
    public void setSecurityPermissions(String uuid, String userName, String permission) throws TavernaException;
    public String getSecurityPermission(String uuid, String userName) throws TavernaException;
    public String getSecurityTrusts(String uuid) throws TavernaException;
    public void setSecurityTrusts(String uuid, String certificateFile, String fileType, String certificateBytes) throws TavernaException;
    public void removeSecurityTrusts(String uuid) throws TavernaException;
    public String getSecurityTrust(String uuid, String trustId) throws TavernaException;
    public void updateSecurityTrust(String uuid, String trustId, String certificateFile, String fileType, String certificateBytes) throws TavernaException;
    public void removeSecurityTrust(String uuid, String trustId) throws TavernaException;
    public File getWorkingDirectoryCompressed(String uuid) throws TavernaException;
    public File getWorkingDirectoryCompressed(String uuid, String filePath) throws TavernaException;
    public String getWorkingDirectoryFileContent(String uuid, String filePath) throws TavernaException;
    public void setWorkingDirectoryFileContent(String uuid, File file) throws TavernaException;
    public void setWorkingDirectoryDirectory(String uuid, String directoryName) throws TavernaException;
    public void removeWorkingDirectoryFileOrDirectory(String uuid, String path) throws TavernaException;
}
