package com.unindra.restoserver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        new Server();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/app.fxml"));
        primaryStage.getIcons().add(new Image("/icons/logo-ramen-bulet-merah-copy50x50.png"));
        primaryStage.setTitle("Osaka Ramen");
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
