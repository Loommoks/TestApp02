package su.zencode.testapp02;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ItemLab {
    private static ItemLab sItemLab;
    private static final String TAG = ".ItemLab";

    private List<GalleryItem> mItems;

    public static ItemLab get(Context context) {
        if (sItemLab == null) {
            sItemLab = new ItemLab(context);
        }

        return sItemLab;
    }

    private ItemLab(Context context) {
        mItems = new ArrayList<>();
    }

    public void setItems(List<GalleryItem> items) {
        mItems = items;
    }

    /**
    public void addItem (GalleryItem item) {
        mItems.add(item);
    }
     */

    public List<GalleryItem> getItems() {
        return mItems;
    }

    public GalleryItem getItem(int index) {
        return mItems.get(index);
    }


    public GalleryItem getItem(String id) {
        for (GalleryItem item :
                mItems) {
            if (item.getId().equals(id)) {
                return item;
            }
        }

        return null;
    }


    public void updateItem(int position, GalleryItem newItem) {
        GalleryItem mItem = getItem(position);
        mItem.setTitle(newItem.getTitle());
        mItem.setText(newItem.getText());
        if(!mItem.getImageUrl().equals(newItem.getImageUrl())){
            Log.i(TAG, "Image URL changed");
            mItem.setImageUrl(newItem.getImageUrl());
            mItem.setBitmap(null);
        }
        mItem.setSort(newItem.getSort());
        mItem.setDate(newItem.getDate());
    }

    public static Comparator<GalleryItem> ServerSortComparator = new Comparator<GalleryItem>() {
        @Override
        public int compare(GalleryItem o1, GalleryItem o2) {
            return o1.getSort() - o2.getSort();
        }
    };

    public static Comparator<GalleryItem> DateSortComparator = new Comparator<GalleryItem>() {
        @Override
        public int compare(GalleryItem o1, GalleryItem o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    };

    /**
    public int listContainId(List<GalleryItem> newList, String id) {
        for (int i = 0; i < newList.size(); i++) {
            if (newList.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public int modelContain(String id) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getId().equals(id)){
                return i;
            }
        }
        return -1;
    }
     */

    public static String parseDateforLayout(Date date) {
        String pattern = "dd.MM.yyyy, HH:mm";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        String stringDate = df.format(date);
        return stringDate;
    }

    public void updateItemBitmap(String itemId, Bitmap bitmap) {

        for(int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getId().equals(itemId)){
                mItems.get(i).setBitmap(bitmap);
                break;
            }
        }
    }

    public void smoothMergeNewItemList(List<GalleryItem> newList, RecyclerView.Adapter adapter,
                                       Comparator<GalleryItem> comparator) {

        Collections.sort(newList, comparator);

        for(int i = 0; i < newList.size(); i++) {

            if (i == mItems.size()) {
                mItems.add(i, newList.get(i));
                adapter.notifyItemInserted(i);
                continue;
            }

            if(mItems.get(i).getId().equals(newList.get(i).getId())) {
                updateItem(i, newList.get(i));
                adapter.notifyItemChanged(i);
                continue;
            }

            if (comparator.compare(mItems.get(i), newList.get(i)) < 0) {
                mItems.remove(i);
                adapter.notifyItemRemoved(i);
                i--;
                continue;
            }

            if (comparator.compare(mItems.get(i), newList.get(i)) == 0) {
                mItems.add(i, newList.get(i));
                adapter.notifyItemInserted(i);
                continue;
            }

            if (comparator.compare(mItems.get(i), newList.get(i)) > 0) {
                mItems.add(i, newList.get(i));
                adapter.notifyItemInserted(i);
                continue;
            }

        }
    }


    public void sortItemsWith(Comparator<GalleryItem> comparator) {
        Collections.sort(mItems, comparator);
    }


}
