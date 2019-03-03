package su.zencode.testapp02;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import su.zencode.testapp02.database.AuthorizationBaseHelper;
import su.zencode.testapp02.database.AuthorizationDbScheme.PairTable;
import su.zencode.testapp02.database.PairsCursorWrapper;

public class AuthorizationLab {
    private static AuthorizationLab sAuthorizationLab;

    private List<AuthorizationPair> mAuthorizationPairs;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private AuthorizationLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new AuthorizationBaseHelper(mContext)
                .getWritableDatabase();
        mAuthorizationPairs = new ArrayList<>();
    }

    public static AuthorizationLab get(Context context) {
        if(sAuthorizationLab == null) {
            sAuthorizationLab = new AuthorizationLab(context);
        }
        return sAuthorizationLab;
    }

    public void addPair(AuthorizationPair pair) {
        ContentValues values = getContentValues(pair);
        mDatabase.insert(PairTable.NAME, null, values);
    }

    public List<AuthorizationPair> getPairs() {
        List<AuthorizationPair> pairs = new ArrayList<>();

        PairsCursorWrapper cursor = queryPairs(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                pairs.add(cursor.getPair());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return pairs;
    }

    public AuthorizationPair getPair(int code) {
        String codeStr = Integer.toString(code);
        PairsCursorWrapper cursor = queryPairs(
                PairTable.Cols.INTERNATIONAL_CODE + " = ?",
                new String[] {codeStr}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getPair();
        } finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(AuthorizationPair pair) {
        ContentValues values = new ContentValues();
        values.put(PairTable.Cols.INTERNATIONAL_CODE, Integer.toString(pair.getInternationalCode()));
        values.put(PairTable.Cols.PHONE, pair.getPhone());
        values.put(PairTable.Cols.PASSWORD, pair.getPassword());
        return values;
    }

    private PairsCursorWrapper queryPairs(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                PairTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new PairsCursorWrapper(cursor);
    }
}
