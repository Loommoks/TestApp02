package su.zencode.testapp02;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import su.zencode.testapp02.DevExamRepositories.Credentials;
import su.zencode.testapp02.DevExamRepositories.AuthorizationsRepository;

public class AuthorizeService{
    public static final String TAG = "AuthorizeService";
    private static final String LTECH_AUTHORIZATION_URL =
            "http://dev-exam.l-tech.ru/api/v1/auth";


    Credentials mCredentials;

    public AuthorizeService(Credentials credentials) {
        mCredentials = credentials;
    }

    public static Credentials getCodeCredentials(Context context, int code) {

        List<Credentials> credentials = AuthorizationsRepository.create(context).getAll();
        for (int i = 0; i < credentials.size(); i++) {
            if (credentials.get(i).getCode() == code) {
                return credentials.get(i);
            }
        }
        return null;
    }

    public Boolean tryRemoteAuthorization() {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .addEncoded("phone", mCredentials.getPhone())
                .addEncoded("password", mCredentials.getPassword())
                .build();
        Request request = new Request.Builder()
                .url(LTECH_AUTHORIZATION_URL)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String resultString = response.body().string();
            JSONObject jsonResponseBody = new JSONObject(resultString);
            return jsonResponseBody.getBoolean("success");
        } catch (IOException e) {
            Log.e(TAG, "Failed to call POST request", e);
        } catch (JSONException jse) {
            Log.e(TAG, "Failed to parse JSON respone", jse);
        }

        return false;
    }

    @NonNull
    static String getPhoneClear(String phone) {
        return phone.replaceAll("[=\\-\\+()\\s]","");
    }

    static int getMaskCode(String mask) {
        String codeStr = mask.replaceAll("[=\\-\\+()\\sХ]","");
        int code = Integer.parseInt(codeStr);
        return code;
    }
}
