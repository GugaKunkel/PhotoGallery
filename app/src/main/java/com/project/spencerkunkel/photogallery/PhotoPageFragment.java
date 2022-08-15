package com.project.spencerkunkel.photogallery;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

public class PhotoPageFragment extends VisibleFragment{

    private static final String ARG_URI = "photo_page_uri";

    private Uri uri;
    private WebView webView;
    private ProgressBar progressBar;

    public static PhotoPageFragment newInstance(Uri uri) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_URI, uri);
        PhotoPageFragment photoPageFragment = new PhotoPageFragment();
        photoPageFragment.setArguments(bundle);
        return photoPageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getParcelable(ARG_URI) != null){
            uri = getArguments().getParcelable(ARG_URI);
        }
        else{
            uri = Uri.EMPTY;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_page, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setMax(100);

        webView = view.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                FragmentActivity fragmentActivity = getActivity();
                if(fragmentActivity != null){
                    ActionBar supportActionBar = ((AppCompatActivity)fragmentActivity).getSupportActionBar();
                    if(supportActionBar != null){
                        supportActionBar.setSubtitle(title);
                    }
                }
            }
        });
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(uri.toString());
        return view;
    }
}
