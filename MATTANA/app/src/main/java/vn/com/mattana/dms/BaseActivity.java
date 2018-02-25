package vn.com.mattana.dms;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import javax.inject.Inject;

import io.realm.Realm;
import retrofit2.Retrofit;
import vn.com.mattana.app.AppController;
import vn.com.mattana.di.component.ActivityComponent;
import vn.com.mattana.di.component.DaggerActivityComponent;
import vn.com.mattana.di.module.ActivityModule;
import vn.com.mattana.util.ApiInterface;
import vn.com.mattana.util.Commons;
import vn.com.mattana.util.RealmController;
import vn.com.mattana.util.SharedPrefsHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityComponent = DaggerActivityComponent.builder().applicationComponent(((AppController) getApplication()).getApplicationComponent()).activityModule(new ActivityModule(this)).build();

        activityComponent.inject(this);
        pDialog = new ProgressDialog(BaseActivity.this);
        pDialog.setTitle("Đang xử lý...");
        pDialog.setCancelable(false);
        //
        realmControl = RealmController.getInstance().getRealm();

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

}
