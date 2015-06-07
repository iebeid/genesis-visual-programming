/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.model;

/**
 *
 * @author Laptop
 */
public class Function {
    private String functionName;
    private String parameters;
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    private String returnType;

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
    
}
