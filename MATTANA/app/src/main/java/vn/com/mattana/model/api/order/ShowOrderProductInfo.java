package vn.com.mattana.model.api.order;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HAI on 2/27/2018.
 */

public class ShowOrderProductInfo {
    @SerializedName("name")
    private String name ;

    @SerializedName("code")
    private String code ;
    @SerializedName("price")
    private String price ;
    @SerializedName("quantityBuy")
    private int quantityBuy ;
    @SerializedName("priceTotal")
    private String priceTotal ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantityBuy() {
        return quantityBuy;
    }

    public void setQuantityBuy(int quantityBuy) {
        this.quantityBuy = quantityBuy;
    }

    public String getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(String priceTotal) {
        this.priceTotal = priceTotal;
    }
}
