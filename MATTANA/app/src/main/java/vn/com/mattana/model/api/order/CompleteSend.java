package vn.com.mattana.model.api.order;

import java.util.List;

import vn.com.mattana.model.api.RequestInfo;

/**
 * Created by HAI on 2/26/2018.
 */

public class CompleteSend extends RequestInfo {

    private String agencyId;

    private String suggestDate;

    private List<CompleteProduct> products;

    public List<CompleteProduct> getProducts() {
        return products;
    }

    public void setProducts(List<CompleteProduct> products) {
        this.products = products;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getSuggestDate() {
        return suggestDate;
    }

    public void setSuggestDate(String suggestDate) {
        this.suggestDate = suggestDate;
    }
}
