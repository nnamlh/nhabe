package vn.com.mattana.di.component;

import android.app.Application;
import android.content.Context;


import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;
import vn.com.mattana.di.module.ApplicationModule;
import vn.com.mattana.di.module.NetModule;
import vn.com.mattana.di.module.UtilityModule;
import vn.com.mattana.di.scope.ApplicationContext;
import vn.com.mattana.util.Commons;
import vn.com.mattana.util.SharedPrefsHelper;

/**
 * Created by HAI on 8/10/2017.
 */

@Singleton
@Component(modules = {
        ApplicationModule.class,
        UtilityModule.class, NetModule.class
})
public interface ApplicationComponent {

    @ApplicationContext
    Context getContext();

    void inject(Application application);

    // Utility Module
    Commons getCommonUtils();


    Retrofit getRetrofit();

    SharedPrefsHelper getSharedPrefsHelper();

}