package at.ac.univie.bachelorarbeit.ss18.calltranscriber.model;

import java.io.Serializable;

public class CallInfo implements Serializable {

    private int id;
    private String name;
    private String number;
    private String date;
    private String time;
    private String fileName;

    public CallInfo(int id, String name, String number, String date, String time, String fileName) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.date = date;
        this.time = time;
        this.fileName = fileName;
    }

    public int getId() {
        return id;
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

    public String getFileName() {
        return fileName;
    }

}
