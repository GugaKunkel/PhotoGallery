package com.project.spencerkunkel.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class PhotoPageActivity extends AppCompatActivity {

    public static Intent newIntent(Context context, Uri photoPageUri){
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(photoPageUri);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_page);
        FragmentManager fm = getSupportFragmentManager();
        Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);
         if(currentFragment == null){
             Fragment fragment = PhotoPageFragment.newInstance(getIntent().getData());
             fm.beginTransaction()
                     .add(R.id.fragment_container, fragment)
                     .commit();
         }
    }

    @Override
    public void onBackPressed() {
        WebView webView = findViewById(R.id.web_view);
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }
}
