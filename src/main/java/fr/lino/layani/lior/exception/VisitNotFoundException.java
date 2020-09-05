package fr.lino.layani.lior.exception;

public class VisitNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public VisitNotFoundException(int id) {
		super("Could not find visit with id " + id);
	}
}