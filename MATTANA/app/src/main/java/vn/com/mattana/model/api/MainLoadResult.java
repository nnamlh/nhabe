package vn.com.mattana.model.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HAI on 3/1/2018.
 */

public class MainLoadResult extends  ResultInfo{

    @SerializedName("role")
    private String role ;
    @SerializedName("notices")
    private int notices ;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getNotices() {
        return notices;
    }

    public void setNotices(int notices) {
        this.notices = notices;
    }
}
