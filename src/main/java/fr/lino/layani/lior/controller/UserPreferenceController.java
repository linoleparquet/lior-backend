package fr.lino.layani.lior.controller;

import fr.lino.layani.lior.model.UserPreference;
import fr.lino.layani.lior.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userPreference")
public class UserPreferenceController {

    @Autowired
    UserPreferenceService userPreferenceService;

    @GetMapping
    public UserPreference getDefaultUserPreference(){
        return userPreferenceService.getDefaultUserPreference();
    }
}
