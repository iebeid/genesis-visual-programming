package edu.genesis.utilities;

import edu.genesis.view.GenesisDevelopmentEnvironmentViewController;
import java.io.*;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GuideConfig extends GenesisDevelopmentEnvironmentViewController {

    Map<String, ProgrammingElement> map = null;
    BufferedReader br = null;
    FileReader fr = null;

    public Map getMap() {
        return map;
    }

    String readLine(BufferedReader fr) {
        String line = null;
        try {
            do {
                line = br.readLine();
            } while (line != null && line.equals(""));
        } catch (FileNotFoundException fex) {
        } catch (IOException ex) {
        }
        return line;
    }

    public GuideConfig(String file) {
        try {
            this.fr = new FileReader(file);
            this.br = new BufferedReader(fr);
            map = new LinkedHashMap<String, ProgrammingElement>();
            String label = null;
            String format = null;
            String toolTip = null;
            String action = null;
            String image = null;
            String className = null;
            int i = 0;
            while ((label = readLine(br)) != null) {

                label = clean(label);
                format = readLine(br);
                format = clean(format);
                format = addNewLines(format);
                toolTip = readLine(br);
                toolTip = clean(toolTip);
                action = readLine(br);
                action = clean(action);
                image = readLine(br);
                image = clean(image);
                className = readLine(br);
                className = clean(className);
                ProgrammingElement pe = new ProgrammingElement(label, toolTip, format, action, image, className);
                map.put(action, pe);
                i++;
            }
        } catch (FileNotFoundException fex) {
            final Stage stage = new Stage();
            Label nameLabel = new Label("File guide.cfg not found at edu/genesis/files");
            VBox v = new VBox();
            v.getChildren().add(nameLabel);
            Button okButton = new Button("Ok");
            okButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    stage.close();
                }
            });
            v.getChildren().add(okButton);
            Scene scene = new Scene(v);
            stage.setTitle("File not found");
            stage.setScene(scene);
            stage.show();
        }
    }

    protected String clean(String s) {
        s = s.trim();
        s = s.substring(s.indexOf("=") + 1);
        s = s.trim();
        return s;
    }

    protected String addNewLines(String s) {
        s = s.replaceAll("\\\\n", "\n");
        return s;
    }
}
