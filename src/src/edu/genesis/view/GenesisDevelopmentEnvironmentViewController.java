/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.view;

import edu.genesis.animation.AnimationInterpreter;
import edu.genesis.components.BlockDialog;
import edu.genesis.components.FunctionDialog;
import edu.genesis.components.GenerateDialog;
import edu.genesis.components.LetAliasDialog;

import edu.genesis.components.ProcedureDialog;
import edu.genesis.components.SelectDialog;
import edu.genesis.components.TaskDialog;
import edu.genesis.components.UnaliasDialog;
import edu.genesis.model.Generate;
import edu.genesis.model.StickyNote;
import edu.genesis.runtime.GenesisInterpreter;
import edu.genesis.utilities.GuideConfig;
import edu.genesis.utilities.ProgrammingElement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TextField;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Laptop
 */
public class GenesisDevelopmentEnvironmentViewController implements Initializable {
    //Attributes


    @FXML
    private Label playLabel;
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
    public TextArea codeArea;
    @FXML
    public TextArea outputArea;
    @FXML
    private TreeView controls;
    @FXML
    public TreeView project;
    @FXML
    public ImageView sticky;
    @FXML
    public ImageView loopIcon;
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public Pane targetBox;
    
    
        public BooleanProperty dragModeActiveProperty;
    private ObservableList<TreeItem<ProgrammingElement>> items;
    private static Object temp;
    private static String actionName;
    private static String formatName;
    private static List<ImageView> list;
    private Delta dragDelta;
    private Group boardGroup;
    public ProgrammingElement peObj;
    private static List<String> codeList;
    private static ObservableList<ImageView> observableList;
    private static ObservableList<String> codeObservableList;
    private static List<StickyNote> variableList;
    public static ObservableList<StickyNote> variableObservableList;
    private static List<Generate> generateList;
    public static ObservableList<Generate> generateObservableList;
    

    public void initializeVariables(){
        dragModeActiveProperty = new SimpleBooleanProperty(this, "dragModeActive", true);
        list = new ArrayList<>();
        dragDelta = new Delta();
        boardGroup = new Group();
        codeList = new ArrayList<>();
        observableList = FXCollections.observableList(list);
        codeObservableList = FXCollections.observableList(codeList);
        variableList = new ArrayList<>();
        variableObservableList = FXCollections.observableList(variableList);
        generateList = new ArrayList<>();
        generateObservableList = FXCollections.observableList(generateList);
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
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeVariables();
        codeArea.setVisible(true);
        codeArea.setDisable(false);
        //codeArea.setText("Hello Code Area!");
        //codeArea.appendText("Code Line: ");
//        TreeMenuBuilder treeMenu = new TreeMenuBuilder();
//        ProjectTreeViewBuilder projectTree = new ProjectTreeViewBuilder();
        GuideConfig initGuide = new GuideConfig("src/edu/genesis/files/guide.cfg");
        //GuideConfig initGuide = new GuideConfig("D:\\Projects\\ArkansasTechUniversity\\Thesis\\Sources\\GithubProjects\\genesis-visual-programming\\src\\src\\edu\\genesis\\files");
        Map map = initGuide.getMap();
        Iterator it = map.values().iterator();
        final TreeItem<ProgrammingElement> treeRoot;
        treeRoot = new TreeItem<>();
        
        //controls.setEditable(true);

        while (it.hasNext()) {
            ProgrammingElement pe = (ProgrammingElement) it.next();
            if(!pe.getLabel().startsWith("t")){
                if(!pe.getLabel().startsWith("g")){
                    treeRoot.getChildren().addAll(Arrays.asList(createNode(pe)));
                }
            }
        }
        
        controls.setEditable(true);
        controls.setShowRoot(true);
        controls.setRoot(treeRoot);
        treeRoot.setExpanded(true);
    }
    
        public File saveButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GEN files (*.txt)", "*.txt");
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
        public void playButton(ActionEvent event) {
        List<String> argsList = new ArrayList<>();
        argsList.add("-x");
        File file = saveButton(event);
        argsList.add(file.getAbsolutePath());
        String[] args = new String[argsList.size()];
        for(int i = 0 ; i < args.length ; i++){
            args[i] = argsList.get(i);
        }
        GenesisInterpreter.callGenesisXML(args);
        File outputFile = new File("src/edu/genesis/files/output.xml");
        AnimationInterpreter ai = new AnimationInterpreter(outputFile);
    }
        
