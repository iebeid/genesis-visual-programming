/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.components;

import edu.genesis.utilities.ProgrammingElement;
import edu.genesis.utilities.GuideConfig;
import edu.genesis.view.GenesisDevelopmentEnvironmentViewController;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Laptop
 */
public class TreeMenuBuilder extends GenesisDevelopmentEnvironmentViewController {

    public TreeMenuBuilder() {
        GuideConfig initGuide = new GuideConfig("src/edu/genesis/files/guide.cfg");
        Map map = initGuide.getMap();
        Iterator it = map.values().iterator();
        final TreeItem<ProgrammingElement> treeRoot;
        treeRoot = new TreeItem<>();
        controls.setEditable(true);

        while (it.hasNext()) {
            ProgrammingElement pe = (ProgrammingElement) it.next();
            if(!pe.getLabel().startsWith("t")){
                if(!pe.getLabel().startsWith("g")){
                    treeRoot.getChildren().addAll(Arrays.asList(createNode(pe)));
                }
            }
        }
        
        controls.setShowRoot(true);
        controls.setRoot(treeRoot);
        treeRoot.setExpanded(true);
    }

    private TreeItem<ProgrammingElement> createNode(final ProgrammingElement pe) {
        return new TreeItem<ProgrammingElement>(pe) {
            @Override
            public ObservableList<TreeItem<ProgrammingElement>> getChildren() {
                    super.getChildren().setAll(buildChildren(this));
                return super.getChildren();
            }
            private ObservableList<TreeItem<ProgrammingElement>> buildChildren(TreeItem<ProgrammingElement> TreeItem) {
                ProgrammingElement f = TreeItem.getValue();
                if (f != null) {
                  String labelName = f.getLabel();
                  if (labelName != null) {
                      ObservableList<TreeItem<ProgrammingElement>> children = FXCollections.observableArrayList();
                      children.add(createNode(f));
                      return children;
                  }
                }
                return FXCollections.emptyObservableList();
            }
        };
    }
}
