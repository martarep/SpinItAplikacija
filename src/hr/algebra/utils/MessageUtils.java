
package hr.algebra.utils;

import javafx.scene.control.Alert;


public class MessageUtils {
    public static void showInfoMessage(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
