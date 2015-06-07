/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.components;

import edu.genesis.model.Procedure;
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
public class ProcedureDialog extends GenesisDevelopmentEnvironmentViewController {

    final Label funcNameLabel;
    final TextField funcNameTextField;
    final Label funcCodeLabel;
    final TextArea funcCodeArea;
    Procedure funcObj = new Procedure();

    public ProcedureDialog(final String formatName) {
        final Stage stage = new Stage();
        funcNameLabel = new Label("Prodcedure Name");
        funcNameTextField = new TextField();
        funcCodeLabel = new Label("Code");
        funcCodeArea = new TextArea();

        Button okButton = new Button("Ok");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String code = formatCode(formatName);
                funcObj.setProcedureName(funcNameTextField.getText());
                funcObj.setAlgorithm(funcCodeArea.getText());
                codeArea.appendText(code);
                stage.close();
            }
        });
        VBox v = new VBox();
        v.getChildren().add(funcNameLabel);
        v.getChildren().add(funcNameTextField);
        v.getChildren().add(funcCodeLabel);
        v.getChildren().add(funcCodeArea);
        v.getChildren().add(okButton);

        Scene scene = new Scene(v);
        stage.setTitle("Procedure");
        stage.setScene(scene);
        stage.show();


    }

    private String formatCode(String codeName) {
        String code = "";
        String[] codeParts = codeName.split(" ");
        for (String string : codeParts) {
            System.out.println("Code Parts : " + string);
            if ((string.startsWith("_") && string.endsWith("_")) || (string.startsWith("(") && string.endsWith(")"))) {
                if (string.contains("procedure")) {
                    string = string.replace(string, funcNameTextField.getText());
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
