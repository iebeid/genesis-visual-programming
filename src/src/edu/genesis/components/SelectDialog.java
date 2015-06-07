/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.components;

import edu.genesis.model.Select;
import edu.genesis.view.GenesisDevelopmentEnvironmentViewController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Laptop
 */
public class SelectDialog extends GenesisDevelopmentEnvironmentViewController {
final Label condLabel;
final TextField condField;
final Label stepLabel;
final TextField stepField;
Select selectObj = new Select();
    public SelectDialog(final String formatName) {
        final Stage stage = new Stage();
        condLabel = new Label("Condition");
        condField = new TextField();
        stepLabel = new Label("Step");
        stepField = new TextField();
        Button okButton = new Button("Ok");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String code = formatCode(formatName);
                selectObj.setCondition(condField.getText());
                selectObj.setStep(stepField.getText());
                codeArea.appendText(code);
                stage.close();
            }
        });
        VBox v = new VBox();
        v.getChildren().add(condLabel);
        v.getChildren().add(condField);
        v.getChildren().add(stepLabel);
        v.getChildren().add(stepField);
        v.getChildren().add(okButton);
        Scene scene = new Scene(v);
        stage.setTitle("Select");
        stage.setScene(scene);
        stage.show();
    }
    private String formatCode(String codeName) {
        String code = "";
        String[] codeParts = codeName.split(" ");
        for (String string : codeParts) {
            System.out.println("Code Parts : " + string);
            if ((string.startsWith("_") && string.endsWith("_"))) {
                if (string.contains("cond")) {
                    string = string.replace(string,condField.getText());
                }
                if (string.contains("step")) {
                    string = string.replace(string, stepField.getText());
                }
            }
            code = code + " " + string;
        }
        return code;
    }
    
}
