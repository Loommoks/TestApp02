package su.zencode.testapp02.LtechApiClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DevExamApiClient{
    private static final String TAG = "AuthorizeService";

    private static byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0 ) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public static String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public Bitmap loadThumbnail(String urlFull) throws IOException {
        byte[] bitmapBytes = getUrlBytes(urlFull);
        final Bitmap bitmap = BitmapFactory
                .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        Log.i(TAG, "Bitmap created from URL: " + urlFull);
        return bitmap;
    }

    public static String tryAuthorize(String phone, String password) {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .addEncoded("phone", phone)
                .addEncoded("password", password)
                .build();
        Request request = new Request.Builder()
                .url(URLsMap.Endpoints.AUTHORIZATION)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String resultString = response.body().string();
            return resultString;
        } catch (IOException e) {
            Log.e(TAG, "Failed to call POST request", e);
        }

        return null;
    }

}
