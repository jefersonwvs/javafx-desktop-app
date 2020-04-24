package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

/* Classe controladora dos objetos e ações desenhadas no arquivo homônimo .fxml*/
public class DepartmentFormController implements Initializable {

    private Department entity;	// entidade que terá seus valores editados para inserção ou atualização conforme prevê a caixa de diálogo em questão
    private DepartmentService service;	// serviço necessário para acessar o bd
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();	// ??
    
    /* ATRIBUTOS e MÉTODOS com a notação @FXML são aqueles que ligam os elementos gráficos da janela gráfica
    DepartmentForm.fxml a esta classe, isto é, ao seu controller */
    
    @FXML
    private TextField txtId;
    
    @FXML
    private TextField txtName;
    
    @FXML
    private Label labelErrorName;
    
    @FXML
    private Button btSave;
    
    @FXML
    private Button btCancel;

    public void setEntity(Department entity) {
	this.entity = entity;
    }

    public void setService(DepartmentService service) {
	this.service = service;
    }
    
    /* ??? ainda não coompreendido */
    public void subscribeDataChangeListener(DataChangeListener listener) {
	dataChangeListeners.add(listener);
    }
    
    @FXML /* Método-ação do botão salvar */
    public void onBtSaveAction(ActionEvent event) { // mantém a referência ao evento anterior à caixa de diálogo
	/* Como as injeções de dependência são feitas manualmente, é importante programar defesas*/
	if (entity == null)
	    throw new IllegalStateException("Entity was null");
	if (service == null)
	    throw new IllegalStateException("Service was null");
	
	try {
	    entity = getFormData(); // obtém os novos valores dos respectivos atributos provenientes da caixa de diálogo
	    service.saveOrUpdate(entity);   // salva ou atualiza o registro no bd
	    notifyDataChangeListeners();    // ?
	    Utils.currentStage(event).close();	// método que pega referência do palco atual a partir do evento e o fecha
	} catch (ValidationException e) {   //trata exceção do método que obtém dados, do getFormData();
	    setErrorMessages(e.getErrors());	//seta uma lista de erros, respectivos a cada campo do registro
	} catch (DbException e) {   // trata exceção de acesso ao bd
	    Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), Alert.AlertType.ERROR);
	}
	
    }
    
    @FXML /* Método-ação do botão cancelar */
    public void onBtCancelAction(ActionEvent event) {
	Utils.currentStage(event).close();
    }

    @Override /* Primeiro método a ser executado quando a caixa de diálogo é aberta */
    public void initialize(URL location, ResourceBundle resources) {
	initializeNodes();  // método que estabelece restrições quanto ao preenchimento dos campos da caixa de diálogo
	/* outros métodos necessários */
    }
    
    private void initializeNodes() {
	Constraints.setTextFieldInteger(txtId);	// restrição: campo do Id aceita somente inteiros
	Constraints.setTextFieldMaxLength(txtName, 30);	// restrição: campo do Nome aceita no máximo 30 caracteres
    }
    
    /* Método para atualizar os campos do formulário */
    public void updateFormData() {
	if (entity == null) // programação defensiva
	    throw new IllegalStateException("Entity was null");
	txtId.setText(String.valueOf(entity.getId()));	// transformando em string e colocando no campo específico
	txtName.setText(entity.getName());
    }
    
    /* Método que obtém os dados de todos os campos e armazena em objeto para ser armazenado no bd */
    private Department getFormData() {
	Department obj = new Department();
	ValidationException exception = new ValidationException("Validation error");	// exceção personalizada de modo a ser possível visualizar o erro respectivo a cada campo do formulário
	
	obj.setId(Utils.tryParseToInt(txtId.getText()));    // transforma o valor do Id de String para Integer
	if (txtName.getText() == null || txtName.getText().trim().equals("")){	// se o campo Nome estiver vazio ou com somente espaços em branco
	    exception.addError("name", "Campo não pode ser vazio"); // adiciona este erro à lista (no caso deste projeto será possível somente este erro)
	}
	obj.setName(txtName.getText());	// embora o dado possa estar inválido, ele ainda setado
	/*if (txtAdress.getText() ...
	  if (txtMaxEmployess.getText() ...
	  ...
	*/
	if (exception.getErrors().size() > 0)	// se houver pelo menos um erro (reforçando: para este projeto será no máximo, pois o único campo que será preenchido manualmente é o Nome
	    throw exception;	// lança a exceção
	return obj; // passou pelo último if, o que quer dizer que os dados do formulário estão válidos e serão retornados para um método onde serão salvos no bd
    }

    /* ??? ainda não compreendido */
    private void notifyDataChangeListeners() {
	for (DataChangeListener listener : dataChangeListeners)
	    listener.onDataChanged();
    }
    
    /* Método que apresenta o erro proveniente de preenchimento inválido ao usuário */
    private void setErrorMessages(Map<String, String> errors) {
	Set<String> fields = errors.keySet();	// armazena as chaves dos erros
	if (fields.contains("name"))	// se existe algum erro para o campo Nome
	    labelErrorName.setText(errors.get("name"));	// seta a mensagem na respectiva label
    }
}
