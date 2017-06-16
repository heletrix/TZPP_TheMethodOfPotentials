package tzpp.controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

class Common {

    static void showErrorWindow(String message, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("Помилка");
        alert.setHeaderText("Виконайте наступні рекомендації: ");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
