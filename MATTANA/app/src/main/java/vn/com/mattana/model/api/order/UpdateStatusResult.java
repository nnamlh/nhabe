package vn.com.mattana.model.api.order;

import com.google.gson.annotations.SerializedName;

import vn.com.mattana.model.api.ResultInfo;

/**
 * Created by HAI on 3/3/2018.
 */

public class UpdateStatusResult extends ResultInfo {
    @SerializedName("status")
    private String status ;
    @SerializedName("statusCode")
    private String statusCode ;
    @SerializedName("nextStatus")
    private String nextStatus ;
    @SerializedName("nextStatusCode")
    private String nextStatusCode ;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getNextStatus() {
        return nextStatus;
    }

    public void setNextStatus(String nextStatus) {
        this.nextStatus = nextStatus;
    }

    public String getNextStatusCode() {
        return nextStatusCode;
    }

    public void setNextStatusCode(String nextStatusCode) {
        this.nextStatusCode = nextStatusCode;
    }
}
