package gui;

import application.Main;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentListController implements Initializable {

    @FXML
    private TableView<Department> tableViewDepartment;
    
    @FXML
    private TableColumn<Department, Integer> tableColumnId;
    
    @FXML
    private TableColumn<Department, String> tableColumnName;
    
    @FXML
    private Button btNew;
    
    @FXML
    private void onBtNewAction() {
	System.out.println("onBtNewAction");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
	initializeNodes();
    }

    private void initializeNodes() {
	/* Para que as colunas funcionem é preciso associá-las aos respectivos atributos */
	tableColumnId.setCellFactory(new PropertyValueFactory("id"));
	tableColumnName.setCellFactory(new PropertyValueFactory("name"));
	
	/* Macete para redimensionar a altura da tabela conforme a janela */
	Stage stage = (Stage) Main.getMainScene().getWindow();
	tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	
    }
}
