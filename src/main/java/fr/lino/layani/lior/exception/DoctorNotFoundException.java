package fr.lino.layani.lior.exception;

public class DoctorNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DoctorNotFoundException(int id) {
		super("Could not find doctor with id " + id);
	}
}
