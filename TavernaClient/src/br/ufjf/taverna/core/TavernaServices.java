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
import br.ufjf.taverna.model.output.TavernaWorkflowOutput;
import br.ufjf.taverna.model.run.TavernaRun;
import java.util.ArrayList;

/**
 *
 * @author vitorfs
 */
public interface TavernaServices {
    
    public String create(String filePath) throws TavernaException;
    public ArrayList<TavernaRun> getRuns() throws TavernaException;
    public String getStatus(String uuid) throws TavernaException;
    public TavernaWorkflowOutput getOutput(String uuid) throws TavernaException;
    public TavernaExpectedInput getExpectedInputs(String uuid) throws TavernaException;
    public String setInputValue(String uuid, String inputName, String inputValue) throws TavernaException;
    public void start(String uuid) throws TavernaException;
    public void destroy(String uuid) throws TavernaException;
}
