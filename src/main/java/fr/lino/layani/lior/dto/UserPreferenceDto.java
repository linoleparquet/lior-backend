package fr.lino.layani.lior.dto;

import lombok.Data;

@Data
public class UserPreferenceDto {
    private int id;
    private String name;
    private String earliestStart;
    private String latestArrival;
    private String waitingTime;
    private int maxDestinationPerDay;
    private String LocationName;
    private double x;
    private double y;

}
