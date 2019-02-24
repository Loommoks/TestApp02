package su.zencode.testapp02;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemsGalleryFragment extends Fragment {
    private static final String TAG = "ItemsGalleryFragment";
    private RecyclerView mItemsRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();

    public static ItemsGalleryFragment newInstance() {
        return new ItemsGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_items_gallery, container, false);
        mItemsRecyclerView = v.findViewById(R.id.items_recycler_view);
        mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();
        return v;
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
            dateTextView.setText(item.getText());
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
            Drawable placeHolder = getResources().getDrawable(R.drawable.loading_thumbnail);
            ltechItemsHolder.bindDrawable(placeHolder);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {

            return new LtechFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }
}
