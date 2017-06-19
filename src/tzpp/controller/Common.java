package tzpp.controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tzpp.model.graphModel.Node;

public class Common {
    private static GraphicsContext gc ;

    public static void setGc(GraphicsContext gcC){
        gc = gcC;
    }


    // Відображає вікно з повідомленням про помилку
    static void showErrorWindow(String message, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("Помилка");
        alert.setHeaderText("Виконайте наступні рекомендації: ");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void drawCell(Color fillColor, int x, int y, String text) {
        Integer size = MainController.getCircleSize();
        drawSquare(fillColor, size, x, y);
        gc.setFill(Color.BLACK);
        gc.fillText(text, size * x + 1, size * (y + 1) - size / 2.7);
    }

    public static void drawCellWithTwoText(Color fillColor, int x, int y, String topText, String bottomText) {
        Integer size = MainController.getCircleSize();
        drawSquare(fillColor, size, x, y);
        gc.setFill(Color.DARKBLUE);
        gc.fillText(topText, size * x + 5, size * y + 12);
        gc.setFill(Color.BLACK);
        gc.fillText(bottomText, size * x + 2, size * (y + 1) - 3);
    }

    private static void drawSquare(Color fillColor, int size, int x, int y) {
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(size * x, size * y, size, size, 0, 0);
        gc.setFill(fillColor);
        gc.fillRoundRect(size * x + 1, size * y + 1, size - 1, size - 1, 0, 0);
    }

    // Малює нову вершину
    public static void drawOval(Node node) {
        int size = MainController.getCircleSize();
        Double x = (node.getNodeCircle().getX() - size / 2);
        Double y = (node.getNodeCircle().getY() - size / 2);
        Integer number = node.getResource();
        Integer id = node.getId();
        if (node.getType() == Node.NodeType.TRANSIT) {
            gc.fillOval(x, y, size, size);
        } else {
            gc.fillOval(x, y, size, size);
            gc.fillRect(x + size, y - 10, 20, 20);
            gc.setFill(Color.BLACK);
            gc.fillText(number.toString(), x + size, y + 5);
        }
        gc.fillText(id.toString(), x + (size / 3), y - (size / 3));
    }

    // Малює стрілочку між вершинами графу
    public static void drawArrow(double node1X, double node1Y, double node2X, double node2Y, String value) {
        setGraphColors(Color.BLACK, Color.BLACK, 2);
        int size = MainController.getCircleSize();
        double length = Math.sqrt((node2X-node1X)*(node2X-node1X)+(node2Y-node1Y)*(node2Y-node1Y));
        double radius = size/2;
        double k = radius/length;

        //перша нова точка
        double newNode1X = node1X+(node2X-node1X)*k;
        double newNode1Y = node1Y+(node2Y-node1Y)*k;

        //друга нова точка
        double newNode2X = node2X-(node2X-node1X)*k;
        double newNode2Y = node2Y-(node2Y-node1Y)*k;

        /////////////////////////////////////////////////////////////////////


        double arrowAngle = Math.toRadians(45.0);
        double arrowLength = 10.0;

        double dx = newNode1X - newNode2X;
        double dy = newNode1Y - newNode2Y;

        double angle = Math.atan2(dy, dx);

        double x1 = Math.cos(angle + arrowAngle) * arrowLength + newNode2X;
        double y1 = Math.sin(angle + arrowAngle) * arrowLength + newNode2Y;

        double x2 = Math.cos(angle - arrowAngle) * arrowLength + newNode2X;
        double y2 = Math.sin(angle - arrowAngle) * arrowLength + newNode2Y;

        gc.fillText(value, (newNode1X + newNode2X) / 2 + 5, (newNode1Y + newNode2Y) / 2 - 5);
        gc.strokeLine(newNode1X, newNode1Y, newNode2X, newNode2Y);
        gc.strokeLine(newNode2X, newNode2Y, x1, y1);
        gc.strokeLine(newNode2X, newNode2Y, x2, y2);
    }


    // змінює кольори графічного контексту графу
    private static void setGraphColors(Color cFill, Color cStroke, Integer lineWidth) {
        gc.setFill(cFill);
        gc.setStroke(cStroke);
        gc.setLineWidth(lineWidth);
    }
    public static void determineColors(Node.NodeType type) {
        if (type == Node.NodeType.OUTPUT)
            setGraphColors(Color.ORANGERED, Color.ORANGERED, 5);
        else if (type == Node.NodeType.INPUT)
            setGraphColors(Color.YELLOWGREEN, Color.YELLOWGREEN, 5);
        else setGraphColors(Color.BLUE, Color.WHITESMOKE, 5);
    }


}
