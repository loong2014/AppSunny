package sunny.player.util;

import android.util.Log;

/**
 * Created by zhangxin17 on 2018/5/17.
 */
public class Logger {

    private final String TAG;

    public Logger() {
        this("Logger");
    }

    public Logger(String tag) {
        TAG = tag;
    }

    public void i(String msg) {
        Log.i(TAG, msg);
    }

    public void e(String msg) {
        Log.e(TAG, msg);
    }

    public void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }
}
