package su.zencode.testapp02.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import su.zencode.testapp02.DevExamRepositories.Credentials;
import su.zencode.testapp02.database.AuthorizationDbScheme.CredentialsTable;

public class PairsCursorWrapper extends CursorWrapper {
    public PairsCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Credentials getPair() {
        int code = getInt(getColumnIndex(CredentialsTable.Cols.INTERNATIONAL_CODE));
        String phone = getString(getColumnIndex(CredentialsTable.Cols.PHONE));
        String password = getString(getColumnIndex(CredentialsTable.Cols.PASSWORD));

        Credentials credentials = new Credentials(
                code, phone, password);

        return credentials;
    }
}
