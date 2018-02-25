package vn.com.mattana.util;

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


    // common
    public final String baseUrl = "http://dmsapi.nongduochai.vn/api/";
}
