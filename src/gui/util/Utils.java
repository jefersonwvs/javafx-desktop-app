package gui.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class Utils {

    /* Método que retorna o palco, de onde um evento foi disparado */
    public static Stage currentStage(ActionEvent event) {
	return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    /* Método que transforma String em Integer */
    public static Integer tryParseToInt(String str) {
	try {
	    return Integer.parseInt(str);
	} catch (NumberFormatException e) {
	    return null;
	}
    }
    
    public static Double tryParseToDouble(String str) {
	try {
	    return Double.parseDouble(str);
	} catch (NumberFormatException e) {
	    return null;
	}
    }

    /* Método para formatar da maneira desejada a data a ser apresentada na TableView */
    public static <T> void formatTableColumnDate(TableColumn<T, Date> tableColumn, String format) {
	tableColumn.setCellFactory(column -> {
	    TableCell<T, Date> cell = new TableCell<T, Date>() {
		private SimpleDateFormat sdf = new SimpleDateFormat(format);

		@Override
		protected void updateItem(Date item, boolean empty) {
		    super.updateItem(item, empty);
		    if (empty) {
			setText(null);
		    } else {
			setText(sdf.format(item));
		    }
		}
	    };
	    return cell;
	});
    }

    /* Método para formatar um número com ponto plutuante com a quantidade de casas desejada */
    public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, int decimalPlaces) {
	tableColumn.setCellFactory(column -> {
	    TableCell<T, Double> cell = new TableCell<T, Double>() {
		@Override
		protected void updateItem(Double item, boolean empty) {
		    super.updateItem(item, empty);
		    if (empty) {
			setText(null);
		    } else {
			Locale.setDefault(Locale.US);
			setText(String.format("%." + decimalPlaces + "f", item));
		    }
		}
	    };
	    return cell;
	});
    }
    
    /* Método para formatar a data da maneira desejada na caixa de diálogo para cadatro de vendedor */
    public static void formatDatePicker(DatePicker datePicker, String format) {
	datePicker.setConverter(new StringConverter<LocalDate>() {
	    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);  // instanciando um formatador para o formato desejado
	    {
		datePicker.setPromptText(format.toLowerCase()); // mostrando no campo de data um texto sugestivo de como preencher. Ex: dd/mm/yyyy
	    }
	    @Override
	    public String toString(LocalDate date) {
		if (date != null) {
		    return dateFormatter.format(date); // formatando e retornando
		} else {
		    return "";
		}
	    }

	    @Override
	    public LocalDate fromString(String string) {
		if (string != null && !string.isEmpty()) {
		    return LocalDate.parse(string, dateFormatter);
		} else {
		    return null;
		}
	    }
	});
    }
}
