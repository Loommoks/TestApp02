package su.zencode.testapp02;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class PostsGalleryActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return PostsGalleryFragment.newInstance();
    }
}
