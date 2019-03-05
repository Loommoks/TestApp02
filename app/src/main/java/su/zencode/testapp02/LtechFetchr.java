package su.zencode.testapp02;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import su.zencode.testapp02.DevExamRepositories.Post;

public class LtechFetchr {
    private static final String TAG = "LtechFetchr";
    private static final String LTECH_POSTS_URL = "http://dev-exam.l-tech.ru/api/v1/posts";
    private static final String LTECH_MASK_URL = "http://dev-exam.l-tech.ru/api/v1/phone_masks";
    private static final String LTECH_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
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

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<Post> fetchItems() {
        List<Post> posts = new ArrayList<>();
        try {
            String jsonBody = getUrlString(LTECH_POSTS_URL);
            Log.i(TAG, "Received JSON: " + jsonBody);
            JSONArray postJsonArray =  new JSONArray(jsonBody);
            parsePosts(posts, postJsonArray);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch posts", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return posts;
    }

    public String fetchMask() {
        String mask = null;
        try {
            String jsonBody = getUrlString(LTECH_MASK_URL);
            Log.i(TAG, "Received mask JSON:" + jsonBody);
            JSONObject maskJSONObject = new JSONObject(jsonBody);
            mask = maskJSONObject.getString("phoneMask");
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch mask", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return mask;
    }

    private void parsePosts(List<Post> posts, JSONArray jsonArray) throws JSONException {

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject itemJsonObject = jsonArray.getJSONObject(i);

            Post post = new Post();

            post.setId(itemJsonObject.getString("id"));
            post.setTitle(itemJsonObject.getString("title"));
            post.setText(itemJsonObject.getString("text"));
            post.setImageUrl(itemJsonObject.getString("image"));
            post.setSort(itemJsonObject.getInt("sort"));
            String stringDate = itemJsonObject.getString("date");
            post.setDate(convertToDate(stringDate));

            posts.add(post);
        }
    }

    private static Date convertToDate(String stringDate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(LTECH_DATE_FORMAT);
        try {
            Date date = dateFormat.parse(stringDate);
            return date;
        } catch (ParseException e) {
            Log.e(TAG, "Failed to parse Date from string", e);
        }
        return null;
    }

    public static String parseLTechMaskToRedMad(String inputMask) {
        if(inputMask == null) {
            Log.i(TAG, "Received Null mask");
            return null;
        }
        char[] inputCharArr = inputMask.toCharArray();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < inputCharArr.length; i++){
            if(inputCharArr[i] == 'Х') {
                builder.append('[');
                builder.append('0');
                while ((i+1<inputCharArr.length) && (inputCharArr[i+1] == 'Х')) {
                    builder.append('0');
                    i++;
                }
                builder.append(']');
            } else {
                builder.append(inputCharArr[i]);
            }
        }
        return builder.toString();
    }

}
