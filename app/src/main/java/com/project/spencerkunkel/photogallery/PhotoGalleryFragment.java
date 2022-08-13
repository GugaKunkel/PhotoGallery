package com.project.spencerkunkel.photogallery;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView photoRecyclerView;
    private PhotoGalleryViewModel photoGalleryViewModel;
    private ProgressDialog progressDialog;
    private PhotoAdapter adapter;
    String searchBarText = "";
    int firstItemPosition;
    int lastItemPosition;
    boolean beenRotated = false;
    List<GalleryItem> items = new ArrayList<>();

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        photoGalleryViewModel = new ViewModelProvider(this).get(PhotoGalleryViewModel.class);
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Downloading photos");
        progressDialog.setMessage("It might take a few seconds..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
                assert layout != null;
                int lastVisibleItem = layout.findLastVisibleItemPosition();
                int firstVisibleItem = layout.findFirstVisibleItemPosition();
                if (lastItemPosition != lastVisibleItem || firstItemPosition != firstVisibleItem) {
                    Log.d(TAG,"Showing item " + firstVisibleItem +" to " + lastVisibleItem);
                    lastItemPosition  = lastVisibleItem;
                    firstItemPosition = firstVisibleItem;
                    int begin = Math.max(firstVisibleItem - 10, 0);
                    int end = Math.min(lastVisibleItem + 21, items.size() - 1);
                    for (@SuppressWarnings("WrapperTypeMayBePrimitive") Integer position = begin; position <= end; position++){
                        String url = items.get(position).getUrl();
                        Glide.with(requireContext())
                                .asBitmap()
                                .load(url)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        if (!resource.isRecycled()) {
                                            photoGalleryViewModel.getCache().put(url, resource);
                                        }
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                                });
                    }
                }
                if(!recyclerView.canScrollVertically(1)){
                    if(items.size() > 18){
                        if(searchBarText.isEmpty()){
                            photoGalleryViewModel.getNextPage();
                        }
                        else{
                            photoGalleryViewModel.getNextSearchPage();
                        }
                    }
                }
            }
        });
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photoGalleryViewModel.getGalleryItemLiveData().observe(this.getViewLifecycleOwner(), galleryItems -> {
            progressDialog.dismiss();
            if(galleryItems.size() > 100 && !beenRotated){
                adapter.notifyDataSetChanged();
            }
            else{
                adapter = new PhotoAdapter(galleryItems);
                photoRecyclerView.setAdapter(adapter);
            }
            items.clear();
            items.addAll(galleryItems);
            beenRotated = false;
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                Log.d(TAG, "QueryTextSubmit: " + queryText);
                progressDialog.show();
                photoGalleryViewModel.fetchPhotos(queryText);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                Log.d(TAG, "QueryTextChange: " + queryText);
                searchBarText = queryText;
                if(queryText.isEmpty()){
                    photoGalleryViewModel.fetchPhotos(queryText);
                }
                return true;
            }
        });
        searchView.setOnSearchClickListener(SearchView -> searchView.setQuery(photoGalleryViewModel.getSearchTerm(), false));
        searchView.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                searchItem.collapseActionView();
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_item_clear) {
            PhotoGalleryViewModel viewModel = this.photoGalleryViewModel;
            if(viewModel != null){
                viewModel.fetchPhotos("");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        beenRotated = true;
    }

    private static class PhotoHolder extends RecyclerView.ViewHolder {

        private final ImageView image;

        public PhotoHolder(@NonNull ImageView itemImage) {
            super(itemImage);
            this.image = itemImage;
        }

        public void bindCache(Bitmap photo) {
            image.setImageBitmap(photo);
        }

        public void bindLoad(GalleryItem galleryItem) {
            Glide.with(image)
                    .load(galleryItem.getUrl())
                    .placeholder(R.drawable.bill_up_close)
                    .into(image);
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
            Bitmap image = photoGalleryViewModel.getCache().get(galleryItems.get(position).getUrl());
            if(image != null && !image.isRecycled()){
                holder.bindCache(image);
            }
            else{
                holder.bindLoad(galleryItems.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return galleryItems.size();
        }
    }
}
