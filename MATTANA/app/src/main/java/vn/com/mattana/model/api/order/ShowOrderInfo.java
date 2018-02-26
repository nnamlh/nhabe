package vn.com.mattana.model.api.order;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HAI on 2/27/2018.
 */

public class ShowOrderInfo {

    @SerializedName("orderId")
    private String orderId ;
    @SerializedName("code")
    private String code ;
    @SerializedName("status")
    private String status ;
    @SerializedName("close")
    private int close ;
    @SerializedName("orderPrice")
    private String orderPrice ;
    @SerializedName("store")
    private String store ;
    @SerializedName("agency")
    private String agency ;
    @SerializedName("address")
    private String address ;
    @SerializedName("phone")
    private String phone ;
    @SerializedName("productNumber")
    private int productNumber ;
    @SerializedName("createTime")
    private String createTime ;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public int getClose() {
        return close;
    }

    public void setClose(int close) {
        this.close = close;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(int productNumber) {
        this.productNumber = productNumber;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
