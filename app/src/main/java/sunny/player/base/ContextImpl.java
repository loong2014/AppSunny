package sunny.player.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhangxin17 on 2018/5/4.
 */
public class ContextImpl {

    private static Context sContext = null;

    private ContextImpl() {

    }

    /**
     * init by {@link Application#onCreate()} method
     */
    public static void init(Context context) {
        sContext = context;
    }

    public static Context getContext() {
        return sContext;
    }


}
