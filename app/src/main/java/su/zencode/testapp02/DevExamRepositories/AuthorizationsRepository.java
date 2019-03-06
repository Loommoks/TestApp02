package su.zencode.testapp02.DevExamRepositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import su.zencode.testapp02.database.AuthorizationBaseHelper;
import su.zencode.testapp02.database.AuthorizationDbScheme.CredentialsTable;
import su.zencode.testapp02.database.PairsCursorWrapper;

public class AuthorizationsRepository {
    private static AuthorizationsRepository sAuthorizationLab;

    private List<Credentials> mCredentials;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private AuthorizationsRepository(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new AuthorizationBaseHelper(mContext)
                .getWritableDatabase();
        mCredentials = new ArrayList<>();
    }

    public static AuthorizationsRepository create(Context context) {
        if(sAuthorizationLab == null) {
            sAuthorizationLab = new AuthorizationsRepository(context);
        }
        return sAuthorizationLab;
    }

    public void add(Credentials pair) {
        ContentValues values = getContentValues(pair);
        mDatabase.insert(CredentialsTable.NAME, null, values);
    }

    public List<Credentials> getAll() {
        List<Credentials> pairs = new ArrayList<>();

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

    public Credentials get(int code) {
        String codeStr = Integer.toString(code);
        PairsCursorWrapper cursor = queryPairs(
                CredentialsTable.Cols.INTERNATIONAL_CODE + " = ?",
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

    private static ContentValues getContentValues(Credentials pair) {
        ContentValues values = new ContentValues();
        values.put(CredentialsTable.Cols.INTERNATIONAL_CODE, Integer.toString(pair.getCode()));
        values.put(CredentialsTable.Cols.PHONE, pair.getPhone());
        values.put(CredentialsTable.Cols.PASSWORD, pair.getPassword());
        return values;
    }

    private PairsCursorWrapper queryPairs(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CredentialsTable.NAME,
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
