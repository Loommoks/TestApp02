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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class LaunchActivity extends AppCompatActivity {
    private static final String TAG = "LaunchActivity";
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
                Toast.makeText(LaunchActivity.this, mPhoneField.getText() + " " + mPasswordField.getText(), Toast.LENGTH_SHORT).show();

                new AuthorizeTask().execute(mPhoneField.getText().toString(),mPasswordField.getText().toString());

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

    private class AuthorizeTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

            String phone = strings[0].replaceAll("[=\\-\\+()\\s]","");
            String password = strings[1];
            String resultString = null;

            try {
                URL url = new URL("http://dev-exam.l-tech.ru/api/v1/auth");
                Map<String,Object> params = new LinkedHashMap<>();
                params.put("phone", phone);
                params.put("password", password);

                StringBuilder postData = urlEncode(params);
                //byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                //connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                Send(postData, connection);
                int responseCode=connection.getResponseCode();

                //connection.getOutputStream().write(postDataBytes);
                InputStream is;

                if(responseCode!=200)
                    is = connection.getErrorStream();
                else
                is = connection.getInputStream();
                Reader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;)
                    sb.append((char)c);
                String response = sb.toString();

                resultString = response;

            } catch (Exception e) {
                Log.e(TAG, "Shit happens", e);
            }



                /**
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=connection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()));
                    StringBuffer sb = new StringBuffer();
                    String line;

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);

                }

            } catch (Exception e) {
                Log.e(TAG, "Failed make POST call",e);
            }*/

            return resultString;
        }

        @NonNull
        private StringBuilder urlEncode(Map<String, Object> params) throws UnsupportedEncodingException {
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            return postData;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(LaunchActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }

    private void Send(StringBuilder postData, HttpURLConnection connection) throws IOException {
        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(postData.toString());

        writer.flush();
        writer.close();
        os.close();
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
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
