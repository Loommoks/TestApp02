package su.zencode.testapp02;

import android.support.v7.util.DiffUtil;

import java.util.List;

import su.zencode.testapp02.DevExamRepositories.Post;

public class PostDiffCallback extends DiffUtil.Callback {
    private final List<Post> mOldPostList;
    private final List<Post> mNewPostList;

    public PostDiffCallback(List<Post> oldPostList, List<Post> newPostList) {
        mOldPostList = oldPostList;
        mNewPostList = newPostList;
    }

    @Override
    public int getOldListSize() {
        return mOldPostList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewPostList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldPostList.get(oldItemPosition).getId()
                .equals(mNewPostList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Post oldPost = mOldPostList.get(oldItemPosition);
        final Post newPost = mNewPostList.get(newItemPosition);

        if(     oldPost.getImageUrl().equals(newPost.getImageUrl())
                && oldPost.getTitle().equals(newPost.getTitle())
                && oldPost.getText().equals(newPost.getText())
                && oldPost.getDate() == newPost.getDate()
                && oldPost.getSort() == newPost.getSort()
                ) {
            return true;
        }

        return false;
    }
}
