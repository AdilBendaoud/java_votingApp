module com.example.votingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens com.example.votingapp to javafx.fxml;
    exports com.example.votingapp;
}