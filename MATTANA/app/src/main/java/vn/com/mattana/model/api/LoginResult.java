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

    @SerializedName("name")
    private String name;

    @SerializedName("code")
    private String code;

    @SerializedName("role")
    private String role;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
