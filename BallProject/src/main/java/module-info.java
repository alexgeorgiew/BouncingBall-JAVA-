module com.example.ballproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.management;


    opens com.example.ballproject to javafx.fxml;
    exports com.example.ballproject;
}