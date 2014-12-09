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
package br.ufjf.taverna.model.input;

import java.util.ArrayList;

/**
 *
 * @author vitorfs
 */
public class TavernaInputDescription {
    private String workflowId;
    private String workflowRun;
    private String workflowRunId;
    private ArrayList<TavernaInput> input;

    /**
     * @return the workflowId
     */
    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * @param workflowId the workflowId to set
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * @return the workflowRun
     */
    public String getWorkflowRun() {
        return workflowRun;
    }

    /**
     * @param workflowRun the workflowRun to set
     */
    public void setWorkflowRun(String workflowRun) {
        this.workflowRun = workflowRun;
    }

    /**
     * @return the workflowRunId
     */
    public String getWorkflowRunId() {
        return workflowRunId;
    }

    /**
     * @param workflowRunId the workflowRunId to set
     */
    public void setWorkflowRunId(String workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

    /**
     * @return the input
     */
    public ArrayList<TavernaInput> getInput() {
        return input;
    }

    /**
     * @param input the input to set
     */
    public void setInput(ArrayList<TavernaInput> input) {
        this.input = input;
    }
}
