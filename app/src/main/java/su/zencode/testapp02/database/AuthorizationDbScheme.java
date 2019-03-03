package su.zencode.testapp02.database;

public class AuthorizationDbScheme {
    public static final class PairTable{
        public static final String NAME = "authorization_pairs";

        public static final class Cols {
            public static final String INTERNATIONAL_CODE = "internationalCode";
            public static final String PHONE = "phone";
            public static final String PASSWORD = "password";
        }
    }
}
