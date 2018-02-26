package vn.com.mattana.model.api.checkin;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import vn.com.mattana.model.api.ResultInfo;

/**
 * Created by HAI on 2/26/2018.
 */

public class CWorkResult extends ResultInfo {

    @SerializedName("works")
    private List<CWorkInfo> works;

    public List<CWorkInfo> getWorks() {
        return works;
    }

    public void setWorks(List<CWorkInfo> works) {
        this.works = works;
    }
}
