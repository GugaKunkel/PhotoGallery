package com.project.spencerkunkel.photogallery.api;

import com.google.gson.annotations.SerializedName;
import com.project.spencerkunkel.photogallery.GalleryItem;

import java.util.List;

public class PhotoResponse {

    @SerializedName("photo")
    public List<GalleryItem> galleryItems;

    public List<GalleryItem> getGalleryItems() {
        return this.galleryItems;
    }

    public void setGalleryItems(List<GalleryItem> galleryItems) {
        this.galleryItems = galleryItems;
    }
}
