package fr.lino.layani.lior.service;

import fr.lino.layani.lior.dto.UserPreferenceDto;
import fr.lino.layani.lior.model.UserPreference;

public interface UserPreferenceService {

    UserPreference getDefaultUserPreference();

    UserPreference getOneUserPreference(int id);

    void putUpdateOneUserPreference(UserPreferenceDto userPreferenceDto);

    UserPreference toEntity(UserPreferenceDto userPreferenceDto);

    UserPreferenceDto toDto(UserPreference userPreference);
}
