package su.zencode.testapp02;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.redmadrobot.inputmask.MaskedTextChangedListener;

import org.jetbrains.annotations.NotNull;

public class LaunchActivity extends AppCompatActivity {
    private Button mLaunchButton;
    private EditText mPhoneField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        new FetchMaskTask().execute();

        mLaunchButton = findViewById(R.id.launch_button);
        mLaunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this, ItemsGalleryActivity.class);
                startActivity(intent);
            }
        });

        mPhoneField = findViewById(R.id.phone_number_field);
        mPasswordField = findViewById(R.id.password_field);
        //final EditText phoneField = findViewById(R.id.phone_number);
        // +7 ([000]) [000]-[00]-[00]

        /**
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
         */

        //phoneField.setHint(listener.placeholder());
        //phoneField.setHint(R.string.phone_number_field_hint);

    }

    private class FetchMaskTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            String mask = new LtechFetchr().fetchMask();
            return mask;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                setupMask(s);
            } else {
                Toast.makeText(LaunchActivity.this,
                        "Received null-phone-mask / No INTERNET connection",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void setupMask(String mask) {
        String redMadMask = LtechFetchr.parseLTechMaskToRedMad(mask);

        //final EditText phoneField = findViewById(R.id.phone_number_field);
        final MaskedTextChangedListener listener =
                MaskedTextChangedListener.Companion.installOn(
                        mPhoneField,
                        redMadMask,
                        new MaskedTextChangedListener.ValueListener() {
                            @Override
                            public void onTextChanged(boolean b, @NotNull String s) {
                                Log.d("TAG", s);
                                Log.d("TAG", String.valueOf(b));
                            }
                        }
                );
    }
}
