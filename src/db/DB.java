package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/*
 * Classe onde se encontram todos os métodos, atributos e demais configurações
 * necessárias para conectar com o bd, desconectar, liberar recursos etc
 */
public class DB {

    private static Connection conn = null;  // objeto para conexão com o BD

    /* Método para obter conexão com o BD */
    public static Connection getConnection() { 	
	if (conn == null) { // verifica se o BD já não está conectado
	   try {
		Properties props = loadProperties();	// carrega as propriedades do BD
		String url = props.getProperty("dburl");    // armazena a propriedade dburl em uma variável
		conn = DriverManager.getConnection(url, props);	// conectando...
	    } catch (SQLException e) {
		throw new DbException(e.getMessage());	// problema com a conexão
	    }
	}
	return conn;	// BD já estava conectado
    }

    /* Método para fechar conexão com o BD */
    public static void closeConnection() {
	if (conn != null) { // se o BD não estiver fechado
	    try {
		conn.close();	// fechando conexão
	    } catch (SQLException e) {
		throw new DbException(e.getMessage());
	    }
	}
    }

    /* Método para carregar as propriedades do BD a partir de um arquivo .properties */
    private static Properties loadProperties() {
	try (FileInputStream fs = new FileInputStream("db.properties")) { // lendo o arquivo db.properties do endereço da pasta raiz do projeto
	    Properties props = new Properties();    // instanciando um objeto properties
	    props.load(fs); // carregando o objeto com as informações lidas
	    return props;
	} catch (IOException e) {
	    throw new DbException(e.getMessage());
	}
    }

    /* Método para liberar o recurso de declaração, o comando SQL */
    public static void closeStatement(Statement st) {
	if (st != null) {   
	    try {
		st.close(); // liberando recurso
	    } catch (SQLException e) {
		throw new DbException(e.getMessage());
	    }
	}
    }

    /* Método para liberar o recurso que armazena o resultado do comando SQL */
    public static void closeResultSet(ResultSet rs) {
	if (rs != null) {
	    try {
		rs.close(); // liberando recurso
	    } catch (SQLException e) {
		throw new DbException(e.getMessage());
	    }
	}
    }
}
