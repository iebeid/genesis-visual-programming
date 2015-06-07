/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.components;

import edu.genesis.model.Block;
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
public class BlockDialog extends GenesisDevelopmentEnvironmentViewController {
final TextArea inputCode;
private Block blockObj = new Block();

    public BlockDialog(final String formatName) {
        final Stage stage = new Stage();
        Label label1 = new Label("{");
        inputCode = new TextArea();
        Label label2 = new Label("}");
        Button okButton = new Button("Ok");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String code = formatCode(formatName);
                blockObj.setCode(inputCode.getText());
                codeArea.appendText(code);
                stage.close();
            }
        });
        VBox v = new VBox();
        v.getChildren().add(label1);
        v.getChildren().add(inputCode);
        v.getChildren().add(label2);
        v.getChildren().add(okButton);

        Scene scene = new Scene(v);
        stage.setTitle("Block");
        stage.setScene(scene);
        stage.show();
    }
      private String formatCode(String codeName) {
        String code = "";
        String[] codeParts = codeName.split(" ");
        for (String string : codeParts) {
            System.out.println("Code Parts : " + string);
            if ((string.startsWith("_") && string.endsWith("_"))) {
                if (string.contains("Step")) {
                    string = string.replace(string, inputCode.getText());
                }
            }
            code = code + " " + string;
        }
        return code;
    }
    
}
