package at.ac.univie.bachelorarbeit.ss18.calltranscriber;

import java.io.Serializable;

public class CallInfo implements Serializable {

    private String name;
    private String number;
    private String date;
    private String time;
    private String duration;
    private String fileName;

    public CallInfo(String name, String number, String date, String time, String duration, String fileName) {
        this.name = name;
        this.number = number;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDuration() {
        return duration;
    }

    public String getFileName() {
        return fileName;
    }

}
