module com.drinkcat.cardsyncelite {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.drinkcat.cardsyncelite to javafx.fxml;
    opens com.drinkcat.cardsyncelite.core to javafx.fxml;
    opens com.drinkcat.cardsyncelite.controller to javafx.fxml;

    exports com.drinkcat.cardsyncelite;
    exports com.drinkcat.cardsyncelite.core;
    exports com.drinkcat.cardsyncelite.controller;
    exports com.drinkcat.cardsyncelite.module;
}