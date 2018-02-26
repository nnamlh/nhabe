package vn.com.mattana.model.api.checkin;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HAI on 2/26/2018.
 */

public class CWorkInfo {

    @SerializedName("workId")
    private String workId;
    @SerializedName("store")
    private String store;
    @SerializedName("phone")
    private String phone;
    @SerializedName("address")
    private String address;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;

    @SerializedName("code")
    private String code;
    @SerializedName("inplan")
    private int inplan;
    @SerializedName("perform")
    private  int perform;

    @SerializedName("status")
    private String status;

    public int getInplan() {
        return inplan;
    }

    public void setInplan(int inplan) {
        this.inplan = inplan;
    }

    public int getPerform() {
        return perform;
    }

    public void setPerform(int perform) {
        this.perform = perform;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
