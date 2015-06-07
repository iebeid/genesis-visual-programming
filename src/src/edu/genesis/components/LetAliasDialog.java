/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.components;

import edu.genesis.model.StickyNote;
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
public class LetAliasDialog extends GenesisDevelopmentEnvironmentViewController {
     final TextField nameBox;
    final TextField valueBox;

    public LetAliasDialog(final String formatName) {
        final Stage stage = new Stage();
        Label nameLabel = new Label("Name");
        nameBox = new TextField();
        Label valueLabel = new Label("Value");
        valueBox = new TextField();
        Button okButton = new Button("Ok");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                StickyNote sn = new StickyNote();
                sn.setName(nameBox.getText());
                sn.setValue(valueBox.getText());
                variableObservableList.add(sn);
                Label nameBoxAfterSet = new Label(sn.getName());
                targetBox.getChildren().add(nameBoxAfterSet);
                String code = formatCode(formatName);
                codeArea.appendText(code);
                stage.close();
            }
        });
        VBox v = new VBox();
        v.getChildren().add(nameLabel);
        v.getChildren().add(nameBox);
        v.getChildren().add(valueLabel);
        v.getChildren().add(valueBox);
        v.getChildren().add(okButton);

        Scene scene = new Scene(v);
        stage.setTitle("Alias");
        stage.setScene(scene);
        stage.show();
    }

    private String formatCode(String codeName) {
        String code = "";
        String[] codeParts = codeName.split(" ");
        for (String string : codeParts) {
            System.out.println("Code Parts : " + string);
            if (string.startsWith("_") && string.endsWith("_")) {
                if (string.contains("StickyNote")) {
                    string = string.replace(string, nameBox.getText());
                }
                if (string.contains("expression")) {
                    string = string.replace(string, valueBox.getText());
                }
            }
            code = code + " " + string;
        }
        return code;
    }
}
