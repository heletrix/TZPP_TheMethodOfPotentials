package tzpp.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CreateNodeDialogController {
    @FXML
    private TextField numberTextField;
    @FXML
    private ComboBox<String> typeComboBox;


    private ObservableList<String> typeOfNodeList = FXCollections.observableArrayList();
    private Stage dialogStage;
    private boolean okClicked = false;

    private Integer indexType;
    private Integer resourceNumber;

    public void initialize() {
        typeOfNodeList.setAll(getTypes());
        typeComboBox.setItems(typeOfNodeList);
        // Заборонимо вводити не цифри
        numberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    numberTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
        });
    }

    private ArrayList<String> getTypes(){
        ArrayList<String> types = new ArrayList<>();
        types.add("Виробник");
        types.add("Споживач");
        return types;
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
            indexType = typeComboBox.getSelectionModel().getSelectedIndex();
            resourceNumber = Integer.parseInt(numberTextField.getText());
            okClicked = true;
            dialogStage.close();
        }
    }

    // перевіряє коректність введених данних
    private boolean isInputValid(){
        String errorMessage = "";
        boolean isTypeEmpty = typeComboBox.getSelectionModel().isEmpty();
        boolean isNumberEmpty = numberTextField.getText().trim().isEmpty();

        if (isTypeEmpty) {
            errorMessage += "Оберіть тип підприємства!\n";
        }
        if (isNumberEmpty) {
            errorMessage += "Оберіть кількість ресурсу!\n";
        }
        if (!isNumberEmpty) {
            int inputNumber = -1;
            try{
            inputNumber = Integer.parseInt(numberTextField.getText());
            } catch (Exception ex){
                errorMessage += "Кількість ресурсу дуже велика!\n";
            }
            if (inputNumber < 0 || inputNumber > 1000)
            errorMessage += "Кількість ресурсу має бути від 0 до 300\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Common.showErrorWindow(errorMessage, dialogStage);
            return false;
        }
    }

    Integer getIndexType() {
        return indexType;
    }

    Integer getResourceNumber() {
        return resourceNumber;
    }
}
