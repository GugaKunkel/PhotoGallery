package com.project.spencerkunkel.photogallery.api;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList" +
            "&api_key=42f9fbc2c6be2772817203d0757a743f" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s")
    @NonNull
    Call<FlickrResponse> fetchPhotos();
}
