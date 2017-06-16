package tzpp.controller;

import javafx.event.EventHandler;
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
import javafx.stage.Window;
import tzpp.Main;

import java.io.IOException;

public class mainController {
    public AnchorPane mainAnchorPane;
    @FXML
    private Canvas canvasGraphDraw;

    GraphicsContext gc;

    public void initialize() {
        gc = canvasGraphDraw.getGraphicsContext2D();
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);

        canvasGraphDraw.addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        try {
                            showCreateDialog(e);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    }
                });
    }

    private Stage getStage() {
        return (Stage) mainAnchorPane.getScene().getWindow();
    }

    public void showCreateDialog(MouseEvent e) throws IOException {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/createEdgeDialog.fxml"));

            // Создаём диалоговое окно Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Додати вершину");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(getStage());
            dialogStage.setScene(new Scene((AnchorPane) loader.load()));

            // Передаём информацию в другой контроллер.
            createEdgeDialogController controller = loader.getController();
//            controller.initData(getLectureHallCB(comboBoxLectureHalls), schedule, disciplines);
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            if (controller.isOkClicked()) {
                gc.fillOval(e.getX(),e.getY(),30,30);
                gc.strokeOval(e.getX(), e.getY(), 30, 30);
            }
    }
}
