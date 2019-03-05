package su.zencode.testapp02.DevExamRepositories;

public class Credentials {
    private int mInternationalCode;
    private String mPhone;
    protected String mPassword;

    public Credentials(int internationalCode, String phone, String password) {
        mInternationalCode = internationalCode;
        mPhone = phone;
        mPassword = password;
    }

    public int getCode() {
        return mInternationalCode;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setInternationalCode(int internationalCode) {
        mInternationalCode = internationalCode;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public void setPassword(String password) {
        mPassword = password;
    }
}
