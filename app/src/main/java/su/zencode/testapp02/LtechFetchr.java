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

public class LtechFetchr {
    private static final String TAG = "LtechFetchr";

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

    public List<GalleryItem> fetchItems() {
        List<GalleryItem> items = new ArrayList<>();
        try {
            String url = "http://dev-exam.l-tech.ru/api/v1/posts";
            String jsonBody = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonBody);
            //JSONObject jsonBodyObject = new JSONObject(jsonBody);
            JSONArray itemJsonArray =  new JSONArray(jsonBody);
            parseItems(items, itemJsonArray);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return items;
    }

    public String fetchMask() {
        String mask = null;
        try {
            String url = "http://dev-exam.l-tech.ru/api/v1/phone_masks";
            String jsonBody = getUrlString(url);
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

    private void parseItems(List<GalleryItem> items, JSONArray jsonArray) throws JSONException {

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject itemJsonObject = jsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();

            item.setId(itemJsonObject.getString("id"));
            item.setTitle(itemJsonObject.getString("title"));
            item.setText(itemJsonObject.getString("text"));
            item.setImageUrl(itemJsonObject.getString("image"));
            item.setSort(itemJsonObject.getInt("sort"));
            String stringDate = itemJsonObject.getString("date");
            item.setDate(convertToDate(stringDate));

            items.add(item);
        }
    }

    private static Date convertToDate(String stringDate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
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
