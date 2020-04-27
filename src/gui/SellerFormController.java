package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

/* Classe controladora dos objetos e ações desenhadas no arquivo homônimo .fxml*/
public class SellerFormController implements Initializable {

    private Seller entity;	// entidade que terá seus valores editados para inserção ou atualização conforme prevê a caixa de diálogo em questão
    private SellerService service;	// serviço necessário para acessar o bd
    private DepartmentService departmentService; // serviço necessário por causa da dependência entre vendedor e departamento
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();	// ??

    /* ATRIBUTOS e MÉTODOS com a notação @FXML são aqueles que ligam os elementos gráficos da janela gráfica
    SellerForm.fxml a esta classe, isto é, ao seu controller */
    
    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML /* Tipo específico para preencher datas */
    private DatePicker dpBirthDate;

    @FXML
    private TextField txtBaseSalary;

    @FXML
    private ComboBox<Department> comboBoxDepartment;

    @FXML
    private Label labelErrorName;

    @FXML
    private Label labelErrorEmail;

    @FXML
    private Label labelErrorBirthDate;

    @FXML
    private Label labelErrorBaseSalary;

    @FXML
    private Button btSave;

    @FXML
    private Button btCancel;

    private ObservableList<Department> obsList;

    public void setEntity(Seller entity) {
	this.entity = entity;
    }

    /*Método para configurar os serviços necessários */
    public void setServices(SellerService service, DepartmentService depService) {
	this.service = service;
	this.departmentService = depService;
    }

    /* ??? ainda não coompreendido */
    public void subscribeDataChangeListener(DataChangeListener listener) {
	dataChangeListeners.add(listener);
    }

