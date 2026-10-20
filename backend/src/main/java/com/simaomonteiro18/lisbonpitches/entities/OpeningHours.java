package com.simaomonteiro18.lisbonpitches.entities;

import com.simaomonteiro18.lisbonpitches.entities.enums.DayType;
import jakarta.persistence.*;

@Entity
@Table(name = "opening_hours",
        uniqueConstraints = @UniqueConstraint(columnNames = {"pitch_id", "day_type"}))
public class OpeningHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Pitch pitch;

    @Enumerated(EnumType.STRING)
    private DayType dayType;

    private int openHour;
    private int closeHour;

    public Long getId() {
        return id;
    }

    public Pitch getPitch() {
        return pitch;
    }

    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }

    public DayType getDayType() {
        return dayType;
    }

    public void setDayType(DayType dayType) {
        this.dayType = dayType;
    }

    public int getOpenHour() {
        return openHour;
    }

    public void setOpenHour(int openHour) {
        this.openHour = openHour;
    }

    public int getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(int closeHour) {
        this.closeHour = closeHour;
    }

}
