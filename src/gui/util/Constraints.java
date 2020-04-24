package gui.util;

import javafx.scene.control.TextField;

public class Constraints {

    /* Método que restringe o preenchimento de dados de um campo a valores inteiros */
    public static void setTextFieldInteger(TextField txt) {
	txt.textProperty().addListener((obs, oldValue, newValue) -> {
	    if (newValue != null && !newValue.matches("\\d*")) {
		txt.setText(oldValue);
	    }
	});
    }

    /* Método que delimita a quantidade de caracteres de um campo */
    public static void setTextFieldMaxLength(TextField txt, int max) {
	txt.textProperty().addListener((obs, oldValue, newValue) -> {
	    if (newValue != null && newValue.length() > max) {
		txt.setText(oldValue);
	    }
	});
    }

    /* Método que restringe o preenchimento de dados de um campo a valores inteiros ou ponto flutuantes */
    public static void setTextFieldDouble(TextField txt) {
	txt.textProperty().addListener((obs, oldValue, newValue) -> {
	    if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
		txt.setText(oldValue);
	    }
	});
    }
}
