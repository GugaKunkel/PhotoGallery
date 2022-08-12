package com.project.spencerkunkel.photogallery;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PhotoGalleryViewModel extends ViewModel {

    private final MutableLiveData<List<GalleryItem>> galleryItemLiveData;
    private int pageNum = 1;
    private int searchPageNum = 1;
    private String searchTerm;
    private final FlickrFetchr flickrFetchr = new FlickrFetchr(this);
    private final MutableLiveData<String> mutableSearchItem = new MutableLiveData<>();

    public PhotoGalleryViewModel() {
        mutableSearchItem.setValue("");
        this.galleryItemLiveData = (MutableLiveData<List<GalleryItem>>) Transformations.switchMap(mutableSearchItem, searchTerm -> {
            if(searchTerm.isEmpty()){
                return flickrFetchr.fetchPhotos(pageNum);
            }
            else{
                this.searchTerm = searchTerm;
                return flickrFetchr.searchPhotos(searchTerm, searchPageNum);
            }
        });
    }

    public void fetchPhotos(String query) {
        if(!query.equals(mutableSearchItem.getValue())){
            searchPageNum = 1;
        }
        pageNum = 1;
        mutableSearchItem.setValue(query);
    }

    public void getNextPage(){
        pageNum++;
        flickrFetchr.fetchPhotos(pageNum);
    }

    public void getNextSearchPage(){
        searchPageNum++;
        flickrFetchr.searchPhotos(searchTerm, searchPageNum);
    }

    public void addItems(List<GalleryItem> items, boolean isPaging){
        if(items != null){
            if(this.galleryItemLiveData.getValue() == null || !isPaging){
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
