/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Mark Bernard
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in the 
 * Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.markbernard.jnotepadfx.dialog;

import io.github.markbernard.jnotepadfx.ApplicationPreferences;
import io.github.markbernard.jnotepadfx.IconCache;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * @author Mark Bernard
 *
 */
public class FontDialog extends Dialog<ButtonType> {
    private static final String[] STYLES = {"Regular", "Bold", "Italic", "Bold Italic"};
    private static final String[] SIZES = {"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"};

    private Window window;
    private String fontFamily;
    private boolean bold;
    private boolean italic;
    private double size;
    private Font selectedFont;

    private TextField familyField;
    private TextField styleField;
    private TextField sizeField;
    private ListView<String> familyList;
    private ListView<String> styleList;
    private ListView<String> sizeList;
    
    private ChangeListener<String> familyListChangeListener;
    private ChangeListener<String> familyTextChangeListener;
    
    /**
     * 
     */
    public FontDialog() {
        Scene scene = getDialogPane().getScene();
        scene.getStylesheets().add(getClass().getResource("/res/css/titleborder.css").toExternalForm());
        window = scene.getWindow();
        window.setOnCloseRequest(event -> window.hide());
        ((Stage)window).getIcons().add(IconCache.loadImage("/res/icons/JNotepadIconSmall.png"));

        selectedFont = ApplicationPreferences.getCurrentFont();
        fontFamily = selectedFont.getFamily();
        bold = ApplicationPreferences.isBold();
        italic = ApplicationPreferences.isItalic();
        size = selectedFont.getSize();
        
        BorderPane mainPane = new BorderPane();
        getDialogPane().setContent(mainPane);

        BorderPane fontChoicePane = new BorderPane();
        mainPane.setTop(fontChoicePane);
        
        BorderPane middlePane = new BorderPane();
        fontChoicePane.setCenter(middlePane);

        BorderPane familyPane = new BorderPane();
        middlePane.setLeft(familyPane);
        familyField = new TextField(fontFamily);
        familyPane.setTop(familyField);
        BorderPane.setMargin(familyField, new Insets(0, 0, 5, 0));
        familyField.setMaxWidth(200);
        familyList = new ListView<>();
        familyPane.setRight(familyList);
        familyList.getItems().addAll(Font.getFamilies());
        familyList.setMaxSize(200, 144);

        BorderPane stylePane = new BorderPane();
        middlePane.setRight(stylePane);
        int style = (bold ? 1 : 0) | (italic ? 2 : 0);
        styleField = new TextField(STYLES[style]);
        stylePane.setTop(styleField);
        BorderPane.setMargin(styleField, new Insets(0, 0, 5, 0));
        styleField.setMaxWidth(80);
        styleList = new ListView<>();
        stylePane.setRight(styleList);
        styleList.getItems().addAll(STYLES);
        styleList.setMaxSize(80, 144);
        
        BorderPane sizePane = new BorderPane();
        fontChoicePane.setRight(sizePane);
        sizeField = new TextField(String.valueOf((int)size));
        sizePane.setTop(sizeField);
        BorderPane.setMargin(sizeField, new Insets(0, 0, 5, 0));
        sizeField.setMaxWidth(50);
        sizeList = new ListView<>();
        sizePane.setRight(sizeList);
        sizeList.getItems().addAll(SIZES);
        sizeList.setMaxSize(50, 144);

        BorderPane bottomPane = new BorderPane();
        mainPane.setBottom(bottomPane);
        GridPane buttonPane = new GridPane();
        bottomPane.setRight(buttonPane);
        BorderPane.setMargin(buttonPane, new Insets(5, 5, 5, 5));
        buttonPane.setHgap(5);
        Button okButton = new Button("OK");
        buttonPane.add(okButton, 0, 0);
        okButton.setDefaultButton(true);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setResult(ButtonType.OK);
                window.hide();
            }
        });
        Button cancelButton = new Button("Cancel");
        buttonPane.add(cancelButton, 1, 0);
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setResult(ButtonType.CANCEL);
                window.hide();
            }
        });
        
        Text fontSampleLabel = new Text("AaBbYyZz");
        fontSampleLabel.setFont(selectedFont);
        BorderedTitledPane fontSampleTitlePane = new BorderedTitledPane("Sample", fontSampleLabel);
        mainPane.setCenter(fontSampleTitlePane);
        fontSampleTitlePane.setMargin(new Insets(15, 0, 15, 0));
        BorderPane.setMargin(fontSampleTitlePane, new Insets(10, 30, 5, 30));
    
        createEvents();
        addListEvents();
        addTextEvents();
    }

    private void createEvents() {
        familyListChangeListener = (observable, oldValue, newValue) -> {
            familyField.setText(newValue);
        };
        
        familyTextChangeListener = (observable, oldValue, newValue) -> {
            familyField.textProperty().removeListener(familyTextChangeListener);
            String name = familyField.getText();
            ObservableList<String> items = familyList.getItems();
            familyList.getSelectionModel().selectedItemProperty().removeListener(familyListChangeListener);
            for (String item : items) {
                if (item.equalsIgnoreCase(name) || item.toLowerCase().startsWith(name.toLowerCase())) {
                    familyField.setText(item);
                    familyField.positionCaret(name.length());
                    familyField.selectEnd();
                    break;
                }
            }
            familyList.getSelectionModel().selectedItemProperty().addListener(familyListChangeListener);
            familyField.textProperty().addListener(familyTextChangeListener);
        };
    }

    private void addListEvents() {
        familyList.getSelectionModel().selectedItemProperty().addListener(familyListChangeListener);
    }
    
    private void addTextEvents() {
        familyField.textProperty().addListener(familyTextChangeListener);
    }

    /**
     * @return the fontFamily
     */
    public String getFontFamily() {
        return fontFamily;
    }

    /**
     * @return the bold
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * @return the italic
     */
    public boolean isItalic() {
        return italic;
    }

    /**
     * @return the size
     */
    public double getSize() {
        return size;
    }
}
