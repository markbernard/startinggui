/**
 * 
 */
package io.github.markbernard.jnotepadfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author Mark Bernard
 *
 */
public class JNotepadFX extends Application {
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        ApplicationPreferences.loadPrefs(primaryStage);
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        primaryStage.setTitle("JNotepad FX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        ApplicationPreferences.savePrefs(primaryStage);
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        launch(args);
    }
}
