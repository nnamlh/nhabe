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
    public final String baseUrl = "http://221.133.7.58:68/api/";
    public final String PREF_KEY_USER = "MUSER";
    public final String PREF_KEY_TOKEN = "MTOKEN";
}
