package fr.lino.layani.lior.service;

import fr.lino.layani.lior.model.UserPreference;

public interface UserPreferenceService {

    UserPreference getDefaultUserPreference();

    UserPreference getOneUserPreference(int id);
}
