package vn.com.mattana.di.component;

import android.content.Context;

import dagger.Component;
import vn.com.mattana.di.module.ActivityModule;
import vn.com.mattana.di.scope.ActivityContext;
import vn.com.mattana.di.scope.ActivityScope;
import vn.com.mattana.dms.BaseActivity;

/**
 * Created by HAI on 8/10/2017.
 */

@ActivityScope
@Component(
        dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class})
public interface ActivityComponent {

    @ActivityContext
    Context getContext();

    void inject(BaseActivity activity);

}