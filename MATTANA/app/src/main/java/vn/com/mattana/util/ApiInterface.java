package vn.com.mattana.util;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vn.com.mattana.model.api.ResultInfo;
import vn.com.mattana.model.api.checkin.CWorkResult;
import vn.com.mattana.model.api.order.ProductInfo;

/**
 * Created by HAI on 2/25/2018.
 */

public interface ApiInterface {

    @GET("user/loginsession")
    Call<ResultInfo> checkSession(
            @Query("user") String user,
            @Query("token") String token);


    // checkin
    @GET("calendar/showwork")
    Call<CWorkResult> showWorks(
            @Query("user") String user);

    @GET("calendar/checkin")
    Call<ResultInfo> checkIn(
            @Query("user") String user,
            @Query("token") String token, @Query("workId") String workId);


    @GET("calendar/checkout")
    Call<ResultInfo> checkOut(
            @Query("user") String user,
            @Query("token") String token, @Query("workId") String workId, @Query("notes") String notes);

    // product
    @GET("info/products")
    Call<List<ProductInfo>> products();

}
