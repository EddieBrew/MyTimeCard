package com.apps.robertbrewer.stanfordtimecard;

import android.graphics.Bitmap;
import android.widget.ImageView;

import static java.sql.Types.NULL;

/**
 * Created by Robert Brewer on 11/21/2017.
 */

public class DailyInfoModel {

    private String day;
    private String date;
    private String eventName;
    private String eventNumber;
    private String time;
    private double rhours;
    private double ohours;
    DailyInfoModel(){}

    DailyInfoModel(String input){
        parseIntoVariable(input);
    }

    public String getDay() {return day; }

    public void setDay(String day) { this.day = day; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventNumber() {
        return eventNumber;
    }

    public void setEventNumber(String eventNumber) {
        this.eventNumber = eventNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String btime) {
        this.time = btime;
    }

    public double getRhours() {
        return rhours;
    }

    public void setRhours(double rhours) {
        this.rhours = rhours;
    }

    public double getOhours() {
        return ohours;
    }

    public void setOhours(double ohours) {
        this.ohours = ohours;
    }

    private void parseIntoVariable(String input){

        // DailyInfoModel entry1 = new DailyInfoModel("Day, Date, Event Num, Event Name, Start/Brgin work time, regular hours, overtime hours");

        final String COMMA_DELIMITER = ", ";
        String[] databaseInput = input.split(COMMA_DELIMITER);

        for (int i = 0; i < databaseInput.length; i++) {
            switch (i) {
                case 0:
                    this.day = databaseInput[i];
                    break;
                case 1:
                    this.date = databaseInput[i];
                    break;
                case 2:
                    this.eventNumber = databaseInput[i];
                    break;
                case 3:
                    this.eventName = databaseInput[i];
                    break;

                case 4:
                    this.time = databaseInput[i];
                    break;

                case 5:
                    this.rhours = Double.parseDouble(databaseInput[i]);
                    break;
                case 6:
                    this.ohours = Double.parseDouble(databaseInput[i]);
                    break;
                default:

            }
        }
    }
}
