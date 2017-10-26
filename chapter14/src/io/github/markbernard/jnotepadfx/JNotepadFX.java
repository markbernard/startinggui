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
package io.github.markbernard.jnotepadfx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import io.github.markbernard.jnotepadfx.action.FileAction;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author Mark Bernard
 *
 */
public class JNotepadFX extends Application {
    private static final String APPLICATION_TITLE = "JNotepad";
    private static final String NEW_FILE_NAME = "Untitled";

    private Stage primaryStage;
    private TextArea textArea;
    private boolean dirty;
    private String fileName;
    private PageLayout pageLayout;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        fileName = NEW_FILE_NAME;
        ApplicationPreferences.loadPrefs(primaryStage);
        dirty = false;
        setTitle();
        BorderPane root = new BorderPane();
        textArea = new TextArea();
        root.setCenter(textArea);
        textArea.setFont(new Font("Courier New", 16));
        createMenus(root);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        List<Image> icons = primaryStage.getIcons();
        icons.add(new Image(getClass().getResourceAsStream("/res/icons/JNotepadIconSmall.png")));
        icons.add(new Image(getClass().getResourceAsStream("/res/icons/JNotepadIcon.png")));
        
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!dirty) {
                dirty = true;
                setTitle();
            }
        });
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (!doExit()) {
                    event.consume();
                }
            }
        });
    }

    @Override
    public void stop() throws Exception {
        ApplicationPreferences.savePrefs(primaryStage);
    }

    private void setTitle() {
        primaryStage.setTitle((dirty ? "*" : "") + fileName + " - " + APPLICATION_TITLE);
    }

    private void createMenus(BorderPane container) {
        MenuBar bar = new MenuBar();
        container.setTop(bar);
        
        Menu fileMenu = new Menu("_File");
        MenuItem fileNewItem = new MenuItem("_New");
        fileNewItem.setOnAction(new FileAction.NewAction(this));
        fileNewItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        MenuItem fileOpenItem = new MenuItem("_Open...");
        fileOpenItem.setOnAction(new FileAction.OpenAction(this));
        fileOpenItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        MenuItem fileSaveItem = new MenuItem("_Save");
        fileSaveItem.setOnAction(new FileAction.SaveAction(this));
        fileSaveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        MenuItem fileSaveAsItem = new MenuItem("Save _As...");
        fileSaveAsItem.setOnAction(new FileAction.SaveAsAction(this));
        MenuItem filePageSetupItem = new MenuItem("Page Set_up...");
        filePageSetupItem.setOnAction(new FileAction.PageSetupAction(this));
        MenuItem filePrintItem = new MenuItem("_Print...");
        filePrintItem.setOnAction(new FileAction.PrintAction(this));
        filePrintItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
        MenuItem fileExitItem = new MenuItem("E_xit");
        fileExitItem.setOnAction(new FileAction.ExitAction(this));
        fileMenu.getItems().addAll(fileNewItem, fileOpenItem, fileSaveItem, fileSaveAsItem, new SeparatorMenuItem(), 
                filePageSetupItem, filePrintItem, new SeparatorMenuItem(), fileExitItem);
        bar.getMenus().add(fileMenu);
    }

    private boolean doExit() {
        DirtyStatus status = isDirty();
        
        boolean saveCompleted = true;
        if (status.equals(DirtyStatus.SAVE_FILE)) {
            saveCompleted = save();
        } else if (status.equals(DirtyStatus.CANCEL_ACTION)) {
            saveCompleted = false;
        }

        return saveCompleted;
    }
    
    /**
     * Exits the application with cleanup.
     */
    public void exit() {
        if (doExit()) {
            Platform.exit();
        }
    }

    /**
     * Create a new document while checking to see if the current document is saved.
     */
    public void newDocument() {
        DirtyStatus status = isDirty();
        
        boolean saveCompleted = true;
        if (status.equals(DirtyStatus.SAVE_FILE)) {
            saveCompleted = save();
        } else if (status.equals(DirtyStatus.CANCEL_ACTION)) {
            saveCompleted = false;
        }

        if (saveCompleted) {
            fileName = NEW_FILE_NAME;
            textArea.setText("");
            dirty = false;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    setTitle();
                }
            });
        }
    }
    
    /**
     * Show a file dialog and load the selected file.
     */
    public void load() {
        DirtyStatus result = isDirty();
        
        boolean saveSuccessful = true;
        if (result.equals(DirtyStatus.SAVE_FILE)) {
            saveSuccessful = save();
        } else if (result.equals(DirtyStatus.CANCEL_ACTION)) {
            saveSuccessful = false;
        }
        
        if (saveSuccessful) {
            String filePath = ApplicationPreferences.getCurrentFilePath();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(filePath));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                fileName = selectedFile.getName();
                ApplicationPreferences.setCurrentFilePath(
                        selectedFile.getParentFile().getAbsolutePath()
                        .replace("\\", "/"));
                loadFile(selectedFile);
            }
        }
    }

    /**
     * Save an existing document. If there is no existing document then call saveAs.
     * If a call to saveAs is made and the user cancels during the file save dialog 
     * then false will be returned.
     * 
     * @return true if the save was not interrupted, false otherwise
     */
    public boolean save() {
        if (fileName.equals(NEW_FILE_NAME)) {
            return saveAs();
        } else {
            saveFile(ApplicationPreferences.getCurrentFilePath() + "/" + fileName);
            dirty = false;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    setTitle();
                }
            });
            
            return true;
        }
    }

    private void saveFile(String path) {
        Writer out = null;
        
        try {
            out = new OutputStreamWriter(new FileOutputStream(path), 
                    StandardCharsets.UTF_8);
            out.write(textArea.getText());
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(AlertType.ERROR, "Unable to create the file: " + path + "\n" + e.getMessage(), ButtonType.OK);
            alert.show();
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR, "Unable to save the file: " + path + "\n" + e.getMessage(), ButtonType.OK);
            alert.show();
        } finally {
            ResourceCleanup.close(out);
        }
    }

    /**
     * Display a dialog to the user asking for a location and name of the file
     * to save. If the user cancels the dialog then return false.
     * 
     * @return false if the user cancels the file save dialog, true otherwise
     */
    public boolean saveAs() {
        boolean result = false;
        
        String filePath = ApplicationPreferences.getCurrentFilePath();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(filePath));
        fileChooser.setInitialFileName(fileName);
        File selectedFile = fileChooser.showSaveDialog(primaryStage);
        if (selectedFile != null) {
            fileName = selectedFile.getName();
            ApplicationPreferences.setCurrentFilePath(
                    selectedFile.getParentFile().getAbsolutePath()
                    .replace("\\", "/"));
            saveFile(selectedFile.getAbsolutePath());
            dirty = false;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    setTitle();
                }
            });
            result = true;
        }
        
        return result;
    }

    /**
     * Load the file at the specified path while checking to see if the current document is saved.
     * 
     * @param path
     */
    public void loadFile(File path) {
        InputStreamReader in = null;
        
        try {
            in = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
            final StringBuilder content = new StringBuilder();
            char[] buffer = new char[32768];
            int read = -1;
            while ((read = in.read(buffer)) > -1) {
                content.append(buffer, 0, read);
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    textArea.setText(content.toString());
                    dirty = false;
                    setTitle();
                }
            });
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(AlertType.ERROR, "Unable to find the file: " + path + "\n" + e.getMessage(), ButtonType.OK);
            alert.show();
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR, "Unable to load the file: " + path + "\n" + e.getMessage(), ButtonType.OK);
            alert.show();
        } finally {
            ResourceCleanup.close(in);
        }
    }
    
    private DirtyStatus isDirty() {
        DirtyStatus result = DirtyStatus.DONT_SAVE_FILE;
                
        if (dirty) {
            Alert alert = new Alert(AlertType.NONE, 
                    "There are changes in the current document.\n" +
                    "Click 'Yes' to save changes.\n" +
                    "Click 'No' to discard changes.\n" +
                    "Click 'Cancel' to stop the current action.", 
                    ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            Optional<ButtonType> userChoice = alert.showAndWait();
            ButtonType choice = userChoice.get();
            if (choice.equals(ButtonType.YES)) {
                result = DirtyStatus.SAVE_FILE;
            } else if (choice.equals(ButtonType.NO)) {
                result = DirtyStatus.DONT_SAVE_FILE;
            } else if (choice.equals(ButtonType.CANCEL)) {
                result = DirtyStatus.CANCEL_ACTION;
            }
        }
        
        return result;
    }
    
    /**
     * Show the user the page setup dialog and store the users settings.
     */
    public void pageSetup() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (pageLayout != null) {
            printerJob.getJobSettings().setPageLayout(pageLayout);
        }
        if (printerJob.showPageSetupDialog(primaryStage)) {
            pageLayout = printerJob.getJobSettings().getPageLayout();
        }
    }

    /**
     * Perform the printing request.
     */
    public void doPrint() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (pageLayout != null) {
            printerJob.getJobSettings().setPageLayout(pageLayout);
        }
        if (printerJob.showPrintDialog(primaryStage)) {
            pageLayout = printerJob.getJobSettings().getPageLayout();
            if (printerJob.printPage(textArea)) {
                printerJob.endJob();
            }
        }
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        launch(args);
    }

    enum DirtyStatus {
        SAVE_FILE, DONT_SAVE_FILE, CANCEL_ACTION;
    }
}