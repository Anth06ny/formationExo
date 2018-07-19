package anthony.com.cahors2.transverse;

import android.support.annotation.StringRes;

public class MyException extends Exception {

    @StringRes
    int code;

    public MyException(@StringRes int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
