package vn.com.mattana.dms;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import io.realm.Realm;
import retrofit2.Retrofit;
import vn.com.mattana.app.AppController;
import vn.com.mattana.di.component.ActivityComponent;
import vn.com.mattana.di.component.DaggerActivityComponent;
import vn.com.mattana.di.module.ActivityModule;
import vn.com.mattana.service.LocationUpdatesService;
import vn.com.mattana.util.ApiInterface;
import vn.com.mattana.util.Commons;
import vn.com.mattana.util.MRes;
import vn.com.mattana.util.NotificationUtils;
import vn.com.mattana.util.RealmController;
import vn.com.mattana.util.SharedPrefsHelper;
import vn.com.mattana.util.Utils;

/**
 * Created by HAI on 2/24/2018.
 */

public class BaseActivity extends AppCompatActivity {


    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private android.support.v7.app.AlertDialog.Builder dNotification;

    private static final String TAG = BaseActivity.class.getName();
    @Inject
    protected Retrofit retrofit;
    @Inject
    protected Commons commons;

    @Inject
    protected SharedPrefsHelper prefsHelper;

    protected ActivityComponent activityComponent;

    private ProgressDialog pDialog;

    protected Toolbar toolbar;

    protected Realm realmControl;

    protected String user;
    protected String token;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    protected LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;


    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();

            mBound = true;

            if (checkPermissions()) {
                if (prefsHelper.get(MRes.getInstance().PREF_UPDATE, false)) {
                    mService.getLastLocation();
                    mService.requestLocationUpdates();
                    prefsHelper.put(MRes.getInstance().PREF_UPDATE, false);
                } else  {
                    mService.requestLocationUpdates();
                }

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    protected boolean checkAdmin(){
        String role = prefsHelper.get(MRes.getInstance().PREF_KEY_ROLE, null);

        if(role == null)
            return false;

        if ("Admin".equals(role))
            return  true;

        return  false;
    }

    private void fireBaseBroadcast() {
        // show dialog notification
        dNotification = new android.support.v7.app.AlertDialog.Builder(BaseActivity.this);
        dNotification.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(MRes.getInstance().REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(MRes.getInstance().TOPIC_GLOBAL);
                } else if (intent.getAction().equals(MRes.getInstance().PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    String title = intent.getStringExtra("title");
                    showNotification(title, message);
                }
            }
        };

    }

    private void showNotification(String title, String messenge) {

        dNotification.setTitle(title);
        dNotification.setMessage(messenge);
        dNotification.show();
    }
    //   protected Location location = MRes.getInstance().location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReceiver = new MyReceiver();
        activityComponent = DaggerActivityComponent.builder().applicationComponent(((AppController) getApplication()).getApplicationComponent()).activityModule(new ActivityModule(this)).build();

        activityComponent.inject(this);
        pDialog = new ProgressDialog(BaseActivity.this);
        pDialog.setTitle("Đang xử lý...");
        pDialog.setCancelable(false);
        //
        realmControl = RealmController.getInstance().getRealm();

        user = prefsHelper.get(MRes.getInstance().PREF_KEY_USER, null);
        token = prefsHelper.get(MRes.getInstance().PREF_KEY_TOKEN, null);
        fireBaseBroadcast();

        MRes.getInstance().isAdmin = checkAdmin();
    }


    /**
     * Return the current state of the permissions needed.
     */
    protected boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    //
    protected void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // api
    protected ApiInterface apiInterface() {
        return retrofit.create(ApiInterface.class);
    }

    protected boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // dialog
    protected void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    protected void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));


        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(MRes.getInstance().REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(MRes.getInstance().PUSH_NOTIFICATION));

        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }

        super.onStop();
    }

    protected String getFirebaseReg() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(MRes.getInstance().SHARED_PREF, 0);
        String token = pref.getString("regId", "");

        return token;
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MRes.getInstance().location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (MRes.getInstance().location != null) {

                /*
                Toast.makeText(BaseActivity.this, Utils.getLocationText(MRes.getInstance().location),
                        Toast.LENGTH_SHORT).show();
                */
            }
        }
    }

    protected double getLat() {
        if (MRes.getInstance().location != null)
            return MRes.getInstance().location.getLatitude();
        return 0;
    }

    protected double getLng() {
        if (MRes.getInstance().location != null)
            return MRes.getInstance().location.getLongitude();
        return 0;
    }

    protected void showSnackbar(final int mainTextStringId, final int actionStringId,
                                View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    protected void locationRequire() {
        if (!checkLocation())
            showAlertLocation();
    }

    protected boolean checkLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return statusOfGPS;
    }

    protected void showAlertLocation() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(BaseActivity.this);
        dialog.setTitle("Enable Location")
                .setMessage("Cho phép lấy thông tin GPS từ điện thoại.")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(myIntent, REQUEST_CHECK_SETTINGS);
                    }
                })
                .setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        commons.showAlertCancel(BaseActivity.this, "Cảnh báo", "Ứng dụng cần mở GPS khi sử dụng", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    Status status = new Status(LocationSettingsStatusCodes.RESOLUTION_REQUIRED);
                                    status.startResolutionForResult(BaseActivity.this, REQUEST_CHECK_SETTINGS);

                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                            }
                        });
                        break;
                }
                break;
        }
    }

    protected int countDayInMonth(int year, int month) {
        String startDateString = String.format("%d/01/%d", month, year);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date startDate;
        try {
            startDate = df.parse(startDateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DATE, -1);

            return calendar.get(Calendar.DAY_OF_MONTH);

        } catch (ParseException e) {
            return 1;
        }
    }
}
