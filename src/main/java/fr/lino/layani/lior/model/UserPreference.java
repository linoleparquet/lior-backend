package fr.lino.layani.lior.model;

import com.graphhopper.jsprit.core.util.Coordinate;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalTime;

@Data
@Entity
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private LocalTime earliestStart;
    private LocalTime latestArrival;
    private LocalTime waitingTime;
    private int maxDestinationPerDay;
    @OneToOne(cascade=CascadeType.ALL)
    private UserLocation userLocation;
}
