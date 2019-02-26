package su.zencode.testapp02;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class ItemDetailedFragment extends Fragment {
    GalleryItem mItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String itemId = getActivity().getIntent()
                .getStringExtra(ItemDetailedActivity.EXTRA_ITEM_ID);
        // todo make ItemLab.class //mItem =
    }


}
