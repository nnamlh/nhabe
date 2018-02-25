package vn.com.mattana.app;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import vn.com.mattana.di.component.ApplicationComponent;
import vn.com.mattana.di.component.DaggerApplicationComponent;
import vn.com.mattana.di.module.ApplicationModule;
import vn.com.mattana.di.module.NetModule;
import vn.com.mattana.di.module.UtilityModule;

/**
 * Created by HAI on 2/24/2018.
 */

public class AppController extends Application {

    ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this))
                .utilityModule(new UtilityModule()).netModule(new NetModule()).build();

    }
    public ApplicationComponent getApplicationComponent() {
        return  applicationComponent;
    }
}
