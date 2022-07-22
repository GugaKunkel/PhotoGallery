package com.project.spencerkunkel.photogallery.api;

public class FlickrResponse {
    public PhotoResponse photos;

    public final PhotoResponse getPhotos() {
        return this.photos;
    }

    public final void setPhotos(PhotoResponse photos) {
        this.photos = photos;
    }
}
