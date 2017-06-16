package tzpp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tzpp.Main;
import tzpp.model.Edge;
import tzpp.model.Node;
import tzpp.model.NodeCircle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private Canvas canvasGraphDraw;

    private GraphicsContext gc;
    private ObservableList<Node> nodes = FXCollections.observableArrayList();
    private ObservableList<Edge> edges = FXCollections.observableArrayList();

    // розмір кружечків
    private Integer circleSize;

    public void initialize() {
        gc = canvasGraphDraw.getGraphicsContext2D();
        setColors(Color.BLACK, Color.BLACK, 5);

        circleSize = 40;
        // Обробник кліка по графічному полю
        canvasGraphDraw.setOnMousePressed(event -> {
            if(event.isPrimaryButtonDown()) {
                try {
                    showCreateDialog(event);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            else  if (event.isSecondaryButtonDown()) {
                setColors(Color.DARKBLUE, Color.WHITESMOKE, 5);
                createOval(0, 2, event.getX() - circleSize/2, event.getY() - circleSize/2);
            }
        });

    }

    private Stage getStage() {
        return (Stage) mainAnchorPane.getScene().getWindow();
    }

    // Відобразити вікно додавання вершини
    private void showCreateDialog(MouseEvent e) throws IOException {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/createNodeDialog.fxml"));
            Stage dialogStage = createStage("Додати вершину", loader.load());
            CreateNodeDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                // Якщо виробник
                if (controller.getIndexType() == 0)
                    setColors(Color.YELLOWGREEN, Color.YELLOWGREEN, 5);
                else
                    setColors(Color.ORANGERED, Color.ORANGERED, 5);
                createOval(controller.getResourceNumber(), controller.getIndexType(), e.getX() - circleSize/2,
                        e.getY() - circleSize/2);
            }
    }

    private void setColors(Color cFill, Color cStroke, Integer lineWidth){
        gc.setFill(cFill);
        gc.setStroke(cStroke);
        gc.setLineWidth(lineWidth);
    }

    private void createOval(Integer number, Integer type, double x, double y){
        Integer id = nodes.size()+1;
        NodeCircle nodeCircle = new NodeCircle(x,y);
        nodes.add(new Node(nodeCircle, number, Node.NodeType.values()[type],id));
        if (type == 2)
        {
            gc.fillOval(x, y, circleSize, circleSize);
        }else {
            gc.fillOval(x, y, circleSize, circleSize);

            gc.strokeOval(x+circleSize, y-10, 20, 20);
            gc.setFill(Color.BLACK);
            gc.fillText(number.toString(), x + circleSize, y+5);

        }
        gc.fillText(id.toString(),x+(circleSize/3), y-(circleSize/3));
    }


    public void addEdge(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/createEdgeDialog.fxml"));
        Stage dialogStage = createStage("Додати ребро", loader.load());

        CreateEdgeDialogController controller = loader.getController();
        controller.initData(nodes);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
        if (controller.isOkClicked()) {
            Node node1 = controller.getFirstNode();
            Node node2 = controller.getSecondNode();
            Integer value = controller.getValueOfEdge();
            // малюємо стрілку та додаємо до списку ребер
            edges.add(new Edge(node1,node2, value));
            drawArrow(node1.getNodeCircle().getX() + circleSize/2,node1.getNodeCircle().getY() + circleSize/2,
                    node2.getNodeCircle().getX() + circleSize/2, node2.getNodeCircle().getY() + circleSize/2,
                    value);
        }
    }

    // Создаём диалоговое окно Stage.
    private Stage createStage(String name, AnchorPane anchorPane){
        Stage dialogStage = new Stage();
        dialogStage.setTitle(name);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(getStage());
        dialogStage.setScene(new Scene(anchorPane));
        return dialogStage;
    }

    private void drawArrow(double node1X, double node1Y, double node2X, double node2Y, Integer value) {
        setColors(Color.BLACK,Color.BLACK, 2);

        double arrowAngle = Math.toRadians(45.0);
        double arrowLength = 10.0;
        double dx = node1X - node2X;
        double dy = node1Y - node2Y;
        double angle = Math.atan2(dy, dx);
        double x1 = Math.cos(angle + arrowAngle) * arrowLength + node2X;
        double y1 = Math.sin(angle + arrowAngle) * arrowLength + node2Y;

        double x2 = Math.cos(angle - arrowAngle) * arrowLength + node2X;
        double y2 = Math.sin(angle - arrowAngle) * arrowLength + node2Y;
        gc.fillText(value.toString(),(node1X+node2X)/2 + 5,(node1Y+node2Y)/2 - 5);
        gc.strokeLine(node1X, node1Y, node2X, node2Y);
        gc.strokeLine(node2X, node2Y, x1, y1);
        gc.strokeLine(node2X, node2Y, x2, y2);
    }
}
