module com.drinkcat.cardsyncelite {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.drinkcat.cardsyncelite to javafx.fxml;
    exports com.drinkcat.cardsyncelite;
}