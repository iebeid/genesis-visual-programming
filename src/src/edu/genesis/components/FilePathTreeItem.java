/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.components;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 *
 * @author Laptop
 */
public class FilePathTreeItem extends TreeItem<String>{
    public static Image folderCollapseImage=new Image(ClassLoader.getSystemResourceAsStream("edu/genesis/assets/folder.png"));
  public static Image folderExpandImage=new Image(ClassLoader.getSystemResourceAsStream("edu/genesis/assets/folder-open.png"));
  public static Image fileImage=new Image(ClassLoader.getSystemResourceAsStream("edu/genesis/assets/text-x-generic.png"));
  
  private String fullPath;
  public String getFullPath(){return(this.fullPath);}
  
  private boolean isDirectory;
  public boolean isDirectory(){return(this.isDirectory);}
    
  public FilePathTreeItem(Path file){
    super(file.toString());
    this.fullPath=file.toString();
    if(Files.isDirectory(file)){
      this.isDirectory=true;
      this.setGraphic(new ImageView(folderCollapseImage));
    }else{
      this.isDirectory=false;
      this.setGraphic(new ImageView(fileImage));
    }
    
    if(!fullPath.endsWith(File.separator)){
      String value=file.toString();
      int indexOf=value.lastIndexOf(File.separator);
      if(indexOf>0){
        this.setValue(value.substring(indexOf+1));
      }else{
        this.setValue(value);
      }
    }
    this.addEventHandler(TreeItem.branchExpandedEvent(),new EventHandler(){
      @Override
      public void handle(Event e){
        FilePathTreeItem source=(FilePathTreeItem)e.getSource();
        if(source.isDirectory()&&source.isExpanded()){
          ImageView iv=(ImageView)source.getGraphic();
          iv.setImage(folderExpandImage);
        }
        try{
          if(source.getChildren().isEmpty()){
            Path path=Paths.get(source.getFullPath());
            BasicFileAttributes attribs=Files.readAttributes(path,BasicFileAttributes.class);
            if(attribs.isDirectory()){
              DirectoryStream<Path> dir=Files.newDirectoryStream(path);
              for(Path file:dir){
                FilePathTreeItem treeNode=new FilePathTreeItem(file);
                source.getChildren().add(treeNode);
              }
            }
          }else{
          }
        }catch(IOException x){
          x.printStackTrace();
        }
      }

    });
     this.addEventHandler(TreeItem.branchCollapsedEvent(),new EventHandler(){
      @Override
      public void handle(Event e){
        FilePathTreeItem source=(FilePathTreeItem)e.getSource();
        if(source.isDirectory()&&!source.isExpanded()){
          ImageView iv=(ImageView)source.getGraphic();
          iv.setImage(folderCollapseImage);
        }
      }
    });
}
}
