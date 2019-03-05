package su.zencode.testapp02.LtechApiClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import su.zencode.testapp02.LtechFetchr;

public class DevExamApiClient{
    private static final String TAG = "AuthorizeService";

    public Bitmap loadThumbnail(String urlFull) throws IOException {
        byte[] bitmapBytes = new LtechFetchr().getUrlBytes(urlFull);
        final Bitmap bitmap = BitmapFactory
                .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        Log.i(TAG, "Bitmap created from URL: " + urlFull);
        return bitmap;
    }

    public Boolean tryAuthorize(String phone, String password) {
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


}
