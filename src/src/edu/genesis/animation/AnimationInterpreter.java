/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.animation;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Islam A. Ebeid
 * This file parses the output.xml file and according to the tags and the elements in the file
 * it generates the graphical objects accordingly
 */
public class AnimationInterpreter {

    protected static NodeList defineList;
    protected static NodeList displayList;
    protected static NodeList moveList;
    protected static NodeList destroyList;
    protected static NodeList hideList;
    protected static LinkedHashMap<String, String> xmlMap = new LinkedHashMap<>();

    public AnimationInterpreter(File file) {
        try {
            populateNodeLists(file);
            startAnimation();
        } catch (ParserConfigurationException | SAXException | IOException e) {
        }
    }

    private static void populateNodeLists(File file) throws ParserConfigurationException, SAXException, IOException {
        File fXmlFile = new File(file.getPath());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        if ("world".equals(doc.getDocumentElement().getNodeName())) {
            xmlMap.put(doc.getDocumentElement().getNodeName(), "root");
            defineList = doc.getElementsByTagName("define");
            for (int temp = 0; temp < defineList.getLength(); temp++) {
                Node nNode = defineList.item(temp);
                if ("define".equals(nNode.getNodeName())) {
                    xmlMap.put(nNode.getNodeName() + " " + temp, "parent");
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String nameStr = eElement.getElementsByTagName("name").item(0).getTextContent();
                        xmlMap.put("name" + " " + temp, nameStr);
                        String typeStr = eElement.getElementsByTagName("type").item(0).getTextContent();
                        xmlMap.put("type" + " " + temp, typeStr);
                        String outlineStr = eElement.getElementsByTagName("outline").item(0).getTextContent();
                        xmlMap.put("outline" + " " + temp, outlineStr);
                        String locationStr = "(" + eElement.getElementsByTagName("location").item(0).
                                getAttributes().getNamedItem("x").getNodeValue() + ","
                                + eElement.getElementsByTagName("location").item(0).getAttributes().
                                getNamedItem("y").getNodeValue() + ")";
                        xmlMap.put("location" + " " + temp, locationStr);
                        String valueStr = eElement.getElementsByTagName("value").item(0).getTextContent();
                        xmlMap.put("value" + " " + temp, valueStr);
                    }
                }
            }
            displayList = doc.getElementsByTagName("display");
            for (int temp = 0; temp < displayList.getLength(); temp++) {
                Node nNode = displayList.item(temp);
                if ("display".equals(nNode.getNodeName())) {
                    xmlMap.put(nNode.getNodeName() + " " + temp, nNode.getTextContent());
                }
            }
            moveList = doc.getElementsByTagName("move");
            for (int temp = 0; temp < moveList.getLength(); temp++) {
                Node nNode = moveList.item(temp);
                if ("move".equals(nNode.getNodeName())) {
                    xmlMap.put(nNode.getNodeName() + " " + temp, nNode.getNodeValue());
                }
            }
            destroyList = doc.getElementsByTagName("destroy");
            for (int temp = 0; temp < destroyList.getLength(); temp++) {
                Node nNode = destroyList.item(temp);
                if ("destroy".equals(nNode.getNodeName())) {
                    xmlMap.put(nNode.getNodeName() + " " + temp, nNode.getNodeValue());
                }
            }
            hideList = doc.getElementsByTagName("hide");
            for (int temp = 0; temp < hideList.getLength(); temp++) {
                Node nNode = hideList.item(temp);
                if ("hide".equals(nNode.getNodeName())) {
                    xmlMap.put(nNode.getNodeName() + " " + temp, nNode.getNodeValue());
                }
            }
        }
    }

    private static void populateGraphicalObjects(final Group group) {
        Set set = xmlMap.entrySet();
        Iterator i = set.iterator();
        TextBox box = null;
        double count = 0;
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            if(me.getKey().toString().contains("define")){
                box = new TextBox(me.getValue().toString(), "10", "10", count , count);
                group.getChildren().add(box);
                count = count + 20;
            }
        }
        
        final Path path = new Path();
        path.getElements().add(new MoveTo(20, 20));
        path.getElements().add(new HLineTo(80));
        path.setOpacity(0);
        final PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(8.0));
        pathTransition.setDelay(Duration.seconds(2.0));
        pathTransition.setPath(path);
        pathTransition.setNode(box);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(Timeline.INDEFINITE);
        pathTransition.setAutoReverse(true);
        group.getChildren().add(path);
        pathTransition.play();

    }

    private static void startAnimation() {
        Stage stage = new Stage();
        final Group rootGroup = new Group();
        final Scene scene = new Scene(rootGroup, 600, 400, Color.GHOSTWHITE);
        stage.setScene(scene);
        stage.setTitle("Animation Ouput");
        stage.show();
        populateGraphicalObjects(rootGroup);
    }
}
