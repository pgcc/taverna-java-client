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
package br.ufjf.taverna.sample;

import br.ufjf.taverna.core.TavernaClient;
import br.ufjf.taverna.model.input.TavernaExpectedInput;
import br.ufjf.taverna.model.input.TavernaInput;
import br.ufjf.taverna.model.output.TavernaOutput;
import br.ufjf.taverna.model.output.TavernaWorkflowOutput;


/**
 *
 * @author vitorfs
 */
public class TavernaSample {
    
    public static void main(String[] args) {
        
        TavernaClient client = new TavernaClient();
        
        //client.setBaseUri("http://localhost:8080/TavernaServer-2.5.4/rest");
        client.setBaseUri("http://ec2-54-191-44-161.us-west-2.compute.amazonaws.com:8080/TavernaServer-2.5.4/rest");
        client.setAuthorization("taverna", "taverna");

        try {
            
            String uuid = "";
            String status = "";
            TavernaExpectedInput tavernaInput = null;
            TavernaWorkflowOutput tavernaOutput = null;
            
            
            uuid = client.create("/Users/vitorfs/Downloads/Web_Service_example.t2flow");
            //uuid = client.create("/Users/vitorfs/Downloads/Workflow_Bruno.t2flow");
            System.out.println(uuid);
            
            /*
            ArrayList<TavernaRun> runs = client.getRuns();
            for (TavernaRun run : runs) {
                //client.destroy(run.getUuid());
                System.out.println(run.getUuid());
            }
            */
            status = client.getStatus(uuid);
            System.out.println(status);
            
            
            tavernaInput = client.getExpectedInputs(uuid);
            if (tavernaInput != null && tavernaInput.getInputDescription() != null && tavernaInput.getInputDescription().getInput() != null) {
                for (TavernaInput input : tavernaInput.getInputDescription().getInput()) {
                    if (input != null) {
                        System.out.println(input.getName());
                    }
                }
            }
            
            
            System.out.println(client.setInputValue(uuid, "Country", "Brazil"));
            System.out.println(client.setInputValue(uuid, "City", "Juiz de Fora"));
            
            
            client.start(uuid);

            do {
                status = client.getStatus(uuid);
                System.out.println(status);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            } while (!"Finished".equals(status));
            
            
            
            tavernaOutput = client.getOutput(uuid);
            for (TavernaOutput output : tavernaOutput.getWorkflowOutputs().getOutput()) {
                System.out.println(output.getName());
            }
            
            
            //client.destroy(uuid);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
