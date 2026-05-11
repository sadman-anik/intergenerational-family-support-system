module src.main.java.com.example.igfss_client {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.igfss_client to javafx.fxml;

    exports com.example.igfss_client;
}
