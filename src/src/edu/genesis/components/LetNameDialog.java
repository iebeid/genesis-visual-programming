///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package edu.genesis.components;
//
//import edu.genesis.model.StickyNote;
//import edu.genesis.view.GenesisDevelopmentEnvironmentViewController;
//import java.util.ArrayList;
//import java.util.List;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.fxml.FXML;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
///**
// *
// * @author Laptop
// */
//public class LetNameDialog extends GenesisDevelopmentEnvironmentViewController {
//
//    final TextField nameBox;
//    final TextField valueBox;
//    private ArrayList<String> generatedCode;
//    public LetNameDialog(final String formatName) {
//        generatedCode = new ArrayList<>();
//        final Stage stage = new Stage();
//        Label nameLabel = new Label("Name");
//        nameBox = new TextField();
//        Label valueLabel = new Label("Value");
//        valueBox = new TextField();
//        Button okButton = new Button("Ok");
//        okButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent e) {
//                
//                StickyNote sn = new StickyNote();
//                sn.setName(nameBox.getText());
//                sn.setValue(valueBox.getText());
//                variableObservableList.add(sn);
//                String code = formatCode(formatName);
//                //codeArea.appendText(code);
//                generatedCode.add(code);
//                stage.close();
//            }
//        });
//        VBox v = new VBox();
//        v.getChildren().add(nameLabel);
//        v.getChildren().add(nameBox);
//        v.getChildren().add(valueLabel);
//        v.getChildren().add(valueBox);
//        v.getChildren().add(okButton);
//
//        Scene scene = new Scene(v);
//        stage.setTitle("StickyNote/List");
//        stage.setScene(scene);
//        stage.show();
//    }
//    
//    public ArrayList<String> getGeneratedCode(){
//        return this.generatedCode;
//    }
//
//    private String formatCode(String codeName) {
//        String code = "";
//        String[] codeParts = codeName.split(" ");
//        for (String string : codeParts) {
//            System.out.println("Code Parts : " + string);
//            if (string.startsWith("_") && string.endsWith("_")) {
//                if (string.contains("StickyNote")) {
//                    string = string.replace(string, nameBox.getText());
//                }
//                if (string.contains("expression")) {
//                    string = string.replace(string, valueBox.getText());
//                }
//            }
//            code = code + " " + string;
//        }
//        return code;
//    }
//}
