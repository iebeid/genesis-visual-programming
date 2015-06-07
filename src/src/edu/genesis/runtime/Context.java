/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.runtime;

/**
 *
 * @author Laptop
 */
class Context {

    public int lineNo;
    public String fileName;
    public int charPos;

    Context() {
        lineNo = 0;
        charPos = 0;
        fileName = "";
    }

    Context(int ln, int cp) {
        lineNo = ln;
        charPos = cp;
    }

    Context(int ln, int cp, String fn) {
        lineNo = ln;
        charPos = cp;
        fileName = "";
    }

    Context(Token t) {
        lineNo = t.lineNo();
        charPos = t.charPos();
        fileName = t.fileName();
    }

    @Override
    public String toString() {
        String ans = "(" + fileName + "/" + lineNo + "/" + charPos + ")";
        return ans;
    }
} // Context
