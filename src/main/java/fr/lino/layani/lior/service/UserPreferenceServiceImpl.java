package fr.lino.layani.lior.service;

import fr.lino.layani.lior.dto.UserPreferenceDto;
import fr.lino.layani.lior.exception.UserPreferenceNotFoundException;
import fr.lino.layani.lior.model.UserLocation;
import fr.lino.layani.lior.model.UserPreference;
import fr.lino.layani.lior.repository.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

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

    @Override
    public void putUpdateOneUserPreference(UserPreferenceDto userPreferenceDto) {
        UserPreference userPreference = toEntity(userPreferenceDto);
        userPreferenceRepository.save(userPreference);
    }

    @Override
    public UserPreference toEntity(UserPreferenceDto userPreferenceDto) {
        UserPreference userPreference = new UserPreference();
        userPreference.setId(userPreferenceDto.getId());
        userPreference.setMaxDestinationPerDay(userPreferenceDto.getMaxDestinationPerDay());
        userPreference.setWaitingTime(LocalTime.parse(userPreferenceDto.getWaitingTime()));
        userPreference.setLatestArrival(LocalTime.parse(userPreferenceDto.getLatestArrival()));
        userPreference.setEarliestStart(LocalTime.parse(userPreferenceDto.getEarliestStart()));
        userPreference.setName(userPreferenceDto.getName());

        UserLocation userLocation = new UserLocation();
        userLocation.setX(userPreferenceDto.getX());
        userLocation.setY(userPreferenceDto.getY());
        userLocation.setName(userPreferenceDto.getLocationName());
        userPreference.setUserLocation(userLocation);

        return userPreference;
    }

    @Override
    public UserPreferenceDto toDto(UserPreference userPreference) {
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setId(userPreference.getId());
        userPreferenceDto.setMaxDestinationPerDay(userPreference.getMaxDestinationPerDay());
        userPreferenceDto.setEarliestStart(userPreference.getEarliestStart().toString());
        userPreferenceDto.setLatestArrival(userPreference.getLatestArrival().toString());
        userPreferenceDto.setWaitingTime(userPreference.getWaitingTime().toString());
        userPreferenceDto.setName(userPreference.getName());

        if(userPreference.getUserLocation() != null) {
            UserLocation userLocation = userPreference.getUserLocation();
            userPreferenceDto.setLocationName(userLocation.getName());
            userPreferenceDto.setX(userLocation.getX());
            userPreferenceDto.setY(userLocation.getY());
        }

        return userPreferenceDto;
    }
}
