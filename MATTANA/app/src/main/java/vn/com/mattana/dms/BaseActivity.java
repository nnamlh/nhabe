package vn.com.mattana.dms;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

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
import vn.com.mattana.util.RealmController;
import vn.com.mattana.util.SharedPrefsHelper;
import vn.com.mattana.util.Utils;

/**
 * Created by HAI on 2/24/2018.
 */

public class BaseActivity extends AppCompatActivity {

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
              if (prefsHelper.get(MRes.getInstance().PREF_UPDATE, false))
              {
                  mService.getLastLocation();
                  mService.requestLocationUpdates();

                  prefsHelper.put(MRes.getInstance().PREF_UPDATE, false);
              }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    protected Location location;

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
    }

    @Override
    protected void onPause() {
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

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BaseActivity.this.location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {

                Toast.makeText(BaseActivity.this, Utils.getLocationText(location),
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    protected double getLat() {
        if (location != null)
            return location.getLatitude();
        return 0;
    }

    protected double getLng() {
        if (location != null)
            return location.getLongitude();
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


}
