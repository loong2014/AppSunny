package sunny.player.base;

import android.app.Application;

/**
 * Created by zhangxin17 on 2018/5/4.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextImpl.init(this);
    }
}
