package su.zencode.testapp02;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ItemDetailedActivity extends SingleFragmentActivity {

    public static final String EXTRA_ITEM_ID = "su.zencode.testapp02.item_id";

    public static Intent newIntent(Context packageContext, String itemId) {
        Intent intent = new Intent(packageContext, ItemDetailedActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String itemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
        return ItemDetailedFragment.newInstance(itemId);
    }

    /**@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detailed);
    }*/
}
