package vn.com.mattana.model.api.checkin;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CalendarWorkDay {

    @SerializedName("dayOfWeek")
    private String dayOfWeek ;
    @SerializedName("date")
    private String date ;
    @SerializedName("plan")
    private List<ShowCalendarAgency> plan ;

    @SerializedName("work")
    private List<ShowCalendarAgency> work ;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ShowCalendarAgency> getPlan() {
        return plan;
    }

    public void setPlan(List<ShowCalendarAgency> plan) {
        this.plan = plan;
    }

    public List<ShowCalendarAgency> getWork() {
        return work;
    }

    public void setWork(List<ShowCalendarAgency> work) {
        this.work = work;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
