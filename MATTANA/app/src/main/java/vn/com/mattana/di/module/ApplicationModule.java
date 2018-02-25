package vn.com.mattana.di.module;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import vn.com.mattana.di.scope.ApplicationContext;

/**
 * Created by HAI on 8/10/2017.
 */

@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(final Application application) {
        mApplication = application;
    }

    @Provides
    public Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    public Context provideApplicationContext() {
        return mApplication;
    }


}