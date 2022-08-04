package com.project.spencerkunkel.photogallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ThumbnailDownloader<T> extends HandlerThread{
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    private boolean hasQuit = false;
    private Handler requestHandler;
    private final Handler responseHandler;
    private final FlickrFetchr flickrFetchr;
    private final ConcurrentHashMap<T, String> requestMap;
    private ThumbnailDownloaderListener<T> thumbnailDownloaderListener;
    private final LifecycleObserver fragmentLifecycleObserver;
    private final LifecycleObserver viewLifecycleObserver;

    public final LifecycleObserver getFragmentLifecycleObserver() {
        return this.fragmentLifecycleObserver;
    }


    public final LifecycleObserver getViewLifecycleObserver() {
        return this.viewLifecycleObserver;
    }

    public interface ThumbnailDownloaderListener<T>{
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public void setThumbnailDownloaderListener(ThumbnailDownloaderListener<T> listener){
        thumbnailDownloaderListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        this.responseHandler = responseHandler;
        this.requestMap = new ConcurrentHashMap<>();
        this.flickrFetchr = new FlickrFetchr();
        //noinspection deprecation
        this.fragmentLifecycleObserver = new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            public final void setup() {
                Log.i(TAG, "Starting background thread");
                start();
                getLooper();
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public final void tearDown() {
                Log.i(TAG, "Destroying background thread");
                quit();
            }
        };
        //noinspection deprecation
        this.viewLifecycleObserver = new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public final void tearDown() {
                Log.i(TAG, "Clearing all requests from queue");
                requestHandler.removeMessages(MESSAGE_DOWNLOAD);
                requestMap.clear();
            }
        };
    }

    @Override
    public boolean quit(){
        hasQuit = true;
        return super.quit();
    }


    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        this.requestHandler = new Handler(){
            @SuppressWarnings("unchecked")
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD){
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL:" + requestMap.get(target));
                    ThumbnailDownloader.this.handleRequest(target);
                }
            }
        };
    }

    public void queueThumbnail(T target, String url){
        Log.i(TAG,"Got a URL: " + url);
        requestMap.put(target, url);
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
    }

    private void handleRequest(T target){
        String url = null;
        if(requestMap.get(target) != null){
            url = requestMap.get(target);
        }
        else{
            return;
        }
        Bitmap bitmap = null;
        if(flickrFetchr.fetchPhoto(url) != null){
            bitmap = flickrFetchr.fetchPhoto(url);
        }
        else{
            return;
        }

        String finalUrl = url;
        Bitmap finalBitmap = bitmap;
        responseHandler.post(() -> {
            if(!Objects.equals(requestMap.get(target), finalUrl) || hasQuit){
                return;
            }
            requestMap.remove(target);
            thumbnailDownloaderListener.onThumbnailDownloaded(target, finalBitmap);
        });
    }
}