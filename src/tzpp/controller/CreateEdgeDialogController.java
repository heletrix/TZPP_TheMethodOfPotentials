package tzpp.controller;

import com.sun.istack.internal.Nullable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tzpp.model.graphModel.Edge;
import tzpp.model.graphModel.Node;

import java.util.Optional;

public class CreateEdgeDialogController {
    @FXML
    public TextField valueTextField;
    @FXML
    private ComboBox<Node> firstNodeComboBox;
    @FXML
    private ComboBox<Node> secondNodeComboBox;

    private ObservableList<Node> nodeList = FXCollections.observableArrayList();
    private ObservableList<Edge> edgesList = FXCollections.observableArrayList();

    private Stage dialogStage;
    private boolean okClicked = false;

    private Node firstNode;
    private Node secondNode;
    private Integer valueOfEdge;

    public void initialize() {
        valueTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    valueTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
        });
    }

    void initData(ObservableList<Node> nodes, ObservableList<Edge> edges){
        this.edgesList = edges;
        this.nodeList = nodes;
        firstNodeComboBox.setItems(nodeList);
        secondNodeComboBox.setItems(nodeList);
    }

    void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    boolean isOkClicked(){
        return okClicked;
    }
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    @FXML
    private void handleOk(ActionEvent actionEvent) {
        if (isInputValid()) {
            firstNode = firstNodeComboBox.getSelectionModel().getSelectedItem();
            secondNode = secondNodeComboBox.getSelectionModel().getSelectedItem();
            valueOfEdge = Integer.parseInt(valueTextField.getText());
            okClicked = true;
            dialogStage.close();
        }
    }

    // перевіряє коректність введених данних
    private boolean isInputValid(){
        String errorMessage = "";
        boolean isFirstEmpty = firstNodeComboBox.getSelectionModel().isEmpty();
        boolean isSecondEmpty = secondNodeComboBox.getSelectionModel().isEmpty();
        boolean isValueEmpty = valueTextField.getText().trim().isEmpty();

        if (isFirstEmpty) {
            errorMessage += "Оберіть звідки вивозити продукцію!\n";
        }
        if (isSecondEmpty) {
            errorMessage += "Оберіть куди везти продукцію!\n";
        }
        if (isValueEmpty) {
            errorMessage += "Введіть транспортні витрати!\n";
        }
        if (!isValueEmpty) {
            int inputNumber = -1;
            try{
                inputNumber = Integer.parseInt(valueTextField.getText());
            } catch (Exception ex){
                errorMessage += "Транспортні витрати дуже великі!\n";
            }
            if (inputNumber < 0 || inputNumber > 1000)
                errorMessage += "Транспортні витрати мають бути від 0 до 1000!\n";
        }
        if (!isFirstEmpty && !isSecondEmpty) {
            if (firstNodeComboBox.getSelectionModel().getSelectedIndex() ==
                    secondNodeComboBox.getSelectionModel().getSelectedIndex()){
                errorMessage += "Оберіть різні пункти!\n";
            }
            @Nullable
            Optional<Edge> edge;
            edge = edgesList.stream().filter(ed -> ed.getFirstNode().equals(firstNodeComboBox.getSelectionModel().getSelectedItem()) &&
                    ed.getSecondNode().equals(secondNodeComboBox.getSelectionModel().getSelectedItem())).findFirst();
            if (edge != null && edge.isPresent()) {
                    errorMessage += "Оберіть інші пункти. Таке сполучення вже існує!\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Common.showErrorWindow(errorMessage, dialogStage);
            return false;
        }
    }

    Node getFirstNode() {
        return firstNode;
    }

    Node getSecondNode() {
        return secondNode;
    }

    Integer getValueOfEdge() {
        return valueOfEdge;
    }
}
