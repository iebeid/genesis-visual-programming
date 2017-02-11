/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.components;

import edu.genesis.view.GenesisDevelopmentEnvironmentViewController;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Laptop
 */
public class ProjectTreeViewBuilder extends GenesisDevelopmentEnvironmentViewController {
    public ProjectTreeViewBuilder() {
        String hostName = "AKEF-PC";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException x) {
        }
        TreeItem<String> treeRoot = new TreeItem<>(hostName, new ImageView(new Image(ClassLoader.getSystemResourceAsStream("edu/genesis/assets/computer.png"))));
        Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();
        for (Path name : rootDirectories) {
            FilePathTreeItem treeNode = new FilePathTreeItem(name);
            treeRoot.getChildren().add(treeNode);
        }
        //project.setRoot(treeRoot);
        //project.setShowRoot(true);
        treeRoot.setExpanded(true);
    }
}
