package com.project.spencerkunkel.photogallery;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView photoRecyclerView;
    private PhotoGalleryViewModel photoGalleryViewModel;
    private ThumbnailDownloader<PhotoHolder> thumbnailDownloader;
    private int lastItemPosition;
    private PhotoAdapter adapter;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        photoGalleryViewModel = new ViewModelProvider(this).get(PhotoGalleryViewModel.class);
        Handler responseHandler = new Handler();
        thumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        thumbnailDownloader.setThumbnailDownloaderListener((ThumbnailDownloader.ThumbnailDownloaderListener<PhotoHolder>) (photoHolder, bitmap) -> {
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            photoHolder.bind(drawable);
        });
        getLifecycle().addObserver(thumbnailDownloader.getFragmentLifecycleObserver());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        photoRecyclerView = view.findViewById(R.id.photo_recycler_view);
        //photoRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); WAS USED TO SET NUMBER OF COLUMNS. MAY STILL BE NEEDED LATER

        photoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float columnWidthInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, requireActivity().getResources().getDisplayMetrics());
                int width = photoRecyclerView.getWidth();
                int columnNumber = Math.round(width / columnWidthInPixels);
                photoRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), columnNumber));
                photoRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        photoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layout = (LinearLayoutManager) recyclerView.getLayoutManager();
                if(!recyclerView.canScrollVertically(1)){
                    assert layout != null;
                    lastItemPosition = layout.findFirstVisibleItemPosition();
                    photoGalleryViewModel.getNextPage();
                }
            }
        });
        getViewLifecycleOwner().getLifecycle().addObserver(thumbnailDownloader.getViewLifecycleObserver());
        //getViewLifecycleOwnerLiveData().observe(getViewLifecycleOwner(), lifecycleOwner -> lifecycleOwner.getLifecycle().addObserver(thumbnailDownloader.getViewLifecycleObserver()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photoGalleryViewModel.getGalleryItemLiveData().observe(this.getViewLifecycleOwner(), galleryItems -> {
            if(galleryItems.size() > 100){
                adapter.addItems(galleryItems);
                //photoRecyclerView.scrollToPosition(lastItemPosition);
            }
            else{
                adapter = new PhotoAdapter(galleryItems);
                photoRecyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getViewLifecycleOwner().getLifecycle().removeObserver(thumbnailDownloader.getViewLifecycleObserver());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(thumbnailDownloader.getFragmentLifecycleObserver());
    }

    private static class PhotoHolder extends RecyclerView.ViewHolder {

        private final ImageView image;

        public PhotoHolder(@NonNull ImageView itemImage) {
            super(itemImage);
            this.image = itemImage;
        }

        public void bind(Drawable image) {
            this.image.setImageDrawable(image);
        }
    }

    private final class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private final List<GalleryItem> galleryItems;

        private PhotoAdapter(List<GalleryItem> galleryItems) {
            this.galleryItems = galleryItems;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView view = (ImageView) PhotoGalleryFragment.this.getLayoutInflater().inflate(R.layout.list_item_gallery, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
            GalleryItem galleryItem = galleryItems.get(position);
            thumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
            Drawable placeholder = ContextCompat.getDrawable(requireContext(), R.drawable.bill_up_close);
            if(placeholder == null){
                placeholder = new ColorDrawable();
            }
            holder.bind(placeholder);
        }

        public void addItems(List<GalleryItem> items){
            this.galleryItems.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return galleryItems.size();
        }
    }
}
