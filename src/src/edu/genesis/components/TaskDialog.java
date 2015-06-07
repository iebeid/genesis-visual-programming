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
import java.util.Arrays;
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
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Laptop
 */
public class TaskDialog extends GenesisDevelopmentEnvironmentViewController {

    final ComboBox task;
    final ComboBox iteratorList;
    final TextField condition;
    final TextField firstBox;
    final TextField iteratorBox;
    final TextField lastBox;
    String conditionString = "";
    String first = "";
    String iteration = "";
    String last = "";
    String taskName = "";
    String listVariableName = "";
    String iterator = "";
    Generate genObj = new Generate();

    public TaskDialog(final String formatName) {
        final Stage stage = new Stage();
        Label listLabel = new Label("List Name");
        iteratorList = new ComboBox();
        List<String> iteratorListName = new ArrayList<>();
        for (Generate gen : generateObservableList) {
            genObj = gen;
            iteratorListName.add(gen.getIteratorName());
        }
        ObservableList<String> iteratorListNameOL = FXCollections.observableArrayList(iteratorListName);
        iteratorList.setItems(iteratorListNameOL);
        Label taskLabel = new Label("Task");
        task = new ComboBox();
        task.setItems(this.taskList());
        Label conditionLabel = new Label("Condition");
        condition = new TextField();
        Label firstValue = new Label("First Value");
        firstBox = new TextField();
        Label iteratorValue = new Label("Iterator Value");
        iteratorBox = new TextField();
        Label lastValue = new Label("Last Value");
        lastBox = new TextField();
        Button okButton = new Button("Ok");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                listVariableName = iteratorList.getSelectionModel().toString();
                String code = formatCode(formatName);
                codeArea.appendText(code);
                stage.close();
            }
        });
        iteratorList.valueProperty().addListener(new ChangeListener<String>() { 

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                iterator = t1;
            }
        
        });
        task.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                if (task.getSelectionModel().getSelectedItem().toString().equals("tPrint")) {
                    conditionString="";
            iteration = "Print " + iterator;
        }
        if (task.getSelectionModel().getSelectedItem().toString().equals("tCount")) {
            first = "Let count name 0";
            iteration = "Let count name count + 1";
        }
        if (task.getSelectionModel().getSelectedItem().toString().equals("tModify")) {
            iteration = "Let " + iterator + " name _expression_ ";
        }
        if (task.getSelectionModel().getSelectedItem().toString().equals("tSum")) {
            first = "Let var name 0";
            iteration = "Let var name var  + _expression_";
        }
        if (task.getSelectionModel().getSelectedItem().toString().equals("tSmallest")) {
            conditionString = "when ( iterator < smallest)";
            first = "Let smallest name _First Item of the List_";
            iteration = "Let smallest name iterator ";
        }
        if (task.getSelectionModel().getSelectedItem().toString().equals("tLargest")) {
            conditionString = "when ( iterator > smallest)";
            first = "Let smallest name _First Item of the List_";
            iteration = "Let smallest name iterator ";
        }
        if (task.getSelectionModel().getSelectedItem().toString().equals("tBuild")) {
            first = "Let newList name <>";
            iterator = "Append iterator onto newList";
        }
                taskName = t1;
                condition.appendText(conditionString);
                firstBox.appendText(first);
                iteratorBox.appendText(iteration);
                lastBox.appendText(last);
            }
        });
        VBox v = new VBox();
        v.getChildren().add(listLabel);
        v.getChildren().add(iteratorList);
        v.getChildren().add(taskLabel);
        v.getChildren().add(task);
        v.getChildren().add(conditionLabel);
        v.getChildren().add(condition);
        v.getChildren().add(firstValue);
        v.getChildren().add(firstBox);
        v.getChildren().add(iteratorValue);
        v.getChildren().add(iteratorBox);
        v.getChildren().add(lastValue);
        v.getChildren().add(lastBox);
        v.getChildren().add(okButton);

        Scene scene = new Scene(v);
        stage.setTitle("Task");
        stage.setScene(scene);
        stage.show();
    }

    private String formatCode(String codeName) {
        
        String code = "";
        String[] codeParts = codeName.split(" ");
        for (String string : codeParts) {
            System.out.println("Code Parts : " + string);
            if (string.startsWith("_") && string.endsWith("_")) {
                if (string.contains("condition")) {
                    string = string.replace(string, condition.getText());
                }
                if (string.contains("first")) {
                    string = string.replace(string, firstBox.getText());
                }
                if (string.contains("iteration")) {
                    string = string.replace(string, iteratorBox.getText());
                }
                if (string.contains("last")) {
                    string = string.replace(string, lastBox.getText());
                }
            }
            code = code + " " + string;
        }
        return code;
    }

    private ObservableList<String> taskList() {
        List<String> taskList = new ArrayList<>();

        GuideConfig initGuide = new GuideConfig("src/edu/genesis/files/guide.cfg");
        Map map = initGuide.getMap();
        Iterator it = map.values().iterator();
        while (it.hasNext()) {
            ProgrammingElement pe = (ProgrammingElement) it.next();
            if (pe.getLabel().startsWith("t")) {
                taskList.add(pe.getLabel());
            }
        }
        ObservableList<String> taskObservableList = FXCollections.observableArrayList(taskList);
        return taskObservableList;
    }
}
