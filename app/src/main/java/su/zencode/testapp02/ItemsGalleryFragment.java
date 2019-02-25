package su.zencode.testapp02;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ItemsGalleryFragment extends Fragment {
    private static final String TAG = "ItemsGalleryFragment";
    private RecyclerView mItemsRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<LtechItemsHolder> mThumbnailDownloader;
    private Button mServerSortButton;
    private Button mDateSortButton;

    public static ItemsGalleryFragment newInstance() {
        return new ItemsGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<LtechItemsHolder>() {
                    @Override
                    public void onThumbnailDownloaded(LtechItemsHolder itemHolder, Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        itemHolder.bindDrawable(drawable);
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_items_gallery, container, false);
        mItemsRecyclerView = v.findViewById(R.id.items_recycler_view);
        mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();
        mServerSortButton = v.findViewById(R.id.server_sort_button);
        mServerSortButton.setOnClickListener(new OnButtonClicked());
        mDateSortButton = v.findViewById(R.id.date_sort_button);
        mDateSortButton.setOnClickListener(new OnButtonClicked());
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter(){
        if(isAdded()) {
            mItemsRecyclerView.setAdapter(new LtechItemsAdapter(mItems));
        }
    }

    private class LtechItemsHolder extends RecyclerView.ViewHolder {
        private View mItemView;

        public LtechItemsHolder(View itemView) {
            super(itemView);

            mItemView = itemView;
        }

        public void bindGalleryItem(GalleryItem item) {
            TextView titleTextView = mItemView.findViewById(R.id.item_title_view);
            titleTextView.setText(item.getTitle());
            TextView detailedTextView = mItemView.findViewById(R.id.item_detailed_text);
            detailedTextView.setText(item.getText());
            TextView dateTextView = mItemView.findViewById(R.id.item_date_view);
            dateTextView.setText(item.getDate().toString());
            dateTextView.setText(parseDateforLayout(item.getDate()));
            TextView sortTextView = mItemView.findViewById(R.id.item_sort_view);
            sortTextView.setText(Integer.toString(item.getSort()));
        }

        public void bindDrawable(Drawable drawable) {
            ImageView imageView = mItemView.findViewById(R.id.item_image_view);
            imageView.setImageDrawable(drawable);
        }
    }

    private class LtechItemsAdapter extends RecyclerView.Adapter<LtechItemsHolder> {
        private List<GalleryItem> mGalleryItems;

        public LtechItemsAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @NonNull
        @Override
        public LtechItemsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            //TextView textView = new TextView(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item,viewGroup, false);
            return new LtechItemsHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LtechItemsHolder ltechItemsHolder, int i) {
            GalleryItem galleryItem = mGalleryItems.get(i);
            ltechItemsHolder.bindGalleryItem(galleryItem);
            Drawable placeHolder = getResources().getDrawable(R.drawable.in_progress_small);
            ltechItemsHolder.bindDrawable(placeHolder);
            mThumbnailDownloader.queueThumbnail(ltechItemsHolder, galleryItem.getImageUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            List<GalleryItem> itemslist = new LtechFetchr().fetchItems();

            //Collections.sort(itemslist, GalleryItem.ServerSortComparator);

            return itemslist;
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }

    public class OnButtonClicked implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.server_sort_button:
                    mServerSortButton.setEnabled(false);
                    mDateSortButton.setEnabled(true);
                    Collections.sort(mItems, GalleryItem.ServerSortComparator);
                    setupAdapter();
                    break;
                case R.id.date_sort_button:
                    mServerSortButton.setEnabled(true);
                    mDateSortButton.setEnabled(false);
                    Collections.sort(mItems, GalleryItem.DateSortComparator);
                    setupAdapter();
                    break;
            }
        }
    }

    public static String parseDateforLayout(Date date) {
        String pattern = "dd.MM.yyyy, HH:mm";
    SimpleDateFormat df = new SimpleDateFormat(pattern);
    String stringDate = df.format(date);
    return stringDate;
    }
}
