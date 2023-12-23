package com.drinkcat.cardsyncelite.util;


import javafx.fxml.FXMLLoader;

public class AlertUtil {
    public void ShowInformWindow(String title, String content) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("view/InformWindow.fxml"));

    }
}
