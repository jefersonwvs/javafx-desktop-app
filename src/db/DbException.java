package db;

/*
 * Classe para tratar exceções que são obrigatorias, como SQLException.
 * Exceção personalizada que, por herdar de RuntimeException, evita a obrigatoriedade de tratamento da exceção
 */
public class DbException extends RuntimeException {

    private static final long serialVersionUID = 1L;	// número de versão padrão

    public DbException(String msg) {
	super(msg);
    }
}
