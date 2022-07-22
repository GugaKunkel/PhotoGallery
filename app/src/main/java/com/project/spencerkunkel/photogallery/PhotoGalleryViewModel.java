package com.project.spencerkunkel.photogallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PhotoGalleryViewModel extends ViewModel {

    private final LiveData<List<GalleryItem>> galleryItemLiveData;

    public PhotoGalleryViewModel() {
        this.galleryItemLiveData = new FlickrFetchr().fetchPhotos();
    }

    public final LiveData<List<GalleryItem>> getGalleryItemLiveData() {
        return this.galleryItemLiveData;
    }
}
