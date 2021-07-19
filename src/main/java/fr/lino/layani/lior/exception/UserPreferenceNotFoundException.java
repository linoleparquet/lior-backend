package fr.lino.layani.lior.exception;

public class UserPreferenceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UserPreferenceNotFoundException(int id) {
		super("Could not find user preference with id " + id);
	}
}
