/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.model;

/**
 *
 * @author Laptop
 */
public class Procedure {
    private String procedureName;
    private String parameters;
     private String algorithm;

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
   

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    
}
