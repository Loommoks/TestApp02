package su.zencode.testapp02.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import su.zencode.testapp02.DevExamRepositories.Credentials;
import su.zencode.testapp02.database.AuthorizationDbScheme.PairTable;

public class PairsCursorWrapper extends CursorWrapper {
    public PairsCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Credentials getPair() {
        // todo getInt | getString
        int code = getInt(getColumnIndex(PairTable.Cols.INTERNATIONAL_CODE));
        String phone = getString(getColumnIndex(PairTable.Cols.PHONE));
        String password = getString(getColumnIndex(PairTable.Cols.PASSWORD));

        Credentials pair = new Credentials(
                code, phone, password);

        return pair;
    }
}
