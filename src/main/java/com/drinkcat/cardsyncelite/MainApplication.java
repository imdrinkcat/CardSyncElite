package com.drinkcat.cardsyncelite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Card Sync Elite");
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("view/MainView.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}
