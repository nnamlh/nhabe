package vn.com.mattana.util;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vn.com.mattana.model.api.ResultInfo;

/**
 * Created by HAI on 2/25/2018.
 */

public interface ApiInterface {

    @GET("user/loginsession")
    Call<ResultInfo> checkSession(
            @Query("user") String user,
            @Query("token") String token);

}