    @FXML
    /* Método-ação do botão salvar */
    public void onBtSaveAction(ActionEvent event) { // mantém a referência ao evento anterior à caixa de diálogo
	/* Como as injeções de dependência são feitas manualmente, é importante programar defesas*/
	if (entity == null) {
	    throw new IllegalStateException("Entity was null");
	}
	if (service == null) {
	    throw new IllegalStateException("Service was null");
	}

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

    @FXML
    /* Método-ação do botão cancelar */
    public void onBtCancelAction(ActionEvent event) {
	Utils.currentStage(event).close();
    }

    @Override
    /* Primeiro método a ser executado quando a caixa de diálogo é aberta */
    public void initialize(URL location, ResourceBundle resources) {
	initializeNodes();  // método que estabelece restrições quanto ao preenchimento dos campos da caixa de diálogo
	/* outros métodos necessários */
    }

    private void initializeNodes() {
	Constraints.setTextFieldInteger(txtId);	// restrição: campo do Id aceita somente inteiros
	Constraints.setTextFieldMaxLength(txtName, 70);	// restrição: campo do Nome aceita no máximo 70 caracteres
	Constraints.setTextFieldMaxLength(txtEmail, 60);
	Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");  // restrição: formato de datas
	Constraints.setTextFieldDouble(txtBaseSalary);	// restrição: campo aceita somente números com ponto flutuante
	initializeComboBoxDepartment();	// método que inicializa o comboBoxDepartment
    }

    /* Método para atualizar os campos do formulário. Para um formulário de cadastro,
    os valores serão nulos ou pré-definidos. Para um formulário de atualização, os valores
    serão oriundos do bd através de um objeto instanciado com tais valores */
    public void updateFormData() {
	if (entity == null) // programação defensiva
	{
	    throw new IllegalStateException("Entity was null");
	}
	txtId.setText(String.valueOf(entity.getId()));	// transformando em string e colocando no campo específico
	txtName.setText(entity.getName());
	txtEmail.setText(entity.getEmail());
	Locale.setDefault(Locale.US);
	txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary())); // transformando Double em String
	if (entity.getBirthDate() != null) // verificação para novo ou atualização
	    dpBirthDate.setValue(LocalDateTime.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()).toLocalDate()); // atualização. Obtendo data do objeto e transformando-a em um objeto DatePicker
	if (entity.getDepartment() == null) // para cadastro
	    comboBoxDepartment.getSelectionModel().selectFirst(); // pré-definição: o ComboBox fica setado no primeiro valor
	else // para atualização
	    comboBoxDepartment.setValue(entity.getDepartment()); // o ComboBox seta no valor armazenado no bd
    }

    /* Método que obtém os dados de todos os campos e armazena em objeto para ser armazenado no bd */
    private Seller getFormData() {
	Seller obj = new Seller();
	ValidationException exception = new ValidationException("Validation error");	// exceção personalizada de modo a ser possível visualizar o erro respectivo a cada campo do formulário

	obj.setId(Utils.tryParseToInt(txtId.getText()));    // transforma o valor do Id de String para Integer
	
	if (txtName.getText() == null || txtName.getText().trim().equals("")) // se o campo Nome estiver vazio ou com somente espaços em branco
	    exception.addError("name", "Campo não pode ser vazio"); // adiciona este erro à lista (no caso deste projeto será possível somente este erro)
	obj.setName(txtName.getText());	// embora o dado possa estar inválido, ele ainda é setado
	
	if (txtEmail.getText() == null || txtEmail.getText().trim().equals(""))	// se o campo Nome estiver vazio ou com somente espaços em branco
	    exception.addError("email", "Campo não pode ser vazio"); // adiciona este erro à lista (no caso deste projeto será possível somente este erro)
	obj.setEmail(txtEmail.getText());	// embora o dado possa estar inválido, ele ainda é setado
	
	if (dpBirthDate.getValue() == null)
	    exception.addError("birthDate", "Campo não pode ser vazio");
	else {
	    Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
	    obj.setBirthDate(Date.from(instant));
	}
	
	if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals(""))
	    exception.addError("baseSalary", "Campo não pode ser vazio");
	obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
	
	obj.setDepartment(comboBoxDepartment.getValue());
	
	if (exception.getErrors().size() > 0) // se houver pelo menos um erro (reforçando: para este projeto será no máximo, pois o único campo que será preenchido manualmente é o Nome
	    throw exception;	// lança a exceção
	return obj; // passou pelo último if, o que quer dizer que os dados do formulário estão válidos e serão retornados para um método onde serão salvos no bd
    }

    /* Método que intermedia o acesso ao bd e carrega os objetos no comboBox */
    public void loadAssociatedObjects() {
	if (departmentService == null) { // programação defensiva
	    throw new IllegalStateException("DepartmentService was null");
	}
	List<Department> list = departmentService.findAll(); // listando do bd
	obsList = FXCollections.observableArrayList(list);  // transformando em tipo observável
	comboBoxDepartment.setItems(obsList);	// configurando no comboBox
    }

    /* Método que inicializa o comboBox */
    private void initializeComboBoxDepartment() {
	Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
	    @Override
	    protected void updateItem(Department item, boolean empty) {
		super.updateItem(item, empty);
		setText(empty ? "" : item.getName());
	    }
	};
	comboBoxDepartment.setCellFactory(factory);
	comboBoxDepartment.setButtonCell(factory.call(null));
    }

    /* ??? ainda não compreendido */
    private void notifyDataChangeListeners() {
	for (DataChangeListener listener : dataChangeListeners) {
	    listener.onDataChanged();
	}
    }

    /* Método que apresenta o erro proveniente de preenchimento inválido ao usuário */
    private void setErrorMessages(Map<String, String> errors) {
	Set<String> fields = errors.keySet();	// armazena as chaves dos erros
	
	labelErrorName.setText(fields.contains("name") ? errors.get("name") : errors.get(""));	// seta a mensagem na respectiva label
	labelErrorEmail.setText(fields.contains("email") ? errors.get("email") : errors.get(""));
	labelErrorBirthDate.setText(fields.contains("birthDate") ? errors.get("birthDate") : errors.get(""));
	labelErrorBaseSalary.setText(fields.contains("baseSalary") ? errors.get("baseSalary") : errors.get(""));
    }
}
