package vn.com.mattana.model.api.checkin;

import com.google.gson.annotations.SerializedName;

import vn.com.mattana.model.api.ResultInfo;

public class CheckInResult extends ResultInfo {

    @SerializedName("perform")
    private   int perform;

    @SerializedName("des")
    private String des;

    @SerializedName("workId")
    private String workId;

    public int getPerform() {
        return perform;
    }

    public void setPerform(int perform) {
        this.perform = perform;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }
}
