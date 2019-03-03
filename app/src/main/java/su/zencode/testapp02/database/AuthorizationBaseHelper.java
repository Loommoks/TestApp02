package su.zencode.testapp02.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import su.zencode.testapp02.database.AuthorizationDbScheme.PairTable;

public class AuthorizationBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "authorizationBase.db";

    public AuthorizationBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PairTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                PairTable.Cols.INTERNATIONAL_CODE + ", " +
                PairTable.Cols.PHONE + ", " +
                PairTable.Cols.PASSWORD +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
