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

import io.github.markbernard.jnotepadfx.IconCache;
import io.github.markbernard.jnotepadfx.JNotepadFX;
import io.github.markbernard.jnotepadfx.action.EditAction;

import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * @author Mark Bernard
 *
 */
public class SearchDialog extends Dialog<ButtonType> {
    private Window window;
    private TextField findField;
    private TextField replaceField;
    private CheckBox matchCase;
    private RadioButton downRadio;
    private boolean replace;

    /**
     * @param jNotepadFX 
     * @param replace 
     */
    public SearchDialog(JNotepadFX jNotepadFX, boolean replace) {
        this.replace = replace;
        setTitle(replace ? "Replace" : "Find");
        Scene scene = getDialogPane().getScene();
        scene.getStylesheets().add(getClass().getResource("/res/css/titleborder.css").toExternalForm());
        window = scene.getWindow();
        window.setOnCloseRequest(event -> window.hide());
        ((Stage)window).getIcons().add(IconCache.loadImage("/res/icons/JNotepadIconSmall.png"));

        BorderPane dialogPane = new BorderPane();
        getDialogPane().setContent(dialogPane);
        BorderPane mainPane = new BorderPane();
        dialogPane.setCenter(mainPane);
        GridPane labelPane = new GridPane();
        RowConstraints rowConstraints = new RowConstraints();
        mainPane.setLeft(labelPane);
        labelPane.add(new Label("Find what:"), 0, 0);

        GridPane inputPane = new GridPane();
        mainPane.setCenter(inputPane);
        BorderPane.setMargin(inputPane, new Insets(5, 5, 5, 5));
        inputPane.setVgap(5);
        findField = new TextField();
        inputPane.add(findField, 0, 0);
        GridPane buttonPane = new GridPane();
        dialogPane.setRight(buttonPane);
        buttonPane.setVgap(5);
        BorderPane.setMargin(buttonPane, new Insets(5, 5, 5, 5));
        Button findNextButton = new Button("Find _Next");
        buttonPane.add(findNextButton, 0, 0);
        findNextButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        findNextButton.setDefaultButton(true);
        findNextButton.setOnAction(new EditAction.FindNextAction(jNotepadFX, this));
        Button cancelButton = new Button("Cancel");
        cancelButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(event -> window.hide());
        GridPane.setFillWidth(findNextButton, true);
        GridPane.setFillWidth(cancelButton, true);
        
        BorderPane bottomPane = new BorderPane();
        mainPane.setBottom(bottomPane);
        matchCase = new CheckBox("Match _case");
        bottomPane.setLeft(matchCase);
        BorderPane.setAlignment(matchCase, Pos.BOTTOM_LEFT);

        if (replace) {
            labelPane.add(new Label("Replace with:"), 0, 1);
            rowConstraints.setPercentHeight(25);
            labelPane.getRowConstraints().addAll(rowConstraints, rowConstraints, rowConstraints, rowConstraints);

            Button replaceButton = new Button("_Replace");
            buttonPane.add(replaceButton, 0, 1);
            replaceButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            replaceButton.setOnAction(new EditAction.FindNextAction(jNotepadFX, this));
            Button replaceAllButton = new Button("Replace _All");
            buttonPane.add(replaceAllButton, 0, 2);
            replaceAllButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            replaceAllButton.setOnAction(new EditAction.ReplaceAllAction(jNotepadFX, this));
            buttonPane.add(cancelButton, 0, 3);

            replaceField = new TextField();
            inputPane.add(replaceField, 0, 1);
        } else {
            rowConstraints.setPercentHeight(100);
            labelPane.getRowConstraints().addAll(rowConstraints);

            buttonPane.add(cancelButton, 0, 1);

            ToggleGroup toggleGroup = new ToggleGroup(); 
            GridPane radioPane = new GridPane();
            RadioButton upRadio = new RadioButton("_Up");
            radioPane.add(upRadio, 0, 0);
            upRadio.setToggleGroup(toggleGroup);
            downRadio = new RadioButton("_Down");
            radioPane.add(downRadio, 1, 0);
            downRadio.setToggleGroup(toggleGroup);
            downRadio.setSelected(true);
            BorderedTitledPane borderedTitledPane = new BorderedTitledPane("Direction", radioPane);
            bottomPane.setRight(borderedTitledPane);
            BorderPane.setMargin(borderedTitledPane, new Insets(5, 2, 2, 2));
        }
    }

    /**
     * @return the findTerm
     */
    public String getFindTerm() {
        return findField.getText();
    }
    
    /**
     * @return the replaceTerm
     */
    public String getReplaceTerm() {
        return replaceField.getText();
    }
    
    /**
     * @return the findDownDirection
     */
    public boolean isFindDirectionDown() {
        if (replace) {
            return true;
        } else {
            return downRadio.isSelected();
        }
    }
    
    /**
     * @return the matchCase
     */
    public boolean isMatchCase() {
        return matchCase.isSelected();
    }
}
