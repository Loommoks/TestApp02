package su.zencode.testapp02;

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

import java.io.IOException;

public class ItemsGalleryFragment extends Fragment {
    private static final String TAG = "ItemsGalleryFragment";
    private RecyclerView mItemsRecyclerView;

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

        return v;
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String result = new LtechFetchr()
                        .getUrlString("http://dev-exam.l-tech.ru/api/v1/posts");
                Log.i(TAG, "Fetched content of URL: " + result);
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to fetch URL: ", ioe);
            }
            return null;
        }
    }
}
