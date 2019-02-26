package su.zencode.testapp02;

import android.content.Context;
import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ItemLab {
    private static ItemLab sItemLab;

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

    public void addItem (GalleryItem item) {
        mItems.add(item);
    }

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

    public boolean lisContainId(String id, List<GalleryItem> newList) {
        boolean isContain = false;
        for (GalleryItem item :
                newList) {
            if (item.getId().equals(id)) {
                isContain = true;
            }
        }
        return isContain;
    }

    public boolean currentModelContain(String id) {
        for (GalleryItem item :
                mItems) {
            if (item.getId().equals(id)){
                return true;
            }
        }
        return false;
    }

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

    public void sortItemsByServer() {
        Collections.sort(mItems, ServerSortComparator);
    }

    public void sortItemsByDate() {
        Collections.sort(mItems, DateSortComparator);
    }

}
