package vn.elca.training.pilot_project_front.util;

import javafx.stage.Stage;


public class StageManager {
    private static Stage primaryStage;

    private StageManager() {
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }
}
