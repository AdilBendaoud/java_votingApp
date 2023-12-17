package Util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Callback;

import java.util.concurrent.atomic.AtomicBoolean;

public class AlertMessage {
    private Alert alert;

    public void errorMessage(String message){

        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }

    public boolean confirmation(String message) {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(message);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        return alert.showAndWait().filter(response -> response == ButtonType.YES).isPresent();
    }

    public void successMessage(String message){
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
