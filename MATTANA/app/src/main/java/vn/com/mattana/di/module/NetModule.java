package vn.com.mattana.di.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.com.mattana.util.MRes;

/**
 * Created by HAI on 8/10/2017.
 */

@Module
public class NetModule {



    public NetModule() {

    }

    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(MRes.getInstance().baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }



}
