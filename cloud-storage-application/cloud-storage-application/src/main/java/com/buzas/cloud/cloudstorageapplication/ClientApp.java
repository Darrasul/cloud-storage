package com.buzas.cloud.cloudstorageapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {

    @Override
    public void start(Stage primaryStage){
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("main.fxml"));
            primaryStage.setScene(new Scene(parent));
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML");
            e.printStackTrace();
        }
    }
}
