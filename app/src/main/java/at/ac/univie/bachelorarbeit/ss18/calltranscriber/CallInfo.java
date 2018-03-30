package at.ac.univie.bachelorarbeit.ss18.calltranscriber;

import java.io.Serializable;

public class CallInfo implements Serializable {

    private String otherPersonName;
    private int otherPersonNumber;
    private String dateTime;
    private String duration;

    public CallInfo(String otherPersonName, int otherPersonNumber, String dateTime, String duration) {
        this.otherPersonName = otherPersonName;
        this.otherPersonNumber = otherPersonNumber;
        this.dateTime = dateTime;
        this.duration = duration
    }

    public String getOtherPersonName() {
        return otherPersonName;
    }

    public void setOtherPersonName(String otherPersonName) {
        this.otherPersonName = otherPersonName;
    }

    public int getOtherPersonNumber() {
        return otherPersonNumber;
    }

    public void setOtherPersonNumber(int otherPersonNumber) {
        this.otherPersonNumber = otherPersonNumber;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
