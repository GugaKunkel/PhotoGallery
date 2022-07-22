package com.project.spencerkunkel.photogallery;


import com.google.gson.annotations.SerializedName;

public class GalleryItem {

    private String title;
    private String id;
    @SerializedName("url_s")
    private String url;

    public GalleryItem(String title, String id, String url) {
        this.title = title;
        this.id = id;
        this.url = url;
    }

    public final String getTitle() {
        return this.title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final String getId() {
        return this.id;
    }

    public final void setId(String id) {
        this.id = id;
    }

    public final String getUrl() {
        return this.url;
    }

    public final void setUrl(String url) {
        this.url = url;
    }
}
