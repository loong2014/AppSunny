package sunny.player.util;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by baiwenlong on 11/24/15.
 */
public class ViewUtils {
    /**
     * 获取可见的view数量
     */
    public static int getVisibleChildCount(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return 0;
        }
        int count = 0;
        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            if (viewGroup.getChildAt(i).getVisibility() == View.VISIBLE) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取view中心点在屏幕中的位置
     */
    public static int[] getViewCenterPointPos(View view) {
        if (view == null) {
            return null;
        }

        int[] viewPos = new int[2];
        view.getLocationOnScreen(viewPos);

        viewPos[0] = viewPos[0] + view.getWidth() / 2;
        viewPos[1] = viewPos[1] + view.getHeight() / 2;

        return viewPos;
    }
}
