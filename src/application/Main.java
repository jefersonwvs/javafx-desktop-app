package application;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {

    private static Scene mainScene; //referência "exposta", para ser possível carregar outras views na view principal
    
    @Override
    public void start(Stage primaryStage) {
	try {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));	// Obtendo configurações da cena de um arquivo de extensão .fxml
	    ScrollPane scrollPane = loader.load();	// Objeto principal (subclasse de Parent) da view
	    
	    scrollPane.setFitToHeight(true);		// Configurando ajuste do objeto ao redimensionamento da janela
	    scrollPane.setFitToWidth(true);
	    
	    mainScene = new Scene(scrollPane);	// "Configurando" a cena
	    primaryStage.setScene(mainScene);		// "Montando" a cena no palco
	    primaryStage.setTitle("Projeto - Modelo de aplicação desktop genérica");	// Título do palco
	    primaryStage.show();			// Apresentando o palco
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
    public static Scene getMainScene() { //efetiva a exposição da referência
	return mainScene;
    }

    public static void main(String[] args) {
	launch(args);
    }

}
