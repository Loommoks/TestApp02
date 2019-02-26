package su.zencode.testapp02;

import android.graphics.Bitmap;

import java.util.Comparator;
import java.util.Date;

public class GalleryItem {
    private String mId;
    private String mTitle;
    private String mText;
    private String mImageUrl;
    private int mSort;
    private Date mDate;
    private Bitmap mBitmap;

    public GalleryItem() {
        mBitmap = null;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public int getSort() {
        return mSort;
    }

    public void setSort(int sort) {
        mSort = sort;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    //Todo Переопределить toString(), при необходимости
    /** Тут
    public String toString() {
        return mTitle;
    } */

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
}
