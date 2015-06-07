/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.view;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
public class GenesisDevelopmentEnvironment extends Application {

    String title;
    String icon;
    

    @Override
    public void start(Stage stage) throws Exception {
        this.initProperties();
        Parent root = FXMLLoader.load(getClass().getResource("GenesisDevelopmentEnvironmentView.fxml"));
        stage.setTitle(title);
        stage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream(icon)));
        Undecorator undecorator;
        undecorator = new Undecorator(stage, (Region) root);
        undecorator.getStylesheets().add("edu/genesis/skin/undecorator.css");
        Scene scene = new Scene(undecorator);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setWidth(640);
        stage.setHeight(480);
        stage.show();
    }

    private void initProperties() {
        try {
            File fXmlFile = new File("src/edu/genesis/files/properties.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            title = doc.getElementsByTagName("title").item(0).getTextContent();
            icon = doc.getElementsByTagName("icon").item(0).getTextContent();
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
