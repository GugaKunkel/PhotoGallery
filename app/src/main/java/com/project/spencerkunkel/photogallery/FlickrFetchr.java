package com.project.spencerkunkel.photogallery;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.project.spencerkunkel.photogallery.api.FlickrApi;
import com.project.spencerkunkel.photogallery.api.FlickrResponse;
import com.project.spencerkunkel.photogallery.api.PhotoDeserializer;
import com.project.spencerkunkel.photogallery.api.PhotoResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";

    private final FlickrApi flickrApi;
    private PhotoGalleryViewModel viewModel;

    public FlickrFetchr(PhotoGalleryViewModel fetchrClass) {
        Gson gson = new GsonBuilder().registerTypeAdapter(PhotoResponse.class, new PhotoDeserializer()).create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.flickr.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        flickrApi = retrofit.create(FlickrApi.class);
        this.viewModel = fetchrClass;
    }

    public MutableLiveData<List<GalleryItem>> fetchPhotos(int page){
        MutableLiveData<List<GalleryItem>> responseLiveData = new MutableLiveData<>();
        Call<FlickrResponse> flickrRequest = flickrApi.fetchPhotos(page);

        flickrRequest.enqueue(new Callback<FlickrResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<FlickrResponse> call, @NonNull Response<FlickrResponse> response) {
                Log.d(TAG, "Response received");
                FlickrResponse flickrResponse = response.body();
                PhotoResponse photoResponse = flickrResponse != null ? flickrResponse.getPhotos() : null;
                List<GalleryItem> galleryItems = photoResponse != null ? photoResponse.getGalleryItems() : new ArrayList<>();
                List<GalleryItem> filteredList = new ArrayList<>();
                for(GalleryItem item: galleryItems){
                    if(!item.getUrl().isEmpty()){
                        filteredList.add(item);
                    }
                }
                viewModel.addItems(filteredList);
            }

            @Override
            public void onFailure(@NonNull Call<FlickrResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch photos", t);
            }
        });

        return responseLiveData;
    }
}
