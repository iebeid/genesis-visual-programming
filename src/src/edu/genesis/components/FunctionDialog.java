/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.components;

import edu.genesis.model.Function;
import edu.genesis.view.GenesisDevelopmentEnvironmentViewController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Laptop
 */
public class FunctionDialog extends GenesisDevelopmentEnvironmentViewController {
    final Label funcNameLabel;
    final TextField funcNameTextField;
    final Label paramNameLabel;
    final TextField paramNameTextField;
    final Label funcCodeLabel;
    final TextArea funcCodeArea;
    final Label returnTypeLabel;
    final TextField returnTypeArea;
    Function funcObj = new Function();
    public FunctionDialog(final String formatName) {
        final Stage stage = new Stage();
        funcNameLabel = new Label("Function Name");
 funcNameTextField = new TextField();
 paramNameLabel = new Label("Parameters");
 paramNameTextField = new TextField();
 funcCodeLabel = new Label("Code");
 funcCodeArea = new TextArea();
 returnTypeLabel = new Label("Return Type");
 returnTypeArea = new TextField();
 
  Button okButton = new Button("Ok");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String code = formatCode(formatName);
                funcObj.setFunctionName(funcNameTextField.getText());
                funcObj.setParameters(paramNameTextField.getText());
                funcObj.setCode(funcCodeArea.getText());
                funcObj.setReturnType(returnTypeArea.getText());
                codeArea.appendText(code);
                stage.close();
            }
        });
        VBox v = new VBox();
        v.getChildren().add(funcNameLabel);
        v.getChildren().add(funcNameTextField);
        v.getChildren().add(paramNameLabel);
        v.getChildren().add(paramNameTextField);
         v.getChildren().add(funcCodeLabel);
        v.getChildren().add(funcCodeArea);
        v.getChildren().add(returnTypeLabel);
        v.getChildren().add(returnTypeArea);
        v.getChildren().add(okButton);

        Scene scene = new Scene(v);
        stage.setTitle("Function");
        stage.setScene(scene);
        stage.show();


    }
    
     private String formatCode(String codeName) {
        String code = "";
        String[] codeParts = codeName.split(" ");
        for (String string : codeParts) {
            System.out.println("Code Parts : " + string);
            if ((string.startsWith("_") && string.endsWith("_"))||(string.startsWith("(")&&string.endsWith(")"))) {
                if (string.contains("parameter")) {
                    string = string.replace(string, "( " + paramNameTextField.getText() + " )");
                }
                if (string.contains("function")) {
                    string = string.replace(string, funcNameTextField.getText());
                }
                if (string.contains("ans")) {
                    string = string.replace(string, returnTypeArea.getText());
                }
                if (string.contains("algorithm")) {
                    string = string.replace(string, funcCodeArea.getText());
                }
            }
            code = code + " " + string;
        }
        return code;
    }
    
}
