package vn.com.mattana.util;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import vn.com.mattana.model.api.MainLoadResult;
import vn.com.mattana.model.api.MainLoadSend;
import vn.com.mattana.model.api.NoticeResult;
import vn.com.mattana.model.api.ResultInfo;
import vn.com.mattana.model.api.checkin.CWorkResult;
import vn.com.mattana.model.api.checkin.CalendarWorkResult;
import vn.com.mattana.model.api.checkin.CheckInResult;
import vn.com.mattana.model.api.order.CompleteSend;
import vn.com.mattana.model.api.order.ProductInfo;
import vn.com.mattana.model.api.order.ShowOrderProductInfo;
import vn.com.mattana.model.api.order.ShowOrderResult;
import vn.com.mattana.model.api.order.UpdateStatusResult;

/**
 * Created by HAI on 2/25/2018.
 */

public interface ApiInterface {

    @GET("user/loginsession")
    Call<ResultInfo> checkSession(
            @Query("user") String user,
            @Query("token") String token);

    @GET("user/savelocation")
    Call<ResultInfo> saveLocation(
            @Query("user") String user,
            @Query("name") String name, @Query("code") String code, @Query("lat") double lat, @Query("lng") double lng);


    // checkin
    @GET("calendar/showwork")
    Call<CWorkResult> showWorks(
            @Query("user") String user);

    @GET("calendar/checkin")
    Call<CheckInResult> checkIn(
            @Query("user") String user,
            @Query("token") String token, @Query("agencyId") String agencyId);


    @GET("calendar/checkout")
    Call<ResultInfo> checkOut(
            @Query("user") String user,
            @Query("token") String token, @Query("workId") String workId, @Query("notes") String notes);



    @GET("calendar/calendarwork")
    Call<CalendarWorkResult> calendarWork(
            @Query("user") String user,@Query("week") int month,@Query("year") int year);

    // product
    @GET("info/products")
    Call<List<ProductInfo>> products();

    // order
    @POST("order/createorder")
    Call<ResultInfo> createOrder(@Body CompleteSend info);

    @GET("order/showorder")
    Call<ShowOrderResult> showOrder(@Query("user") String user, @Query("fDate") String fDate, @Query("tDate") String tDate, @Query("status") String status, @Query("agency") String agency);

    @GET("order/showproductorder")
    Call<List<ShowOrderProductInfo>> showOrderProducts(@Query("orderId") String orderId);


    @GET("info/updateagencylocation")
    Call<ResultInfo> updateLocation(@Query("lat") double lat, @Query("lng") double lng, @Query("agencyCode") String agencyCode);




    // mai
    @POST("user/mainload")
    Call<MainLoadResult> mainLoad(@Body MainLoadSend info);

    @GET("user/logout")
    Call<ResultInfo> logOut(@Query("user") String user, @Query("token") String token);


    // notice
    @GET("info/notices")
    Call<List<NoticeResult>> notices(@Query("user") String user, @Query("page") int page);

    @GET("info/updatenoticeread")
    Call<ResultInfo> updateNoticeRead(@Query("id") String id);

    //
    @GET("order/updateorderstatus")
    Call<UpdateStatusResult> updateOrderStatus(@Query("user") String user,@Query("token") String token,@Query("orderId") String orderId,@Query("status") String status);

    @GET("order/updatedelivery")
    Call<ResultInfo> updateDelivery(@Query("orderId") String orderId,@Query("productId") String productId,@Query("quantity") int quantity,@Query("user") String user,@Query("token") String token);


    //
}
