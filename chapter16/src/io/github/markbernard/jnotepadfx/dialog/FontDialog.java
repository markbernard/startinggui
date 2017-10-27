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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
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
    private Text fontSampleLabel;
    
    private ChangeListener<String> familyListChangeListener;
    private ChangeListener<String> styleListChangeListener;
    private ChangeListener<String> sizeListChangeListener;
    private ChangeListener<String> familyTextChangeListener;
    private ChangeListener<String> styleTextChangeListener;
    private ChangeListener<String> sizeTextChangeListener;
    
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
        familyList.getSelectionModel().select(fontFamily);
        familyList.scrollTo(fontFamily);

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
        styleList.getSelectionModel().select(style);
        styleList.scrollTo(style);
        
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
        sizeList.getSelectionModel().select((String.valueOf((int)size)));
        sizeList.scrollTo((String.valueOf((int)size)));

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
        
        fontSampleLabel = new Text("AaBbYyZz");
        fontSampleLabel.setFont(selectedFont);
        BorderedTitledPane fontSampleTitlePane = new BorderedTitledPane("Sample", fontSampleLabel);
        mainPane.setCenter(fontSampleTitlePane);
        fontSampleTitlePane.setMargin(new Insets(15, 0, 15, 0));
        BorderPane.setMargin(fontSampleTitlePane, new Insets(10, 30, 5, 30));
        fontSampleTitlePane.setMaxHeight(56);
    
        addListEvents();
        addTextEvents();
    }

    private void addListEvents() {
        familyListChangeListener = (observable, oldValue, newValue) -> {
            familyField.textProperty().removeListener(familyTextChangeListener);
            familyField.setText(newValue);
            familyField.textProperty().addListener(familyTextChangeListener);
            updateFont();
        };
        styleListChangeListener = (observable, oldValue, newValue) -> {
            styleField.textProperty().removeListener(styleTextChangeListener);
            styleField.setText(newValue);
            styleField.textProperty().addListener(styleTextChangeListener);
            updateFont();
        };
        sizeListChangeListener = (observable, oldValue, newValue) -> {
            sizeField.textProperty().removeListener(sizeTextChangeListener);
            sizeField.setText(newValue);
            sizeField.textProperty().addListener(sizeTextChangeListener);
            updateFont();
        };

        familyList.getSelectionModel().selectedItemProperty().addListener(familyListChangeListener);
        styleList.getSelectionModel().selectedItemProperty().addListener(styleListChangeListener);
        sizeList.getSelectionModel().selectedItemProperty().addListener(sizeListChangeListener);
    }
    
    private void addTextEvents() {
        familyTextChangeListener = (observable, oldValue, newValue) -> {
            familyList.getSelectionModel().selectedItemProperty().removeListener(familyListChangeListener);
            ObservableList<String> items = familyList.getItems();
            for (String item : items) {
                if (item.equalsIgnoreCase(newValue) || item.toLowerCase().startsWith(newValue.toLowerCase())) {
                    familyList.getSelectionModel().select(item);
                    familyList.scrollTo(item);
                    familyField.setText(item);
                    Platform.runLater(() -> {
                        familyField.positionCaret(newValue.length());
                        familyField.selectEnd();
                    });
                    break;
                }
            }
            familyList.getSelectionModel().selectedItemProperty().addListener(familyListChangeListener);
            updateFont();
        };
        styleTextChangeListener = (observable, oldValue, newValue) -> {
            styleList.getSelectionModel().selectedItemProperty().removeListener(styleListChangeListener);
            ObservableList<String> items = styleList.getItems();
            for (String item : items) {
                if (item.equalsIgnoreCase(newValue) || item.toLowerCase().startsWith(newValue.toLowerCase())) {
                    styleList.getSelectionModel().select(item);
                    styleList.scrollTo(item);
                    styleField.setText(item);
                    Platform.runLater(() -> {
                        styleField.positionCaret(newValue.length());
                        styleField.selectEnd();
                    });
                    break;
                }
            }
            styleList.getSelectionModel().selectedItemProperty().addListener(styleListChangeListener);
            updateFont();
        };
        sizeTextChangeListener = (observable, oldValue, newValue) -> {
            sizeList.getSelectionModel().selectedItemProperty().removeListener(sizeListChangeListener);
            ObservableList<String> items = sizeList.getItems();
            for (String item : items) {
                if (item.equalsIgnoreCase(newValue) || item.toLowerCase().startsWith(newValue.toLowerCase())) {
                    sizeList.getSelectionModel().select(item);
                    sizeList.scrollTo(item);
                    sizeField.setText(item);
                    Platform.runLater(() -> {
                        sizeField.positionCaret(newValue.length());
                        sizeField.selectEnd();
                    });
                    break;
                }
            }
            sizeList.getSelectionModel().selectedItemProperty().addListener(sizeListChangeListener);
            updateFont();
        };

        familyField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.booleanValue()) {
                Platform.runLater(() -> {
                    familyField.selectAll();
                });
            }
        });
        styleField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.booleanValue()) {
                Platform.runLater(() -> {
                    styleField.selectAll();
                });
            }
        });
        sizeField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.booleanValue()) {
                Platform.runLater(() -> {
                    sizeField.selectAll();
                });
            }
        });

        familyField.textProperty().addListener(familyTextChangeListener);
        styleField.textProperty().addListener(styleTextChangeListener);
        sizeField.textProperty().addListener(sizeTextChangeListener);
    }
    
    private void updateFont() {
        fontFamily = familyList.getSelectionModel().getSelectedItem();
        int index = styleList.getSelectionModel().getSelectedIndex();
        bold = (index & 1) == 1;
        italic = (index & 2) == 2;
        size = Double.parseDouble(sizeField.getText());
        Font font = Font.font(fontFamily, bold ? FontWeight.BOLD : FontWeight.NORMAL, italic ? FontPosture.ITALIC : FontPosture.REGULAR, size);
        fontSampleLabel.setFont(font);
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
