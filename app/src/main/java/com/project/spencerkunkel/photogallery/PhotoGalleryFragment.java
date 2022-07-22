package com.project.spencerkunkel.photogallery;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView photoRecyclerView;
    private PhotoGalleryViewModel photoGalleryViewModel;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoGalleryViewModel = new ViewModelProvider(this).get(PhotoGalleryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        photoRecyclerView = view.findViewById(R.id.photo_recycler_view);
        //photoRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); WAS USED TO SET NUMBER OF COLUMNS. MAY STILL BE NEEDED LATER

        photoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float columnWidthInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, getActivity().getResources().getDisplayMetrics());
                int width = photoRecyclerView.getWidth();
                int columnNumber = Math.round(width / columnWidthInPixels);
                photoRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), columnNumber));
                photoRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photoGalleryViewModel.getGalleryItemLiveData().observe(this.getViewLifecycleOwner(),
                galleryItems -> photoRecyclerView.setAdapter(new PhotoAdapter(galleryItems)));
    }

    private static class PhotoHolder extends RecyclerView.ViewHolder {

        private TextView title;

        public PhotoHolder(@NonNull TextView itemTextView) {
            super(itemTextView);
            this.title = itemTextView;
        }

        public void bind(String title) {
            this.title.setText(title);
        }
    }

    private static final class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private final List<GalleryItem> galleryItems;

        private PhotoAdapter(List<GalleryItem> galleryItems) {
            this.galleryItems = galleryItems;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
            GalleryItem galleryItem = galleryItems.get(position);
            holder.bind(galleryItem.getTitle());
        }

        @Override
        public int getItemCount() {
            return galleryItems.size();
        }
    }
}
