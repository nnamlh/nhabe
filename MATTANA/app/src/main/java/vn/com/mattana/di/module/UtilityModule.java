package vn.com.mattana.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import vn.com.mattana.util.Commons;

/**
 * Created by HAI on 8/10/2017.
 */
@Module
public class UtilityModule {

    @Provides
    @Singleton
    public Commons provideCommonUtils() {
        return new Commons();
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPrefs(Application mApplication) {
        return mApplication.getSharedPreferences("mattana-prefs", Context.MODE_PRIVATE);
    }

}



