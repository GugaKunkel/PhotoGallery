package com.project.spencerkunkel.photogallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PhotoGalleryViewModel extends ViewModel {

    private final MutableLiveData<List<GalleryItem>> galleryItemLiveData;
    private int pageNum = 1;

    public PhotoGalleryViewModel() {
        this.galleryItemLiveData = new FlickrFetchr(this).fetchPhotos(pageNum);
    }

    public void getNextPage(){
        pageNum++;
        LiveData<List<GalleryItem>> nextPageItems = new FlickrFetchr(this).fetchPhotos(pageNum);
        if(this.galleryItemLiveData.getValue() != null && nextPageItems.getValue() != null){
            this.galleryItemLiveData.getValue().addAll(nextPageItems.getValue());
        }
    }

    public void addItems(List<GalleryItem> items){
        if(items != null){
            if(this.galleryItemLiveData.getValue() == null){
                this.galleryItemLiveData.setValue(items);
            }
            else{
                List<GalleryItem> oldItems = this.galleryItemLiveData.getValue();
                oldItems.addAll(items);
                this.galleryItemLiveData.setValue(oldItems);
            }
        }
    }

    public final MutableLiveData<List<GalleryItem>> getGalleryItemLiveData() {
        return this.galleryItemLiveData;
    }
}
