package fr.lino.layani.lior.controller;

import fr.lino.layani.lior.dto.DoctorDto;
import fr.lino.layani.lior.dto.UserPreferenceDto;
import fr.lino.layani.lior.model.UserPreference;
import fr.lino.layani.lior.service.UserPreferenceService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/userPreference")
public class UserPreferenceController {

    Logger LOGGER = Logger.getLogger(this.getClass().getName());

    @Autowired
    UserPreferenceService userPreferenceService;

    @GetMapping("/default")
    public UserPreferenceDto getDefaultUserPreference(){
        return userPreferenceService.toDto(userPreferenceService.getDefaultUserPreference());
    }

    @PutMapping("/{id}")
    public void putUpdateOneUserPreference(@RequestBody UserPreferenceDto userPreferenceDto)
    {
        LOGGER.info("Updating User Preference: " + userPreferenceDto);
        userPreferenceService.putUpdateOneUserPreference(userPreferenceDto);
    }


}
