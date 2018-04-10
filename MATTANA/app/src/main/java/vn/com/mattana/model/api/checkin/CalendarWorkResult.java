package vn.com.mattana.model.api.checkin;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import vn.com.mattana.model.api.ResultInfo;

public class CalendarWorkResult extends ResultInfo {

    @SerializedName("week")
    private int week ;

    @SerializedName("year")
    private int year ;
    @SerializedName("fDate")
    private String fDate ;

    @SerializedName("tDate")
    private String tDate ;

    @SerializedName("works")
    private List<CalendarWorkDay> works ;

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getfDate() {
        return fDate;
    }

    public void setfDate(String fDate) {
        this.fDate = fDate;
    }

    public String gettDate() {
        return tDate;
    }

    public void settDate(String tDate) {
        this.tDate = tDate;
    }

    public List<CalendarWorkDay> getWorks() {
        return works;
    }

    public void setWorks(List<CalendarWorkDay> works) {
        this.works = works;
    }
}
