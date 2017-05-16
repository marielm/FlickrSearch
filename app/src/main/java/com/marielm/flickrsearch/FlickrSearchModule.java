package com.marielm.flickrsearch;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.marielm.flickrsearch.activities.MainActivity;
import com.marielm.flickrsearch.activities.SearchDialog;
import com.marielm.flickrsearch.network.PhotoSearchService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(
        injects = {
                MainActivity.class,
                SearchDialog.class
        },
        library = true
)

public class FlickrSearchModule {

    @Provides @Singleton
    public Retrofit provideRetrofitBuilder(){
        return new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/services/rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    public PhotoSearchService providePhotoSearchService(Retrofit retrofit) {
        return retrofit.create(PhotoSearchService.class);
    }

    @Provides SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(FlickrSearchApplication.context);
    }

}
