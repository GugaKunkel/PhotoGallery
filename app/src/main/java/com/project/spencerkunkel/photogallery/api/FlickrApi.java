package com.project.spencerkunkel.photogallery.api;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApi {
    @NonNull
    @GET("services/rest/?method=flickr.interestingness.getList" +
            "&api_key=4f721bbafa75bf6d2cb5af54f937bb70" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s")
    Call<FlickrResponse> fetchPhotos(@Query("page") int page);

    @GET("services/rest/?method=flickr.photos.search" +
            "&api_key=42f9fbc2c6be2772817203d0757a743f" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s" +
            "&safesearch=1" +
            "&sort=relevance")
    Call<FlickrResponse> searchPhotos(@Query("text") String query, @Query("page") int page);
}
