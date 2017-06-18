package tzpp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tzpp.Main;
import tzpp.model.*;
import tzpp.model.graphModel.Edge;
import tzpp.model.graphModel.Graph;
import tzpp.model.graphModel.Node;
import tzpp.model.graphModel.NodeCircle;

import java.io.*;
import java.util.ArrayList;

public class MainController {
    @FXML
    private TextArea textAreaMPZK;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private Canvas canvasGraphDraw;
    @FXML
    private Canvas canvasTZLP;
    @FXML
    private Canvas canvasMPZK;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab tabTZLP;
    @FXML
    private TextArea textAreaTZLP;
    @FXML
    private  Tab tabMPZK;

    private GraphicsContext gcGraph;
    private GraphicsContext gcTZLP;
    private ObservableList<Node> nodes = FXCollections.observableArrayList();
    private ObservableList<Edge> edges = FXCollections.observableArrayList();
    private TZLPmodel tzlpModel;
    private MPZKmodel MPZK;
    // розмір кружечків
    private Integer circleSize;

    public void initialize() {
        gcGraph = canvasGraphDraw.getGraphicsContext2D();
        gcTZLP = canvasTZLP.getGraphicsContext2D();

        circleSize = 40;
        tzlpModel = new TZLPmodel(canvasTZLP, circleSize);
        // Обробник кліка по графічному полю
        canvasGraphDraw.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                try {
                    showCreateNodeDialog(event);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else if (event.isSecondaryButtonDown()) {
                setGraphColors(Color.DARKBLUE, Color.WHITESMOKE, 5);
                drawOval(addOval(0, 2, event.getX(), event.getY()));
            }
        });

    }
    private Stage getStage() {
        return (Stage) mainAnchorPane.getScene().getWindow();
    }

    // Створює сцену діалогового вікна.
    private Stage createStage(String name, AnchorPane anchorPane) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(name);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(getStage());
        dialogStage.setScene(new Scene(anchorPane));
        return dialogStage;
    }

    // Відображує вікно додавання вершини
    private void showCreateNodeDialog(MouseEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/createNodeDialog.fxml"));
        Stage dialogStage = createStage("Додати вершину", loader.load());
        CreateNodeDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
        if (controller.isOkClicked()) {
            // Якщо виробник
            if (controller.getIndexType() == 0)
                setGraphColors(Color.YELLOWGREEN, Color.YELLOWGREEN, 5);
            else // Якщо споживач
                setGraphColors(Color.ORANGERED, Color.ORANGERED, 5);
            drawOval(addOval(controller.getResourceNumber(), controller.getIndexType(), e.getX(), e.getY()));
        }
    }
    // Відображує вікно додавання ребра
    @FXML
    private void showCreateEdgeDialog(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/createEdgeDialog.fxml"));
        Stage dialogStage = createStage("Додати ребро", loader.load());

        CreateEdgeDialogController controller = loader.getController();
        controller.initData(nodes, edges);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
        if (controller.isOkClicked()) {
            Node node1 = controller.getFirstNode();
            Node node2 = controller.getSecondNode();
            Integer value = controller.getValueOfEdge();
            // малюємо стрілку та додаємо до списку ребер
            edges.add(new Edge(node1, node2, value));
            drawArrow(node1.getNodeCircle().getX(), node1.getNodeCircle().getY(),
                    node2.getNodeCircle().getX(), node2.getNodeCircle().getY(),
                    value);
        }
    }

    // Додає нову вершину
    private Node addOval(Integer number, Integer type, double x, double y) {
        Integer id = nodes.size() + 1;
        NodeCircle nodeCircle = new NodeCircle(x, y);
        Node node = new Node(nodeCircle, number, Node.NodeType.values()[type], id);
        nodes.add(node);
        return node;
    }

    // Малює нову вершину
    private void drawOval(Node node) {
        Double x = node.getNodeCircle().getX() - circleSize / 2;
        Double y = node.getNodeCircle().getY() - circleSize / 2;
        Integer number = node.getResource();
        Integer id = node.getId();
        if (node.getType() == Node.NodeType.TRANSIT) {
            gcGraph.fillOval(x, y, circleSize, circleSize);
        } else {
            gcGraph.fillOval(x, y, circleSize, circleSize);
            gcGraph.strokeOval(x + circleSize, y - 10, 20, 20);
            gcGraph.setFill(Color.BLACK);
            gcGraph.fillText(number.toString(), x + circleSize, y + 5);
        }
        gcGraph.fillText(id.toString(), x + (circleSize / 3), y - (circleSize / 3));
    }

    // Малює стрілочку між вершинами графу
    private void drawArrow(double node1X, double node1Y, double node2X, double node2Y, Integer value) {
        setGraphColors(Color.BLACK, Color.BLACK, 2);

        double arrowAngle = Math.toRadians(45.0);
        double arrowLength = 10.0;

        double dx = node1X - node2X;
        double dy = node1Y - node2Y;

        double angle = Math.atan2(dy, dx);

        double x1 = Math.cos(angle + arrowAngle) * arrowLength + node2X;
        double y1 = Math.sin(angle + arrowAngle) * arrowLength + node2Y;

        double x2 = Math.cos(angle - arrowAngle) * arrowLength + node2X;
        double y2 = Math.sin(angle - arrowAngle) * arrowLength + node2Y;

        gcGraph.fillText(value.toString(), (node1X + node2X) / 2 + 5, (node1Y + node2Y) / 2 - 5);
        gcGraph.strokeLine(node1X, node1Y, node2X, node2Y);
        gcGraph.strokeLine(node2X, node2Y, x1, y1);
        gcGraph.strokeLine(node2X, node2Y, x2, y2);
    }

    // Збереження графу у файл
    public void saveGraph(ActionEvent actionEvent) {
        if (edges.size() < 1 || nodes.size() < 1) {
            Common.showErrorWindow("Збереження неможливе. Граф не задано", getStage());
        } else {
            Graph graph = new Graph(new ArrayList<>(nodes), new ArrayList<>(edges));
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Зберегти граф");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graph", "*.graph"));
            File file = fileChooser.showSaveDialog(getStage());
            if (file != null) {
                FileOutputStream fileOut;
                try {
                    fileOut = new FileOutputStream(file);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(graph);
                    out.close();
                    fileOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Завантаження графу з файлу
    public void loadGraph(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Завантажити граф");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graph", "*.graph"));
        File file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            FileInputStream fileIn;
            Graph graphDeserialize;
            try {
                fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                graphDeserialize = (Graph) in.readObject();
                nodes.setAll(graphDeserialize.getNodes());
                edges.setAll(graphDeserialize.getEdges());
                in.close();
                fileIn.close();
                redrawCanvas();
            }  catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // змінює кольори графічного контексту графу
    private void setGraphColors(Color cFill, Color cStroke, Integer lineWidth) {
        gcGraph.setFill(cFill);
        gcGraph.setStroke(cStroke);
        gcGraph.setLineWidth(lineWidth);
    }

    // Визначає колір та малює вершину
    private void determineColors(Node.NodeType type) {
        if (type == Node.NodeType.OUTPUT)
            setGraphColors(Color.YELLOWGREEN, Color.YELLOWGREEN, 5);
        else if (type == Node.NodeType.INPUT)
            setGraphColors(Color.ORANGERED, Color.ORANGERED, 5);
        else setGraphColors(Color.DARKBLUE, Color.WHITESMOKE, 5);
    }

    // Перемалювує поле графу
    private void redrawCanvas() {
        gcGraph.clearRect(0, 0, canvasGraphDraw.getWidth(), canvasGraphDraw.getHeight());
        for (Node node : nodes) {
            determineColors(node.getType());
            drawOval(node);
        }
        for (Edge edge : edges) {
            setGraphColors(Color.BLACK, Color.BLACK, 2);
            drawArrow(edge.getFirstNode().getNodeCircle().getX(), edge.getFirstNode().getNodeCircle().getY(),
                    edge.getSecondNode().getNodeCircle().getX(), edge.getSecondNode().getNodeCircle().getY(),
                    edge.getValue());
        }
    }

    @FXML
    private void clearGraphField() {
        gcGraph.clearRect(0, 0, canvasGraphDraw.getWidth(), canvasGraphDraw.getHeight());
        nodes.clear();
        edges.clear();
    }

    // Перетворює ТЗПП в ТЗЛП
    @FXML
    private void createTZLP() {
        // Визначення кількості продукції, що виробляється
        double sumOutputNodes = nodes.filtered(f -> f.getType().equals(Node.NodeType.OUTPUT)).stream()
                .mapToInt(Node::getResource).sum();
        // Визначення кількості продукції, що споживається
        double sumInputNodes = nodes.filtered(f -> f.getType().equals(Node.NodeType.INPUT)).stream()
                .mapToInt(Node::getResource).sum();
        // Якщо пункти не задані
        if (sumInputNodes <= 0 || sumOutputNodes <= 0) {
            Common.showErrorWindow("Спочатку задайте виробників та споживачів!", getStage());
        }
        // Якщо задача збалансована
        else if (sumOutputNodes == sumInputNodes) {
            tzlpModel.clear();
            mainTabPane.getSelectionModel().select(tabTZLP);
            tzlpModel.createTZLP(new ArrayList<> (nodes), new ArrayList<> (edges), sumInputNodes);
            // Прорисовка таблиці транспортних витрат ТЗЛП

            tzlpModel.drawColumnName();
            tzlpModel.drawRowName();
            tzlpModel.drawTable();
            textAreaTZLP.setText(tzlpModel.typeListsToString());
        }
        // Якщо задача не збалансована
        else {
            Common.showErrorWindow("Збалансуйте задачу!\nВсього виробляється: " +
                    sumOutputNodes + ";\n Всього споживається: " + sumInputNodes, getStage());
        }
    }
    @FXML
    private void findDBRbyMPZK(){
        if(textAreaTZLP.getText().length() > 0){

            GraphicsContext gcMPZK = canvasMPZK.getGraphicsContext2D();
            gcMPZK.clearRect(0,0,canvasMPZK.getWidth(),canvasMPZK.getHeight());

            mainTabPane.getSelectionModel().select(tabMPZK);
            MPZK = new MPZKmodel(tzlpModel);
            MPZK.getSolution();


            MPZK.drawSolution(gcMPZK, circleSize);
            textAreaMPZK.setText(MPZK.getTransportCost());
        }
        else Common.showErrorWindow("Спочатку перетворіть в ТЗЛП!", getStage());
    }
}
