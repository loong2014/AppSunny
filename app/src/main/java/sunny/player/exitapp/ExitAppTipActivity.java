package sunny.player.exitapp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import sunny.player.R;
import sunny.player.base.BaseActivity;
import sunny.player.floating.LeChildFloatingLayerDialog;

/**
 * Created by zhangxin17 on 2018/5/4.
 */
public class ExitAppTipActivity extends BaseActivity {


    private LeChildFloatingLayerDialog mFloatingLayerDialog;

    private boolean isShowing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_exit_app);
        dealShowDialog();
    }

    private void dealShowDialog() {
        if (isShowing) {
            return;
        }
        isShowing = true;

        mFloatingLayerDialog = new LeChildFloatingLayerDialog(this);
        mFloatingLayerDialog.setListener(new LeChildFloatingLayerDialog.FloatingLayerListener() {
            @Override
            public void onShowWelcomeAnimation() {

            }

            @Override
            public void onShowBottomActivity() {

            }

            @Override
            public void onClickConfirm() {
                exitApp();
            }

            @Override
            public void onCancel() {
                exitApp();
            }

            @Override
            public int[] getTopActivityCenterPos() {
                return null;
            }
        });
        mFloatingLayerDialog.setData();
        mFloatingLayerDialog.doShowDialog();
    }

    private void exitApp() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFloatingLayerDialog != null) {
            mFloatingLayerDialog.doDismissDialog();
        }
    }
}
