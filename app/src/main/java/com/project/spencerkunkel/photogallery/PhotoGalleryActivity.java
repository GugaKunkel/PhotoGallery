package com.project.spencerkunkel.photogallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.jetbrains.annotations.NotNull;

public class PhotoGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        boolean isFragmentContainerEmpty = savedInstanceState == null;
        if(isFragmentContainerEmpty){
            this.getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, PhotoGalleryFragment.newInstance()).commit();
        }
    }

    public static final class Companion {
        @NotNull
        public static Intent newIntent(@NotNull Context context) {
            return new Intent(context, PhotoGalleryActivity.class);
        }

        private Companion() {
        }
    }
}