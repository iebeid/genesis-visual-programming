/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.components;

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
public class UnaliasDialog extends GenesisDevelopmentEnvironmentViewController {
Label stickynote;
TextField sntext;

    public UnaliasDialog(final String formatName) {
        final Stage stage = new Stage();
        stickynote = new Label("Sticky Note");
        sntext = new TextField();
         Button okButton = new Button("Ok");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String code = formatCode(formatName);
                codeArea.appendText(code);
                stage.close();
            }
        });
         VBox v = new VBox();
        v.getChildren().add(stickynote);
        v.getChildren().add(sntext);
        v.getChildren().add(okButton);
        Scene scene = new Scene(v);
        stage.setTitle("Unalias");
        stage.setScene(scene);
        stage.show();
    }
    private String formatCode(String codeName) {
        String code = "";
        String[] codeParts = codeName.split(" ");
        for (String string : codeParts) {
            System.out.println("Code Parts : " + string);
            if ((string.startsWith("_") && string.endsWith("_"))) {
                if (string.contains("StickyNote")) {
                    string = string.replace(string,sntext.getText());
                }
            }
            code = code + " " + string;
        }
        return code;
    }
    
}
