package gui;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
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
    private void onBtNewAction(ActionEvent event) {
	Stage parentStage = Utils.currentStage(event);	// referência para o palco pai do próximo evento
	createDialogForm("/gui/DepartmentForm.fxml", parentStage);
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
    
    private void createDialogForm(String absoluteName, Stage parentStage) {
	try {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
	    Pane pane = loader.load();	//carrega um novo painel
	    
	    /* Como se trata de janelas de diálogo, uma vez que são modais, elas devem ocorrer por cima de outras*/
	    Stage dialogStage = new Stage();	// instanciamento de um novo palco
	    dialogStage.setTitle("Insira os dados do novo departamento");
	    dialogStage.setScene(new Scene(pane));  // configurando a cena com o painel desejado
	    dialogStage.setResizable(false);	    // palco não redimensionável
	    dialogStage.initOwner(parentStage);	    // pai da janela, janela anterior
	    dialogStage.initModality(Modality.WINDOW_MODAL); //janela travada até ser fechada salvando ou cancelando
	    dialogStage.showAndWait();	//mostra o palco e aguarda
	}
	catch (IOException e){
	    Alerts.showAlert("IO Exception", "Erro ao carregar view", e.getMessage(), Alert.AlertType.ERROR);
	}
    }
}
