package vn.com.mattana.util;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vn.com.mattana.model.api.LoginResult;

/**
 * Created by HAI on 2/25/2018.
 */

public interface LoginService {

    @GET("user/login")
    Call<LoginResult> basicLogin();
}
