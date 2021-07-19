package fr.lino.layani.lior.service;

import fr.lino.layani.lior.exception.UserPreferenceNotFoundException;
import fr.lino.layani.lior.model.UserPreference;
import fr.lino.layani.lior.repository.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPreferenceServiceImpl implements UserPreferenceService{

    @Autowired
    UserPreferenceRepository userPreferenceRepository;

    @Override
    public UserPreference getDefaultUserPreference() {
        return getOneUserPreference(1);
    }

    @Override
    public UserPreference getOneUserPreference(int id){
        return userPreferenceRepository.findById(id).orElseThrow(() -> new UserPreferenceNotFoundException(id));
    }
}
