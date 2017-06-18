package tzpp.controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Common {
    // Відображає вікно з повідомленням про помилку
    static void showErrorWindow(String message, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("Помилка");
        alert.setHeaderText("Виконайте наступні рекомендації: ");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void drawCell(GraphicsContext gc, Color fillColor, int x, int y, String text) {
        Integer size = MainController.getCircleSize();
        drawSquare(gc, fillColor, size, x, y);

        gc.setFill(Color.BLACK);
        gc.fillText(text, size * x + 1, size * (y + 1) - size / 2.7);
    }

    public static void drawCellWithTwoText(GraphicsContext gc, Color fillColor, int x, int y, String topText, String bottomText) {
        Integer size = MainController.getCircleSize();
        drawSquare(gc, fillColor, size, x, y);

        gc.setFill(Color.DARKBLUE);
        gc.fillText(topText, size * x + 5, size * y + 12);
        gc.setFill(Color.BLACK);
        gc.fillText(bottomText, size * x + 2, size * (y + 1) - 3);
    }

    private static void drawSquare(GraphicsContext gc, Color fillColor, Integer size, int x, int y) {
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(size * x, size * y, size, size, 0, 0);
        gc.setFill(fillColor);
        gc.fillRoundRect(size * x + 1, size * y + 1, size - 1, size - 1, 0, 0);
    }

}
