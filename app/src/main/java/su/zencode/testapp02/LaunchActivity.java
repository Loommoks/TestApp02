package su.zencode.testapp02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.redmadrobot.inputmask.MaskedTextChangedListener;

import org.jetbrains.annotations.NotNull;

public class LaunchActivity extends AppCompatActivity {
    private Button mLaunchButton;
    //private EditText mPhoneField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        mLaunchButton = findViewById(R.id.launch_button);
        mLaunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this, ItemsGalleryActivity.class);
                startActivity(intent);
            }
        });

        final EditText phoneField = findViewById(R.id.phone_number);
        // +7 ([000]) [000]-[00]-[00]

        final MaskedTextChangedListener listener =
                MaskedTextChangedListener.Companion.installOn(
                phoneField,
                "+7 ([000]) [000]-[00]-[00]",
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(boolean b, @NotNull String s) {
                        Log.d("TAG", s);
                        Log.d("TAG", String.valueOf(b));
                    }
                }
        );

        //phoneField.setHint(listener.placeholder());
        phoneField.setHint(R.string.phone_number_field_hint);

    }
}
