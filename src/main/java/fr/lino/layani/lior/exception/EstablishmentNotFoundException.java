package fr.lino.layani.lior.exception;

public class EstablishmentNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EstablishmentNotFoundException(int id) {
		super("Could not find establishment with id " + id);
	}
}