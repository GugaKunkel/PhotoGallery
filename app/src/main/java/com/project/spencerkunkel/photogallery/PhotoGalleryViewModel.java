package com.project.spencerkunkel.photogallery;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

public class PhotoGalleryViewModel extends AndroidViewModel {

    private final MutableLiveData<List<GalleryItem>> galleryItemLiveData;
    private int pageNum = 1;
    private final LruCache<String, Bitmap> cache;
    private int searchPageNum = 1;
    private String searchTerm;
    private final Application app;
    private final FlickrFetchr flickrFetchr = new FlickrFetchr(this);
    private final MutableLiveData<String> mutableSearchItem = new MutableLiveData<>();

    public PhotoGalleryViewModel(Application app) {
        super(app);
        this.app = app;
        int maxMemory = (int)(Runtime.getRuntime().maxMemory() /1024);
        cache = new LruCache<String, Bitmap>(maxMemory){
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() /1024;
            }
        };
        mutableSearchItem.setValue(QueryPreferences.getInstance().getStoredQuery(app));
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
        QueryPreferences.getInstance().setStoredQuery(app, query);
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

    public final String getSearchTerm() {
        return this.mutableSearchItem.getValue() != null ? this.mutableSearchItem.getValue(): "";
    }

    public final MutableLiveData<List<GalleryItem>> getGalleryItemLiveData() {
        return this.galleryItemLiveData;
    }

    public LruCache<String, Bitmap> getCache() {
        return cache;
    }
}
