package vn.com.mattana.util;

import android.location.Location;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import vn.com.mattana.model.api.checkin.CWorkInfo;
import vn.com.mattana.model.api.order.ProductInfo;
import vn.com.mattana.model.api.order.ShowOrderInfo;
import vn.com.mattana.model.data.DAgencyInfo;

/**
 * Created by HAI on 2/24/2018.
 */

public class MRes {

    private static MRes instance = null;

    protected MRes() {

    }

    public static MRes getInstance() {
        if (instance == null) {
            instance = new MRes();
        }
        return instance;
    }
    // broadcast receiver intent filters
    public final String REGISTRATION_COMPLETE = "registrationComplete";
    public final String PUSH_NOTIFICATION = "pushNotification";
    // global topic to receive app wide push notifications
    public final String TOPIC_GLOBAL = "global";
    // id to handle the notification in the notification tray
    public final int NOTIFICATION_ID = 100;
    public final String SHARED_PREF = "ah_firebase";

    // common
    public final String baseUrl = "http://221.133.7.58:68/api/";
    public final String PREF_KEY_USER = "MUSER";
    public final String PREF_KEY_TOKEN = "MTOKEN";
    public final String PREF_KEY_NAME = "MNAME";
    public final String PREF_KEY_CODE = "MCODE";
    public final String PREF_KEY_ROLE = "MROLE";
    public final String PREF_UPDATE = "LOCATION_UPDATE";

    public Location location;

    // check in
    public DAgencyInfo agency;

    public ShowOrderInfo orderInfo;

    public boolean isAdmin;
    //
    public String formatMoneyToText(double value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String moneyString = formatter.format(value);
        if (moneyString.endsWith(".00")) {
            int centsIndex = moneyString.lastIndexOf(".00");
            if (centsIndex != -1) {
                moneyString = moneyString.substring(1, centsIndex);
            }
        }

        return moneyString + " VND";
    }


    // product
    private List<ProductInfo> productOrders;

    public void addProductOrder(ProductInfo product) {
        if (productOrders == null)
            productOrders = new ArrayList<>();

        if (product != null)
            productOrders.add(product);
    }


    public List<ProductInfo> getProductOrder() {
        if (productOrders == null)
            productOrders = new ArrayList<>();

        return productOrders;
    }

    public void clearProductOrder() {
        productOrders = new ArrayList<>();
    }
    public void removeProductOrderAt(int i) {
        if (productOrders != null)
            productOrders.remove(i);
    }

    public boolean checkExistProductOrder(String code) {
        if (productOrders != null) {
            for (ProductInfo order : productOrders) {
                if (code.equals(order.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    public ProductInfo getProductOrder(String id) {
        if (productOrders != null) {
            for (ProductInfo order : productOrders) {
                if (id.equals(order.getId())) {
                    return order;
                }
            }
        }

        return null;
    }

    public int getProductOrderIndex(String id) {
        if (productOrders != null) {
            for (int i = 0; i < productOrders.size(); i++) {
                if (id.equals(productOrders.get(i).getId())) {
                    return i;
                }
            }
        }
        return 0;
    }

}
