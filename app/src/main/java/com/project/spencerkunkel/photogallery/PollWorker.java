package com.project.spencerkunkel.photogallery;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import kotlin.collections.CollectionsKt;

public class PollWorker extends Worker {

    private static final String TAG = "Pollworker";

    private final Context context;

    public PollWorker(@androidx.annotation.NonNull Context context, @androidx.annotation.NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @NonNull
    @Override
    public Result doWork() {
        String query = QueryPreferences.getInstance().getStoredQuery(context);
        String lastResultId = QueryPreferences.getInstance().getLastResultId(context);
        List<GalleryItem> items = null;
        if(query.isEmpty()){
            try {
                items = Objects.requireNonNull(new FlickrFetchr().fetchPhotosRequest(1).execute().body()).photos.galleryItems;
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        else{
            try {
                items = Objects.requireNonNull(new FlickrFetchr().searchPhotosRequest(query, 1).execute().body()).photos.galleryItems;
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        if(items == null){
            items = Collections.emptyList();
        }

        if(items.isEmpty()){
            return Result.success();
        }

        String resultId = ((GalleryItem) CollectionsKt.first(items)).getId();
        if(resultId.equals(lastResultId)){
            Log.i(TAG, "Got an old result " + resultId);
        }
        else{
            Log.i(TAG, "Got an new result " + resultId);
            QueryPreferences.getInstance().setLastResultId(context, resultId);

            Intent intent = PhotoGalleryActivity.Companion.newIntent(context);
            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);
            }
            else{
                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            }

            Resources resources = context.getResources();
            Notification notification = new NotificationCompat.
                    Builder(context, "flickr_poll")
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0,notification);
        }
        return Result.success();
    }
}