            public void clearButton(ActionEvent event) {
        targetBox.getChildren().removeAll(targetBox.getChildren());
        codeArea.clear();
    }
            
                public void runButton(ActionEvent event) {
        List<String> argsList = new ArrayList<>();
        argsList.add("-d");
        File file = this.saveButton(event);
        argsList.add(file.getAbsolutePath());
        String[] args = new String[argsList.size()];
        for(int i = 0 ; i < args.length ; i++){
            args[i] = argsList.get(i);
        }
        GenesisInterpreter.callGenesis(args);
        
        InputStream is = null; 
        try {
            is = new FileInputStream("A.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenesisDevelopmentEnvironmentViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader buf = new BufferedReader(new InputStreamReader(is)); 
        String line = null; 
        try {
            line = buf.readLine();
        } catch (IOException ex) {
            Logger.getLogger(GenesisDevelopmentEnvironmentViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        StringBuilder sb = new StringBuilder(); 
        while(line != null){ sb.append(line).append("\n"); try {
            line = buf.readLine();
            } catch (IOException ex) {
                Logger.getLogger(GenesisDevelopmentEnvironmentViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
} 
        String fileAsString = sb.toString();
        
        outputArea.appendText(fileAsString);


    }
                
                   public void loadButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GEN files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File input = fileChooser.showOpenDialog(null);
        try {
            FileReader fr = new FileReader(input);
            codeArea.appendText(fr.toString() + "\n");
            fr.close();
        } catch (Exception e) {
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
                    //Logger.getLogger(ButtonEventsHandler.class.getName()).log(Level.SEVERE, null, ex);
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
                       
    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == play || event.getSource() == playMenu) {
            //new ButtonEventsHandler().playButton(event);
            playButton(event);
        }
        if (event.getSource() == clear || event.getSource() == clearMenu) {
            clearButton(event);
        }
        if (event.getSource() == save || event.getSource() == saveMenu) {
            saveButton(event);
        }
        if (event.getSource() == run || event.getSource() == runMenu) {
            runButton(event);
        }
        if (event.getSource() == load || event.getSource() == loadMenu) {
            loadButton(event);
        }
        if (event.getSource() == newButton || event.getSource() == newMenu) {
            newButtonHandler(event);
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
    TextField nameBox;
    TextField valueBox;
    public void handleTreeMenuOnDragDone(DragEvent event) {
        if (actionName.equals("1")) {
//            LetNameDialog dialog = new LetNameDialog(formatName);
//            ArrayList<String> generatedCode = dialog.getGeneratedCode();
//            System.out.println("Code Generated");
//            for(String codeLine:generatedCode){
//                System.out.println("Code Line: " + codeLine);
//                codeArea.appendText("Code Line: " + codeLine);
//            }


                    
        final Stage stage = new Stage();
        Label nameLabel = new Label("Name");
        nameBox = new TextField();
        Label valueLabel = new Label("Value");
        valueBox = new TextField();
        Button okButton = new Button("Ok");
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                
                StickyNote sn = new StickyNote();
                sn.setName(nameBox.getText());
                sn.setValue(valueBox.getText());
                variableObservableList.add(sn);
                //String code = formatCode(formatName);
                
                        String code = "";
        String[] codeParts = formatName.split(" ");
        for (String string : codeParts) {
            System.out.println("Code Parts : " + string);
            if (string.startsWith("_") && string.endsWith("_")) {
                if (string.contains("StickyNote")) {
                    string = string.replace(string, nameBox.getText());
                }
                if (string.contains("expression")) {
                    string = string.replace(string, valueBox.getText());
                }
            }
            code = code + " " + string;
        }
                
                
                codeArea.appendText(code);
                //generatedCode.add(code);
                stage.close();
            }
        });
        VBox v = new VBox();
        v.getChildren().add(nameLabel);
        v.getChildren().add(nameBox);
        v.getChildren().add(valueLabel);
        v.getChildren().add(valueBox);
        v.getChildren().add(okButton);

        Scene scene = new Scene(v);
        stage.setTitle("StickyNote/List");
        stage.setScene(scene);
        stage.show();
        
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
