package vn.com.mattana.model.api.checkin;

import com.google.gson.annotations.SerializedName;

public class ShowCalendarAgency {

    @SerializedName("code")
    private String code ;

    @SerializedName("name")
    private String name ;

    @SerializedName("target")
    private String target ;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
