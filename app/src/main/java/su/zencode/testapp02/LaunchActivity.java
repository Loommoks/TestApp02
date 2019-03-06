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

import su.zencode.testapp02.DevExamRepositories.Credentials;
import su.zencode.testapp02.DevExamRepositories.AuthorizationsRepository;

import static su.zencode.testapp02.AuthorizeService.getPhoneClear;
import static su.zencode.testapp02.AuthorizeService.getMaskCode;
import static su.zencode.testapp02.AuthorizeService.getCredentialsWithCode;


public class LaunchActivity extends AppCompatActivity {
    private static final String TAG = "LaunchActivity";
    private Button mLaunchButton;
    private EditText mPhoneField;
    private EditText mPasswordField;
    private int mMaskCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        mPhoneField = findViewById(R.id.phone_number_field);
        mPasswordField = findViewById(R.id.password_field);
        mLaunchButton = findViewById(R.id.launch_button);
        mLaunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Credentials credentials = new Credentials(
                        mMaskCode,
                        getPhoneClear(mPhoneField.getText().toString()),
                        mPasswordField.getText().toString()
                );
                new AuthorizeTask(credentials).execute();
            }
        });

        new FetchMaskTask().execute();
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
                int code = getMaskCode(s);
                mMaskCode = code;
                Credentials pair = getCredentialsWithCode(LaunchActivity.this, code);
                if (pair != null){
                    mPhoneField.setText(pair.getPhone());
                    mPasswordField.setText(pair.getPassword());
                }

            } else {
                Log.e(TAG, "Received null-phone-mask");
            }
        }
    }

    private class AuthorizeTask extends AsyncTask<Void,Void,Boolean> {
        Credentials mCredentials;

        public AuthorizeTask(Credentials credentials) {
            mCredentials = credentials;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean success = new AuthorizeService(mCredentials)
                    .tryRemoteAuthorization();
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                if(AuthorizationsRepository.create(LaunchActivity.this)
                        .get(mCredentials.getCode()) == null) {
                    saveAuthData(mCredentials);
                }

                Intent intent = new Intent(LaunchActivity.this,
                        PostsGalleryActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(
                        LaunchActivity.this,
                        "Invalid username or password",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void saveAuthData(Credentials credentials) {
        AuthorizationsRepository.create(this).add(
                credentials);
    }

    private void setupMask(String mask) {
        String redMadMask = LtechFetchr.parseLTechMaskToRedMad(mask);

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
