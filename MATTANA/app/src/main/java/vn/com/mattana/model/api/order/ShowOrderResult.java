package vn.com.mattana.model.api.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import vn.com.mattana.model.api.ResultInfo;

/**
 * Created by HAI on 2/27/2018.
 */

public class ShowOrderResult extends ResultInfo {


    @SerializedName("orders")
    private List<ShowOrderInfo> orders;

    public List<ShowOrderInfo> getOrders() {
        return orders;
    }

    public void setOrders(List<ShowOrderInfo> orders) {
        this.orders = orders;
    }
}
