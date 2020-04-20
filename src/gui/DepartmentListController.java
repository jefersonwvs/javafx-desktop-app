package gui;

import application.Main;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {

    private DepartmentService service;	// associação
    
    @FXML
    private TableView<Department> tableViewDepartment;
    
    @FXML
    private TableColumn<Department, Integer> tableColumnId;
    
    @FXML
    private TableColumn<Department, String> tableColumnName;
    
    @FXML
    private Button btNew;
    
    /* Tipo de lista que pode ser visualizável em elementos do JavaFX*/
    private ObservableList<Department> obsList;
    
    @FXML
    private void onBtNewAction() {
	System.out.println("onBtNewAction");
    }
    
    public void setDepartmentService(DepartmentService service) { //injenção de dependêcia
	this.service = service;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
	initializeNodes();
    }

    private void initializeNodes() {
	/* Para que as colunas funcionem é preciso associá-las aos respectivos atributos */
	tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
	tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
	
	/* Macete para redimensionar a altura da tabela conforme a janela */
	Stage stage = (Stage) Main.getMainScene().getWindow();
	tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	
    }
    
    /* Método que atualiza a tabela de departamentos */
    public void updateTableView() {
	if (service == null) { //Precaução para caso o programador tenha esquecido de injetar a dependência
	    throw new IllegalStateException("Service was null");
	}
	List<Department> list = service.findAll();	    // recebendo a lista de objetos
	obsList = FXCollections.observableArrayList(list);  // transformando em um tipo visualizável
	tableViewDepartment.setItems(obsList);		    // carregando na tabela
    }
}
