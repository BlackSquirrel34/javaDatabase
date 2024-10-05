package de.uniba.wiai.dsg.ajp.assignment2.literature.logic;

public class LiteratureDatabaseException extends Exception {

	private static final long serialVersionUID = 11145967179560427L;

	public LiteratureDatabaseException() {
		super();
	}

	public LiteratureDatabaseException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LiteratureDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public LiteratureDatabaseException(String message) {
		super(message);
	}

	public LiteratureDatabaseException(Throwable cause) {
		super(cause);
	}

}
