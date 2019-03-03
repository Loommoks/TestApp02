package su.zencode.testapp02;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ItemsGalleryFragment extends Fragment {
    private static final String TAG = "ItemsGalleryFragment";
    private RecyclerView mItemsRecyclerView;
    private ThumbnailDownloader<LtechItemsHolder> mThumbnailDownloader;
    private Button mServerSortButton;
    private Button mDateSortButton;
    private ItemLab mItemLab;
    private LtechItemsAdapter mLtechItemsAdapter;
    private Comparator<GalleryItem> mComparator;
    private Timer mTimer;

    public static ItemsGalleryFragment newInstance() {
        return new ItemsGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mItemLab = ItemLab.get(getActivity());
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new FetchItemsUpdateTask().execute();
            }
        },0,10000);
        new FetchItemsTask().execute();

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<LtechItemsHolder>() {
                    @Override
                    public void onThumbnailDownloaded(LtechItemsHolder itemHolder, Bitmap bitmap, String itemId) {
                        mItemLab.updateItemBitmap(itemId, bitmap);
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

        /**
         * AppCompatActivity activity = (AppCompatActivity) getActivity();
        android.support.v7.app.ActionBar mActionBar = activity.getSupportActionBar();
        mActionBar.setTitle("Trello");
         */

        mServerSortButton = v.findViewById(R.id.server_sort_button);
        mServerSortButton.setOnClickListener(new OnButtonClicked());
        mComparator = ItemLab.ServerSortComparator;
        mServerSortButton.setEnabled(false);

        mDateSortButton = v.findViewById(R.id.date_sort_button);
        mDateSortButton.setOnClickListener(new OnButtonClicked());

        setupAdapter();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_items_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_list_button:
                new FetchItemsUpdateTask().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter(){
        if(isAdded()) {
            mLtechItemsAdapter = new LtechItemsAdapter(mItemLab.getItems());
            mItemsRecyclerView.setAdapter(mLtechItemsAdapter);
        }
    }

    private class LtechItemsHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
        private View mItemView;
        private GalleryItem mItem;

        public LtechItemsHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mItemView = itemView;
        }

        public void bindGalleryItem(GalleryItem item) {
            mItem = item;
            TextView titleTextView = mItemView.findViewById(R.id.item_title_view);
            titleTextView.setText(item.getTitle());
            TextView detailedTextView = mItemView.findViewById(R.id.item_detailed_text);
            detailedTextView.setText(item.getText());
            TextView dateTextView = mItemView.findViewById(R.id.item_date_view);
            dateTextView.setText(ItemLab.parseDateforLayout(item.getDate()));
            //todo remove below
            TextView sortTextView = mItemView.findViewById(R.id.item_sort_view);
            sortTextView.setText(Integer.toString(item.getSort()));
        }

        public void bindDrawable(Drawable drawable) {
            ImageView imageView = mItemView.findViewById(R.id.item_image_view);
            imageView.setImageDrawable(drawable);
        }

        @Override
        public void onClick(View v) {
            Intent intent = ItemDetailedActivity.newIntent(getActivity(), mItem.getId());
            startActivity(intent);
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
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item,viewGroup, false);
            return new LtechItemsHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LtechItemsHolder ltechItemsHolder, int i) {
            GalleryItem galleryItem = mGalleryItems.get(i);
            ltechItemsHolder.bindGalleryItem(galleryItem);

            if(mGalleryItems.get(i).getBitmap() == null) {
                setupThumbnailPlaceholder(ltechItemsHolder);
                getNewThumbnail(ltechItemsHolder, galleryItem);
            } else {
                setupSavedThumbnail(ltechItemsHolder, i);
            }
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

        private void getNewThumbnail(LtechItemsHolder holder, GalleryItem item) {
            mThumbnailDownloader.queueThumbnail(holder, item.getImageUrl(), item.getId());
        }

        private void setupThumbnailPlaceholder(LtechItemsHolder holder) {
            Drawable placeHolder = getResources().getDrawable(R.drawable.in_progress_small);
            holder.bindDrawable(placeHolder);
        }

        private void setupSavedThumbnail(LtechItemsHolder holder, int position) {
            holder.bindDrawable(
                    new BitmapDrawable(
                            getResources(),
                            mGalleryItems.get(position).getBitmap()
                    ));
        }
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            List<GalleryItem> itemslist = new LtechFetchr().fetchItems();
            return itemslist;
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItemLab.setItems(galleryItems);
            updateSortState();
            setupAdapter();
        }
    }

    /** copypast */
    //Todo fix.it

    private class FetchItemsUpdateTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            List<GalleryItem> itemslist = new LtechFetchr().fetchItems();
            return itemslist;
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            updateModel(galleryItems);
        }
    }

    /** end of copypast */

    private void updateModel(List<GalleryItem> galleryItems) {

        mItemLab.smoothMergeNewItemList(galleryItems, mLtechItemsAdapter, mComparator);
    }

    public class OnButtonClicked implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.server_sort_button:
                    mServerSortButton.setEnabled(false);
                    mDateSortButton.setEnabled(true);
                    mComparator = ItemLab.ServerSortComparator;
                    break;
                case R.id.date_sort_button:
                    mServerSortButton.setEnabled(true);
                    mDateSortButton.setEnabled(false);
                    mComparator = ItemLab.DateSortComparator;
                    break;
            }
            updateSortState();
        }
    }

    private void updateSortState() {
        mItemLab.sortItemsWith(mComparator);
        setupAdapter();
        //mLtechItemsAdapter.notifyDataSetChanged();
    }
}
