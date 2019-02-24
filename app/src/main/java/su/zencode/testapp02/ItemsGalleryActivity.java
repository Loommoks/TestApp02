package su.zencode.testapp02;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class ItemsGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return ItemsGalleryFragment.newInstance();
    }
}
