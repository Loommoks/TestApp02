package su.zencode.testapp02;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemDetailedFragment extends Fragment {

    private static final String ARG_ITEM_ID = "item_id";

    GalleryItem mItem;
    ImageView mItemIgameView;
    TextView mItemTitleView;
    TextView mItemDescriprionView;


    public static ItemDetailedFragment newInstance(String itemId) {
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, itemId);

        ItemDetailedFragment fragment = new ItemDetailedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String itemId = getArguments().getString(ARG_ITEM_ID);
        mItem = ItemLab.get(getActivity()).getItem(itemId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_detailed, container, false);

        mItemIgameView = v.findViewById(R.id.item_image);
        mItemIgameView.setImageDrawable(new BitmapDrawable(getResources(), mItem.getBitmap()));

        mItemTitleView = v.findViewById(R.id.item_text);
        mItemTitleView.setText(mItem.getTitle());

        mItemDescriprionView = v.findViewById(R.id.item_details);
        mItemDescriprionView.setText(mItem.getText());

        return v;
    }
}
