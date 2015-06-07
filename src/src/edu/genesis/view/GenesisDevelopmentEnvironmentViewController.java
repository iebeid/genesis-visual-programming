/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.view;

import edu.genesis.components.BlockDialog;
import edu.genesis.components.FunctionDialog;
import edu.genesis.components.GenerateDialog;
import edu.genesis.components.LetAliasDialog;
import edu.genesis.components.LetNameDialog;
import edu.genesis.components.ProcedureDialog;
import edu.genesis.components.ProjectTreeViewBuilder;
import edu.genesis.components.SelectDialog;
import edu.genesis.components.TaskDialog;
import edu.genesis.components.TreeMenuBuilder;
import edu.genesis.components.UnaliasDialog;
import edu.genesis.model.Generate;
import edu.genesis.model.StickyNote;
import edu.genesis.utilities.ButtonEventsHandler;
import edu.genesis.utilities.ProgrammingElement;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Laptop
 */
public class GenesisDevelopmentEnvironmentViewController implements Initializable {
    //Attributes

    public final BooleanProperty dragModeActiveProperty = new SimpleBooleanProperty(this, "dragModeActive", true);
    private ObservableList<TreeItem<ProgrammingElement>> items;
    private static Object temp;
    private static String actionName;
    private static String formatName;
    private static List<ImageView> list = new ArrayList<>();
    private Delta dragDelta = new Delta();
    private Group boardGroup = new Group();
    public ProgrammingElement peObj;
    private static List<String> codeList = new ArrayList<>();
    private static ObservableList<ImageView> observableList = FXCollections.observableList(list);
    private static ObservableList<String> codeObservableList = FXCollections.observableList(codeList);
    private static List<StickyNote> variableList = new ArrayList<>();
    public static ObservableList<StickyNote> variableObservableList = FXCollections.observableList(variableList);
    private static List<Generate> generateList = new ArrayList<>();
    public static ObservableList<Generate> generateObservableList = FXCollections.observableList(generateList);
    @FXML
    protected static Label playLabel;
    @FXML
    private Button newButton;
    @FXML
    private Button play;
    @FXML
    private Button clear;
    @FXML
    private Button save;
    @FXML
    private Button run;
    @FXML
    private Button load;
    @FXML
    private MenuItem newMenu;
    @FXML
    private MenuItem closeMenu;
    @FXML
    private MenuItem deleteMenu;
    @FXML
    private MenuItem playMenu;
    @FXML
    private MenuItem clearMenu;
    @FXML
    private MenuItem saveMenu;
    @FXML
    private MenuItem runMenu;
    @FXML
    private MenuItem loadMenu;
    @FXML
    private MenuItem generateMenu;
    @FXML
    private MenuItem aboutMenu;
    @FXML
    protected static TextArea codeArea;
    @FXML
    public static TextArea outputArea;
    @FXML
    public static TreeView controls;
    @FXML
    public static TreeView project;
    @FXML
    public static ImageView sticky;
    @FXML
    public static ImageView loopIcon;
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public static Pane targetBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TreeMenuBuilder treeMenu = new TreeMenuBuilder();
        ProjectTreeViewBuilder projectTree = new ProjectTreeViewBuilder();
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == play || event.getSource() == playMenu) {
            new ButtonEventsHandler().playButton(event);
        }
        if (event.getSource() == clear || event.getSource() == clearMenu) {
            ButtonEventsHandler.clearButton(event);
        }
        if (event.getSource() == save || event.getSource() == saveMenu) {
            ButtonEventsHandler.saveButton(event);
        }
        if (event.getSource() == run || event.getSource() == runMenu) {
            ButtonEventsHandler.runButton(event);
        }
        if (event.getSource() == load || event.getSource() == loadMenu) {
            ButtonEventsHandler.loadButton(event);
        }
        if (event.getSource() == newButton || event.getSource() == newMenu) {
            ButtonEventsHandler.newButtonHandler(event);
        }
        if (event.getSource() == closeMenu) {
            System.exit(0);
        }
        if (event.getSource() == deleteMenu) {
        }
        if (event.getSource() == generateMenu) {
        }
        if (event.getSource() == aboutMenu) {
            final Stage stage = new Stage();
            Label label = new Label("Genesis Development Environment\n Created by Islam A. Ebeid\n"
                    + "Under the supervision of Dr. Larry Morell\n Arkansas Tech University\n Spring 2013");
            Button okButton = new Button("Ok");
            okButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    stage.close();
                }
            });
            VBox vbox = new VBox();
            vbox.getChildren().add(label);
            vbox.getChildren().add(okButton);
            Scene scene = new Scene(vbox);
            stage.setTitle("About");
            stage.setScene(scene);
            stage.show();
        }

    }

    @FXML
    private void onDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasImage()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
        event.consume();
    }

    @FXML
    private void onDragDropped(DragEvent event) {
        observableList.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                        targetBox.getChildren().removeAll(observableList);
                        targetBox.getChildren().addAll(observableList);
            }
        });
        Dragboard db = event.getDragboard();
        Image image = db.getImage();
        ImageView iv = new ImageView();
        iv.setImage(image);
        final ImageView imageNode = (ImageView) makeDraggable(iv);
        observableList.add(imageNode);
        targetBox.getChildren().removeAll(observableList);
        targetBox.getChildren().addAll(observableList);
        event.setDropCompleted(true);
        event.consume();
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        dragDelta.x = targetBox.getTranslateX() - mouseEvent.getScreenX();
        dragDelta.y = targetBox.getTranslateY() - mouseEvent.getScreenY();
        targetBox.setCursor(Cursor.MOVE);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        targetBox.setCursor(Cursor.HAND);
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        targetBox.setTranslateX(mouseEvent.getScreenX() + dragDelta.x);
        targetBox.setTranslateY(mouseEvent.getScreenY() + dragDelta.y);
    }

    public void onMouseEntered(MouseEvent mouseEvent) {
        if (!mouseEvent.isPrimaryButtonDown()) {
            targetBox.setCursor(Cursor.HAND);
        }
    }

    public void onMouseExited(MouseEvent mouseEvent) {
        if (!mouseEvent.isPrimaryButtonDown()) {
            targetBox.setCursor(Cursor.DEFAULT);
        }
    }
    //Tree View

    public void handleTreeMenuOnDragDetected(MouseEvent event) {
        Dragboard db = controls.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        items = controls.getSelectionModel().getSelectedItems();
        for (TreeItem<ProgrammingElement> treeItem : items) {
            content.putImage(new Image(ClassLoader.getSystemResourceAsStream(treeItem.getValue().getImage())));
            actionName = treeItem.getValue().getAction();
            formatName = treeItem.getValue().getFormat();
        }
        db.setContent(content);
        event.consume();
    }

    public void handleTreeMenuOnDragDone(DragEvent event) {
        if (actionName.equals("1")) {
            LetNameDialog dialog = new LetNameDialog(formatName);
        }
        if (actionName.equals("2")) {
            LetAliasDialog dialog = new LetAliasDialog(formatName);
        }
        if (actionName.equals("3")) {
            UnaliasDialog dialog = new UnaliasDialog(formatName);
        }
        if (actionName.equals("4")) {
            SelectDialog dialog = new SelectDialog(formatName);
        }
        if (actionName.equals("5")) {
            GenerateDialog dialog = new GenerateDialog(formatName);
        }
        if (actionName.equals("9")) {
            TaskDialog dialog = new TaskDialog(formatName);
        }
        if (actionName.equals("17")) {
            BlockDialog dialog = new BlockDialog(formatName);
        }
        if (actionName.equals("18")) {
            codeArea.appendText("\n" + formatName);
        }
        if (actionName.equals("19")) {
            FunctionDialog dialog = new FunctionDialog(formatName);
        }
        if (actionName.equals("20")) {
            ProcedureDialog dialog = new ProcedureDialog(formatName);
        }
        event.consume();
    }

    public void handleTreeMenuOnDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasString()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private Node makeDraggable(final Node node) {
        final DragContext dragContext = new DragContext();
        node.addEventFilter(
                MouseEvent.ANY,
                new EventHandler<MouseEvent>() {
                    public void handle(final MouseEvent mouseEvent) {
                        if (dragModeActiveProperty.get()) {
                            mouseEvent.consume();
                        }
                    }
                });

        node.addEventFilter(
                MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
                    public void handle(final MouseEvent mouseEvent) {
                        if (dragModeActiveProperty.get()) {
                            dragContext.mouseAnchorX = targetBox.getTranslateX() - mouseEvent.getScreenX();
                            dragContext.mouseAnchorY = targetBox.getTranslateY() - mouseEvent.getScreenY();
                            targetBox.setCursor(Cursor.MOVE);
                            dragContext.initialTranslateX =
                                    node.getTranslateX();
                            dragContext.initialTranslateY =
                                    node.getTranslateY();
                        }
                        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            System.out.println("Inside context menu");
                            ContextMenu menu = new ContextMenu();
                            MenuItem item1 = new MenuItem("Delete", 
                                    new ImageView(new Image("edu/genesis/assets/redx.png")));
                            item1.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent t) {
                                    node.setVisible(false);
                                    node.setDisable(true);
                                    observableList.remove(node);
                                }
                            });
                            menu.getItems().add(item1);
                            menu.show(node,Side.RIGHT,0,0);
                        }
                    }
                });

        node.addEventFilter(
                MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>() {
                    public void handle(final MouseEvent mouseEvent) {
                        if (dragModeActiveProperty.get()) {
                            //targetBox.setTranslateX(mouseEvent.getScreenX() + dragContext.mouseAnchorX);
                            //targetBox.setTranslateY(mouseEvent.getScreenY() + dragContext.mouseAnchorY);
//                              node.setTranslateX(
//                                    dragContext.initialTranslateX
//                                    + mouseEvent.getX()
//                                    - dragContext.mouseAnchorX);
//                            node.setTranslateY(
//                                    dragContext.initialTranslateY
//                                    + mouseEvent.getY()
//                                    - dragContext.mouseAnchorY);
                            node.setTranslateX(
                                    dragContext.initialTranslateX
                                    + mouseEvent.getX() - targetBox.getTranslateX());
                            node.setTranslateY(
                                    dragContext.initialTranslateY
                                    + mouseEvent.getY() - targetBox.getTranslateY());

                        }
                    }
                });
        return node;
    }

    private static final class DragContext {

        public double mouseAnchorX;
        public double mouseAnchorY;
        public double initialTranslateX;
        public double initialTranslateY;
    }

    private class Delta {

        public double x, y;
    }
}
