package su.zencode.testapp02;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.redmadrobot.inputmask.MaskedTextChangedListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LaunchActivity extends AppCompatActivity {
    private static final String TAG = "LaunchActivity";
    private Button mLaunchButton;
    private EditText mPhoneField;
    private EditText mPasswordField;
    private List<AuthorizationPair> mPairs;
    private int mMaskCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        mPairs = AuthorizationLab.get(this).getPairs();

        new FetchMaskTask().execute();

        mLaunchButton = findViewById(R.id.launch_button);
        mLaunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LaunchActivity.this, mPhoneField.getText()
                        + " " + mPasswordField.getText(), Toast.LENGTH_SHORT).show();

                new AuthorizeTask(
                        mPhoneField.getText().toString(),
                        mPasswordField.getText().toString())
                        .execute();
            }
        });

        mPhoneField = findViewById(R.id.phone_number_field);
        mPasswordField = findViewById(R.id.password_field);

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
                int code = getCodefromMask(s);
                mMaskCode = code;
                AuthorizationPair pair = getPairWithCode(code);
                if (pair != null){
                    mPhoneField.setText(pair.getPhone());
                    mPasswordField.setText(pair.getPassword());
                }

            } else {
                Toast.makeText(LaunchActivity.this,
                        "Received null-phone-mask / No INTERNET connection",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private class AuthorizeTask extends AsyncTask<Void,Void,Boolean> {
        String mPhone;
        String mPassword;
        int mCode;

        public AuthorizeTask(String phone, String password) {
            mCode = mMaskCode;
            mPhone = getClearPhoneBody(phone);
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String phone = mPhone;
            String password = mPassword;
            String resultString = null;

            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .addEncoded("phone", phone)
                    .addEncoded("password", password)
                    .build();
            Request request = new Request.Builder()
                    .url("http://dev-exam.l-tech.ru/api/v1/auth")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .post(body)
                    .build();

            Response response = null;
            boolean success = false;

            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                Log.e(TAG, "failed to call POST request", e);
            }

            try {
                resultString = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONObject jsonResponseBody = new JSONObject(resultString);
                success = jsonResponseBody.getBoolean("success");
            } catch (JSONException jse) {
                Log.e(TAG, "Failde to parse JSON respone", jse);
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean resultBoolean) {
            if(resultBoolean) {
                //todo add Db check & update
                if(AuthorizationLab.get(LaunchActivity.this).getPair(mCode) == null) {
                    saveAuthData(mCode, mPhone, mPassword);
                }
                Toast.makeText(LaunchActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LaunchActivity.this, ItemsGalleryActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(LaunchActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
            
        }
    }

    @NonNull
    private static String getClearPhoneBody(String phone) {
        return phone.replaceAll("[=\\-\\+()\\s]","");
    }

    private static int getCodefromMask(String mask) {
        String codeStr = mask.replaceAll("[=\\-\\+()\\sХ]","");
        int code = Integer.parseInt(codeStr);
        return code;
    }

    private AuthorizationPair getPairWithCode(int code) {

        for (int i = 0; i < mPairs.size(); i++) {
            if (mPairs.get(i).getInternationalCode() == code) {
                return mPairs.get(i);
            }
        }
        return null;
    }

    private void saveAuthData(int code, String phone, String password) {
        AuthorizationLab.get(this).addPair(
                new AuthorizationPair(
                        code,
                        phone,
                        password
                )
        );
        Toast.makeText(this, "Received pahone: " + phone + ", and password: " + password + " to save", Toast.LENGTH_SHORT).show();
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
