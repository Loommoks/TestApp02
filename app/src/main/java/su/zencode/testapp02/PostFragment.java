package su.zencode.testapp02;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import su.zencode.testapp02.DevExamRepositories.Post;
import su.zencode.testapp02.DevExamRepositories.PostsRepository;

public class PostFragment extends Fragment {

    private static final String ARG_ITEM_ID = "item_id";

    private Post mItem;
    private ImageView mItemImageView;
    private TextView mItemTitleView;
    private TextView mItemDescriprionView;
    private ActionBar mActionBar;
    private ThumbnailDownloader<ImageView> mThumbnailDownloader;


    public static PostFragment newInstance(String itemId) {
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, itemId);

        PostFragment fragment = new PostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_detailed, container, false);

        String itemId = getArguments().getString(ARG_ITEM_ID);
        mItem = PostsRepository.get(getActivity()).getItem(itemId);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        mActionBar = activity.getSupportActionBar();
        String title = mItem.getTitle();
        mActionBar.setTitle(title);

        mItemImageView = v.findViewById(R.id.item_image);
        if(mItem.getBitmap() == null) {
            mThumbnailDownloader.setThumbnailDownloadListener(
                    new ThumbnailDownloader.ThumbnailDownloadListener<ImageView>() {
                        @Override
                        public void onThumbnailDownloaded(
                                ImageView target,boolean targetChanged, Bitmap thumbnail, String id) {
                            PostsRepository.get(getActivity()).getItem(id).setBitmap(thumbnail);
                            Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                            target.setImageDrawable(drawable);
                        }
            });
            mThumbnailDownloader.start();
            mThumbnailDownloader.getLooper();
            mThumbnailDownloader.queueThumbnail(
                    mItemImageView, mItem.getImageUrl(), mItem.getId());
        } else {
            mItemImageView.setImageDrawable(
                    new BitmapDrawable(getResources(), mItem.getBitmap()));
        }


        mItemTitleView = v.findViewById(R.id.item_text);
        mItemTitleView.setText(mItem.getTitle());

        mItemDescriprionView = v.findViewById(R.id.item_details);
        mItemDescriprionView.setText(mItem.getText());

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
    }
}
