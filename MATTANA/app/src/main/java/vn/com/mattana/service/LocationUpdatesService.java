package vn.com.mattana.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.com.mattana.model.api.ResultInfo;
import vn.com.mattana.util.ApiInterface;
import vn.com.mattana.util.MRes;
import vn.com.mattana.util.SharedPrefsHelper;
import vn.com.mattana.util.Utils;

public class LocationUpdatesService extends Service {

    private static final String PACKAGE_NAME =
            "vn.com.mattana.dms";

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";

    private final IBinder mBinder = new LocalBinder();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    private Handler mServiceHandler;

    /**
     * The current location.
     */
    private Location mLocation;

    ApiInterface apiService;

    SharedPrefsHelper prefsHelper;

    private boolean firstTime = true;


    public LocationUpdatesService() {
    }

    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
        firstTime = true;
        createLocationRequest();
      //  getLastLocation();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MRes.getInstance().baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService =
                retrofit.create(ApiInterface.class);


        prefsHelper = new SharedPrefsHelper(getApplication().getSharedPreferences("mattana-prefs", Context.MODE_PRIVATE));

      //  requestLocationUpdates();

    }

    private void makeUpdate(double lat, double lng) {
        String user = prefsHelper.get(MRes.getInstance().PREF_KEY_USER, null);
        String name = prefsHelper.get(MRes.getInstance().PREF_KEY_NAME, null);
        String code = prefsHelper.get(MRes.getInstance().PREF_KEY_CODE, null);

        if (!TextUtils.isEmpty(user)) {
            Call<ResultInfo> call = apiService.saveLocation(user, name, code, lat, lng);

            call.enqueue(new Callback<ResultInfo>() {
                @Override
                public void onResponse(Call<ResultInfo> call, Response<ResultInfo> response) {

                }

                @Override
                public void onFailure(Call<ResultInfo> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");

        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationUpdates() {

           Log.i(TAG, "Requesting location updates");
           Utils.setRequestingLocationUpdates(this, true);
           startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
           try {
               mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                       mLocationCallback, Looper.myLooper());

           } catch (SecurityException unlikely) {
               Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
           }


    }


    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Utils.setRequestingLocationUpdates(this, false);
            stopSelf();
            firstTime = true;
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    public void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        Log.i(TAG, "New location: " + location);

        boolean isUpdate = false;

        if (mLocation == null)
        {
            isUpdate = true;
            mLocation = location;
        }

        float distance = location.distanceTo(mLocation);

        if (distance >= 10)
            isUpdate = true;

        mLocation = location;

        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        if (firstTime)
        {
            isUpdate = true;
            firstTime = false;
        }

        // send location to server
        if (isUpdate)
            makeUpdate(location.getLatitude(), location.getLongitude());
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }


}
