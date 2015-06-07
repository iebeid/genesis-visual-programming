/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.utilities;

import edu.genesis.animation.AnimationInterpreter;
import edu.genesis.components.ProjectTreeViewBuilder;
import edu.genesis.runtime.GenesisInterpreter;
import edu.genesis.view.GenesisDevelopmentEnvironmentViewController;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Laptop
 */
public class ButtonEventsHandler extends GenesisDevelopmentEnvironmentViewController {

    String working;
    private void initProperties() {
        try {
            File fXmlFile = new File("src/edu/genesis/files/properties.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            working = doc.getElementsByTagName("working").item(0).getTextContent();
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
        }
    }
    public static void newButtonHandler(ActionEvent event) {
        final Stage stage = new Stage();
        Label projectLabel = new Label("Project Name");
        final TextField projectName = new TextField();
        Button okButton = new Button("Ok");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                File dir = new File(projectName.getText());
                dir.mkdir();
                File src = new File(dir,"source");
                src.mkdir();
                File packageFolder = new File(dir,"package");
                packageFolder.mkdir();
                File animation = new File(dir,"animation");
                animation.mkdir();
                File config = new File(dir,"configuration");
                config.mkdir();
                 try {
                    dir.createNewFile();
                    src.createNewFile();
                    packageFolder.createNewFile();
                    animation.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(ButtonEventsHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                stage.close();
            }
        });
        VBox v = new VBox();
        v.getChildren().add(projectLabel);
        v.getChildren().add(projectName);
        v.getChildren().add(okButton);
        Scene scene = new Scene(v);
        stage.setTitle("New Project");
        stage.setScene(scene);
        stage.show();
    }
    
    public void playButton(ActionEvent event) {
        List<String> argsList = new ArrayList<>();
        argsList.add("-x");
        File file = ButtonEventsHandler.saveButton(event);
        argsList.add(file.getAbsolutePath());
        String[] args = new String[argsList.size()];
        for(int i = 0 ; i < args.length ; i++){
            args[i] = argsList.get(i);
        }
        GenesisInterpreter.callGenesisXML(args);
        File outputFile = new File("src/edu/genesis/files/output.xml");
        AnimationInterpreter ai = new AnimationInterpreter(outputFile);
    }

    public static void loadButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GEN files (*.gen)", "*.gen");
        fileChooser.getExtensionFilters().add(extFilter);
        File input = fileChooser.showOpenDialog(null);
        try {
            FileReader fr = new FileReader(input);
            codeArea.appendText(fr.toString() + "\n");
            fr.close();
        } catch (Exception e) {
        }
    }

    public static void clearButton(ActionEvent event) {
        targetBox.getChildren().removeAll(targetBox.getChildren());
        codeArea.clear();
    }

    public static File saveButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GEN files (*.gen)", "*.gen");
        fileChooser.getExtensionFilters().add(extFilter);
        File input = fileChooser.showSaveDialog(null);
        String codeAreaText = codeArea.getText();
        if (input != null) {
            try {
                FileWriter fileWriter = null;
                fileWriter = new FileWriter(input);
                fileWriter.write(codeAreaText);
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(codeAreaText).log(Level.SEVERE, null, ex);
            }
        }
        return input;
    }

    public static void runButton(ActionEvent event) {
        List<String> argsList = new ArrayList<>();
        argsList.add("-d");
        File file = ButtonEventsHandler.saveButton(event);
        argsList.add(file.getAbsolutePath());
        String[] args = new String[argsList.size()];
        for(int i = 0 ; i < args.length ; i++){
            args[i] = argsList.get(i);
        }
        GenesisInterpreter.callGenesis(args);
    }
}
