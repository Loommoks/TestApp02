package su.zencode.testapp02;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import su.zencode.testapp02.DevExamRepositories.Credentials;
import su.zencode.testapp02.DevExamRepositories.AuthorizationsRepository;
import su.zencode.testapp02.LtechApiClient.DevExamApiClient;

public class AuthorizeService{
    public static final String TAG = "AuthorizeService";
    private static final String LTECH_SUCCESS_AUTHORIZATION_JSON_TITLE = "success";

    Credentials mCredentials;

    public AuthorizeService(Credentials credentials) {
        mCredentials = credentials;
    }

    public static Credentials getCredentialsWithCode(Context context, int code) {
        List<Credentials> credentials =
                AuthorizationsRepository.create(context).getAll();

        for (int i = 0; i < credentials.size(); i++) {
            if (credentials.get(i).getCode() == code) {
                return credentials.get(i);
            }
        }
        return null;
    }

    public Boolean tryRemoteAuthorization() {

        String resultString = DevExamApiClient.tryAuthorize(
                mCredentials.getPhone(),
                mCredentials.getPassword());

        if (resultString == null) return null;

        try {
            JSONObject jsonResponseBody = new JSONObject(resultString);
            return jsonResponseBody.getBoolean(LTECH_SUCCESS_AUTHORIZATION_JSON_TITLE);
        } catch (JSONException jse) {
            Log.e(TAG, "Failed to parse Json response", jse);
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
