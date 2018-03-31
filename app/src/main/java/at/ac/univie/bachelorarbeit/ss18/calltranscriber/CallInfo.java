package at.ac.univie.bachelorarbeit.ss18.calltranscriber;

import java.io.Serializable;

public class CallInfo implements Serializable {

    private String name;
    private String number;
    private String dateTime;
    private String duration;

    public CallInfo(String name, String number, String dateTime, String duration) {
        this.name = name;
        this.number = number;
        this.dateTime = dateTime;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDuration() {
        return duration;
    }

}
