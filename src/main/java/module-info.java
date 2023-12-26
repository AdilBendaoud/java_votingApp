module com.example.votingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires kernel;
    requires layout;

    opens com.example.votingapp.Model to javafx.base;
    opens com.example.votingapp to javafx.fxml;
    exports com.example.votingapp;
    exports com.example.votingapp.Model;
}