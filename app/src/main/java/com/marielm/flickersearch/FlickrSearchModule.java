package com.marielm.flickersearch;


import com.marielm.flickersearch.activities.MainActivity;
import com.marielm.flickersearch.network.PhotoSearchService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(
        injects = {
                MainActivity.class
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

}
