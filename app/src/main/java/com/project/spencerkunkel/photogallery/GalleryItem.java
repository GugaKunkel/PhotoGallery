package com.project.spencerkunkel.photogallery;


import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import kotlin.jvm.internal.Intrinsics;

public class GalleryItem {

    private String title;
    private String id;
    @SerializedName("url_s")
    private String url;
    @SerializedName("owner")
    private String owner;

    public GalleryItem(String title, String id, String url, String owner) {
        this.title = title;
        this.id = id;
        this.url = url;
        this.owner = owner;
    }

    public final Uri getPhotoPageUri() {
        return Uri.parse("https://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build();
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
