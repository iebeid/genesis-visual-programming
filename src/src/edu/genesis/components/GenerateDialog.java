/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.components;

import edu.genesis.model.Generate;
import edu.genesis.model.StickyNote;
import edu.genesis.view.GenesisDevelopmentEnvironmentViewController;
import edu.genesis.utilities.GuideConfig;
import edu.genesis.utilities.ProgrammingElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Laptop
 */
public class GenerateDialog extends GenesisDevelopmentEnvironmentViewController {

    final TextField nameBox;
    ComboBox listNames;
    ComboBox generateList;
    Generate generateObj = new Generate();
    String condition= "";
    String from="";
    String to="";
    String by="";

    public GenerateDialog(final String formatName) {
        final Stage stage = new Stage();
        Label nameLabel = new Label("Iterator");
        nameBox = new TextField();
        Label valueLabel = new Label("StickyNote/List");
        listNames = new ComboBox();
        List<String> listNamesList = new ArrayList<>();
        for (StickyNote sn : variableObservableList) {
            listNamesList.add(sn.getName());
        }
        ObservableList<String> listNamesOL = FXCollections.observableArrayList(listNamesList);
        listNames.setItems(listNamesOL);
        Label generateLabel = new Label("Generate Type");
        generateList = new ComboBox();
        generateList.setItems(this.generateList());
        VBox v = new VBox();
        v.getChildren().add(nameLabel);
        v.getChildren().add(nameBox);
        v.getChildren().add(valueLabel);
        v.getChildren().add(listNames);
        v.getChildren().add(generateLabel);
        v.getChildren().add(generateList);
        final VBox subV = new VBox();
        Button okButton = new Button("Ok");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String code = formatCode(formatName);
                generateObj.setIteratorName(nameBox.getText());
                generateObj.setListName(listNames.getValue().toString());
                generateObservableList.add(generateObj);
                codeArea.appendText(code);
                stage.close();
            }
        });
        generateList.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                if (generateList.getSelectionModel().getSelectedItem().toString().equals("gListWhile")) {
                    subV.getChildren().removeAll();
                    Label condLabel = new Label("Condition");
                    TextField condField = new TextField();
                    condition = condField.getText();
                    subV.getChildren().add(condLabel);
                    subV.getChildren().add(condField);
                }
                if (generateList.getSelectionModel().getSelectedItem().toString().equals("gListUntil")) {
                    subV.getChildren().removeAll();
                    Label condLabel = new Label("Condition");
                    TextField condField = new TextField();
                    condition = condField.getText();
                    subV.getChildren().add(condLabel);
                    subV.getChildren().add(condField);
                }
                if (generateList.getSelectionModel().getSelectedItem().toString().equals("gFromToBy")) {
                    subV.getChildren().removeAll();
                    Label fromLabel = new Label("From");
                    TextField fromField = new TextField();
                    Label toLabel = new Label("To");
                    TextField toField = new TextField();
                    Label byLabel = new Label("By");
                    TextField byField = new TextField();
                    from = fromField.getText();
                    to = toField.getText();
                    by = byField.getText();
                    subV.getChildren().add(fromLabel);
                    subV.getChildren().add(fromField);
                    subV.getChildren().add(toLabel);
                    subV.getChildren().add(toField);
                    subV.getChildren().add(byLabel);
                    subV.getChildren().add(byField);
                }
            }
        });
        v.getChildren().add(subV);
        v.getChildren().add(okButton);
        Scene scene = new Scene(v);
        stage.setTitle("Generate");
        stage.setScene(scene);
        stage.show();
    }

    private String formatCode(String codeName) {
        String code = "";
        String[] codeParts = codeName.split(" ");
        for (String string : codeParts) {
            System.out.println("Code Parts : " + string);
            if ((string.startsWith("_") && string.endsWith("_")) || (string.startsWith("(") && string.endsWith(")"))) {
                if (string.contains("iterator")) {
                    string = string.replace(string, "( " + nameBox.getText() + " )");
                }
                if (string.contains("list")) {
                    string = string.replace(string, listNames.getValue().toString());
                }
            }
            code = code + " " + string;
        }
        return code;
    }

    private ObservableList<String> generateList() {
        List<String> generateList = new ArrayList<>();
        GuideConfig initGuide = new GuideConfig("src/edu/genesis/files/guide.cfg");
        Map map = initGuide.getMap();
        Iterator it = map.values().iterator();
        while (it.hasNext()) {
            ProgrammingElement pe = (ProgrammingElement) it.next();
            if (pe.getLabel().startsWith("g")) {
                generateList.add(pe.getLabel());
            }
        }
        ObservableList<String> generateObservableList = FXCollections.observableArrayList(generateList);
        return generateObservableList;
    }
}
