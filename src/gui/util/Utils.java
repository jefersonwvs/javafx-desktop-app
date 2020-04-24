package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

    /* Método que retorna o palco, de onde um evento foi disparado */
    public static Stage currentStage(ActionEvent event) {
	return (Stage)((Node)event.getSource()).getScene().getWindow();
    }
    
    /* Método que transforma String em Integer */
    public static Integer tryParseToInt(String str) {
	try {
	    return Integer.parseInt(str);
	} catch (NumberFormatException e) {
	    return null;
	}
    }
}
