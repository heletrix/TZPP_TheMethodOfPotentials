package tzpp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Created by Helene on 16.06.2017.
 */
public class createEdgeDialogController {
    @FXML
    private TextField numberTextField;
    @FXML
    private ComboBox typeComboBox;

    ObservableList<String> typeOfEdgeList = FXCollections.observableArrayList();
    private Stage dialogStage;

    public void initialize() {
        typeOfEdgeList.setAll(getTypes());
        typeComboBox.setItems(typeOfEdgeList);

    }

    private ArrayList<String> getTypes(){
        ArrayList<String> types = new ArrayList<String>();
        types.add("Виробник");
        types.add("Споживач");
        types.add("Склад");
        return types;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked(){
        return true;
    }
}
