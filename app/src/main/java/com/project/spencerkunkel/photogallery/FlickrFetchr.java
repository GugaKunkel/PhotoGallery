package com.project.spencerkunkel.photogallery;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.project.spencerkunkel.photogallery.api.FlickrApi;
import com.project.spencerkunkel.photogallery.api.FlickrResponse;
import com.project.spencerkunkel.photogallery.api.PhotoDeserializer;
import com.project.spencerkunkel.photogallery.api.PhotoInterceptor;
import com.project.spencerkunkel.photogallery.api.PhotoResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";

    private final FlickrApi flickrApi;
    private PhotoGalleryViewModel viewModel = null;

    public FlickrFetchr() {
        Gson gson;
        gson = new GsonBuilder().registerTypeAdapter(PhotoResponse.class, new PhotoDeserializer()).create();
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new PhotoInterceptor()).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.flickr.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        flickrApi = retrofit.create(FlickrApi.class);
    }

    public FlickrFetchr(PhotoGalleryViewModel fetchrClass) {
        Gson gson;
        gson = new GsonBuilder().registerTypeAdapter(PhotoResponse.class, new PhotoDeserializer()).create();
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new PhotoInterceptor()).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.flickr.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        flickrApi = retrofit.create(FlickrApi.class);
        this.viewModel = fetchrClass;
    }

    public final Call<FlickrResponse> fetchPhotosRequest(int page) {
        return flickrApi.fetchPhotos(page);
    }

    public LiveData<List<GalleryItem>> fetchPhotos(int page) {
        return fetchPhotoMetadata(fetchPhotosRequest(page));
    }

    public final Call<FlickrResponse> searchPhotosRequest(String query,int page) {
        return flickrApi.searchPhotos(query, page);
    }

    public LiveData<List<GalleryItem>> searchPhotos(String query, int page) {
        return fetchPhotoMetadata(searchPhotosRequest(query, page));
    }

    private MutableLiveData<List<GalleryItem>> fetchPhotoMetadata(Call<FlickrResponse> flickrRequest) {
        MutableLiveData<List<GalleryItem>> responseLiveData = new MutableLiveData<>();

        flickrRequest.enqueue(new Callback<FlickrResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<FlickrResponse> call, @NonNull Response<FlickrResponse> response) {
                Log.d(TAG, "Response received");
                FlickrResponse flickrResponse = response.body();
                PhotoResponse photoResponse = flickrResponse != null ? flickrResponse.getPhotos() : null;
                List<GalleryItem> galleryItems = photoResponse != null ? photoResponse.getGalleryItems() : new ArrayList<>();
                List<GalleryItem> filteredList = new ArrayList<>();
                for (GalleryItem item : galleryItems) {
                    if (item.getUrl() != null && !item.getUrl().isEmpty()) {
                        filteredList.add(item);
                    }
                }
                viewModel.addItems(filteredList, response.toString().contains("page") && !response.toString().contains("page=1"));
            }

            @Override
            public void onFailure(@NonNull Call<FlickrResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch photos", t);
            }
        });

        return responseLiveData;
    }
}
