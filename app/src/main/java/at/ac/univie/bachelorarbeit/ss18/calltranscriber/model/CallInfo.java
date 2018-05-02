package at.ac.univie.bachelorarbeit.ss18.calltranscriber.model;

import java.io.Serializable;

/**
 * Represents a call with all its necessary information.
 */
public class CallInfo implements Serializable {

    /**
     * Unique id of the specific call, which will be need for deleting it.
     */
    private int id;

    /**
     * Name of the called person.
     */
    private String name;

    /**
     * Number of the called person.
     */
    private String number;

    /**
     * Date of the call. For example: 02.05.2018
     */
    private String date;

    /**
     * Time of the call. For example: 23:12:26
     */
    private String time;

    /**
     * Name of the audio file.
     */
    private String fileName;

    /**
     * Simply assigns all parameter values.
     * @param id Unique id of the specific call, which will be need for deleting it.
     * @param name Name of the called person.
     * @param number Number of the called person.
     * @param date Date of the call. For example: 02.05.2018
     * @param time Time of the call. For example: 23:12:26
     * @param fileName Name of the audio file.
     */
    public CallInfo(int id, String name, String number, String date, String time, String fileName) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.date = date;
        this.time = time;
        this.fileName = fileName;
    }

    /**
     * Simply get method for the id.
     * @return Unique id of the specific call, which will be need for deleting it.
     */
    public int getId() {
        return id;
    }

    /**
     * Simply get method for the name.
     * @return Name of the called person.
     */
    public String getName() {
        return name;
    }

    /**
     * Simply get method for the number.
     * @return Number of the called person.
     */
    public String getNumber() {
        return number;
    }

    /**
     * Simply get method for the date.
     * @return Date of the call. For example: 02.05.2018
     */
    public String getDate() {
        return date;
    }

    /**
     * Simply get method for the time.
     * @return Time of the call. For example: 23:12:26
     */
    public String getTime() {
        return time;
    }

    /**
     * Simply get method for the fileName.
     * @return Name of the audio file.
     */
    public String getFileName() {
        return fileName;
    }

}
