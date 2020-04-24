package gui;

import application.Main;
import gui.util.Alerts;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable {

    @FXML
    private MenuItem menuItemSeller;

    @FXML
    private MenuItem menuItemDepartment;
    
    @FXML
    private MenuItem menuItemAbout;
    
    @FXML /* Método-ação do botão Vendedores */
    public void onMenuItemSellerAction() {
	System.out.println("onMenuItemSellerAction");
    }
    
    @FXML /* Método-ação do botão Departamentos */
    public void onMenuItemDepartmentAction() {
	loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> { // carregando a view DepartamentList.fxml
	    controller.setDepartmentService(new DepartmentService());	// Ações de inicialização
	    controller.updateTableView();
	});
    }
    
    @FXML /* Método-ação do botão Sobre */
    public void onMenuItemAboutAction() {
	loadView("/gui/About.fxml", x -> {}); // carregando a view About.fxml
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    /* Para evitar a necessidade de criar funções diferentes sempre que as ações de inicialização mudarem,
    utilizamos uma função lambda para enviar a ação de inicialização por parâmetro */
    private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
	try {
	    /* Carregando o "desenho" (.fxml) em um objeto para o Java */
	    FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
	    VBox newVBox = loader.load();
	    
	    /* Obtendo a referência da minha view principal */
	    Scene mainScene = Main.getMainScene();
	    
	    /* A partir da mainScene, por meio de métodos específicos e upcastings obtemos a referência para o objeto onde será anexado o newVBox*/
	    VBox mainVBox = (VBox) (((ScrollPane) mainScene.getRoot())	// .getRoot(): pega a referência para o objeto principal, que neste caso trata-se de um ScrollPane e, por isso, foi necessário fazer upcasting
			    .getContent());				// .getContent(): pega a ref. para o conteúdo do ScrollPane
	    
	    /* Para colocar os filhos do newVBox no mainVBox, primeiro guardamos os filhos na mainVBox que queremos manter, depois limpamos e recolocamos os de interesse*/
	    Node mainMenu = mainVBox.getChildren().get(0);
	    mainVBox.getChildren().clear();
	    mainVBox.getChildren().add(mainMenu);
	    mainVBox.getChildren().addAll(newVBox.getChildren());
	    
	    T controller = loader.getController();
	    initializingAction.accept(controller);  //executa a função que veio como parâmetro
	    
	} catch (IOException ex) {
	    Alerts.showAlert("IO Exception", "Erro ao carregar view", ex.getMessage(), Alert.AlertType.ERROR);
	}
    }
}
