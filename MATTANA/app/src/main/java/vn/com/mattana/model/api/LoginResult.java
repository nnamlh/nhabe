package vn.com.mattana.model.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HAI on 2/25/2018.
 */

public class LoginResult extends ResultInfo {
    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private String user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}