/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.genesis.animation;

import javafx.beans.property.StringProperty;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Laptop
 */
public class TextBox extends Group {
    private Text text;
    private Rectangle rectangle;
    private Rectangle clip;
    public StringProperty textProperty() { return text.textProperty(); }

    public TextBox(String string, String width, String height, double positionX, double positionY) {
      this.text = new Text(string);
      text.setTextAlignment(TextAlignment.CENTER);
      text.setFill(Color.BLACK);
      text.setTextOrigin(VPos.CENTER);
      text.setFont(Font.font("Comic Sans MS", 16));
      text.setFontSmoothingType(FontSmoothingType.LCD);

      this.rectangle = new Rectangle(positionX, positionY, new Double(width).doubleValue(), new Double(height).doubleValue());
      rectangle.setFill(Color.GREY);

      this.clip = new Rectangle(new Double(width).doubleValue(), new Double(height).doubleValue());
      text.setClip(clip);

      this.getChildren().addAll(rectangle, text);

    }

    @Override protected void layoutChildren() {
      final double w = rectangle.getWidth();
      final double h = rectangle.getHeight();
      clip.setWidth(w);
      clip.setHeight(h);
      clip.setLayoutX(0);
      clip.setLayoutY(-h/2);

      text.setWrappingWidth(w * 0.9);
      text.setLayoutX(w / 2 - text.getLayoutBounds().getWidth() / 2);
      text.setLayoutY(h / 2);
    }
  }
