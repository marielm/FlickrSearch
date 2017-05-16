package com.marielm.flickrsearch.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PhotoSearchService {
    @GET("?api_key=6a8e0794c29c9c6641e639af0f2e0754&format=json&nojsoncallback=1&method=flickr.photos.search")
    Call<TagsResponse> getTaggedPhotos(@Query("tags") String tag);
}
