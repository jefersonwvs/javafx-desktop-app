package db;

/*
 * Classe para tratar exceções que são obrigatorias, como SQLException.
 * Exceção personalizada que, por herdar de RuntimeException, evita a obrigatoriedade de tratamento da exceção
 */
public class DbIntegrityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DbIntegrityException(String msg) {
	super(msg);
    }
}
