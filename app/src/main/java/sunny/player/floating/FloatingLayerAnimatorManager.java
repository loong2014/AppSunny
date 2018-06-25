package sunny.player.floating;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;

import sunny.player.util.Logger;
import sunny.player.util.ViewUtils;

/**
 * Created by zhangxin17 on 2018/5/15.
 * 浮层动画管理
 */
public class FloatingLayerAnimatorManager {
    private final Logger mLogger = new Logger("FloatingLayerAnimatorManager");

    // 手指移动
    private static final float HAND_MOVE_LEFT_X = 0F; // 手指向左移动的位置，默认手指在左边
    private static final float HAND_MOVE_RIGHT_X = 308F; // 手指向右移动的位置
    private static final long HAND_MOVE_DURATION = 400; // 手指移动时长

    // 下浮层眨眼
    private static final int BOTTOM_BEAR_BLINK_INTERVAL = 100; // 眨眼间隔-ms

    public FloatingLayerAnimatorManager(Context context) {

    }

    public void doComeFront(LeChildFloatingLayerDialog.BearViewHolder bearViewHolder, AnimatorListenerAdapter listener) {

        int totalTime = 100; // 总时间的十分之一

        // 女熊
        View girlView = bearViewHolder.girlLayout;
        girlView.setPivotX(250);
        girlView.setPivotY(600);

        // 女熊旋转变大
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(girlView, "alpha", 0f, 0.5f, 1f);
        alpha1.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator rotation1 = ObjectAnimator.ofFloat(girlView, "rotation", 6, 3, 0);
        rotation1.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(girlView, "scaleX", 0.5f, 1f);
        scaleX1.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(girlView, "scaleY", 0.5f, 1f);
        scaleY1.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet girlMoveSet = new AnimatorSet();
        girlMoveSet.playTogether(alpha1, rotation1, scaleX1, scaleY1);
        girlMoveSet.setDuration(totalTime * 8);


        // 女熊跳跃
        float fromX = girlView.getTranslationX();
        float fromY = girlView.getTranslationY();
        ObjectAnimator translationX1 = ObjectAnimator.ofFloat(girlView, "translationX", fromX, fromX - 40);
        ObjectAnimator translationY1 = ObjectAnimator.ofFloat(girlView, "translationY", fromY, fromY - 30, fromY - 50, fromY);
        ObjectAnimator scaleY12 = ObjectAnimator.ofFloat(girlView, "scaleY", 1f, 1.1f, 1f);

        AnimatorSet girlJumpSet = new AnimatorSet();
        girlJumpSet.play(translationX1).with(translationY1).with(scaleY12);
        girlJumpSet.setDuration(totalTime * 4);

        AnimatorSet girlSet = new AnimatorSet();
        girlSet.play(girlMoveSet).before(girlJumpSet);


        // 男熊
        View boyView = bearViewHolder.boyLayout;
        boyView.setPivotX(250);
        boyView.setPivotY(600);

        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(boyView, "alpha", 0f, 0.5f, 1f);
        alpha2.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator rotation2 = ObjectAnimator.ofFloat(boyView, "rotation", -6, -3, 0);
        rotation2.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(boyView, "scaleX", 0.5f, 1f);
        scaleX2.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(boyView, "scaleY", 0.5f, 1f);
        scaleY2.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet boyMoveSet = new AnimatorSet();
        boyMoveSet.setDuration(totalTime * 7);
        boyMoveSet.playTogether(alpha2, scaleX2, scaleY2, rotation2);


        // set
        AnimatorSet set = new AnimatorSet();
        set.addListener(listener);
        set.play(boyMoveSet).with(girlSet);
        set.start();
    }

    /**
     * 小熊在中间摇头，眨眼睛
     */
    public void doCenterBoyBlinkAndShake(final LeChildFloatingLayerDialog.BearViewHolder bearViewHolder,
                                         AnimatorListenerAdapter listener) {

        // 男熊-头
        View boyHead = bearViewHolder.boyHead;

        // 旋转
        boyHead.setPivotX(190);
        boyHead.setPivotY(300);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(boyHead, "rotation", 0, -7, 0);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setDuration(600);

        // 眨眼睛1
        ValueAnimator blind1 = ValueAnimator.ofInt(0, 1);
        blind1.setInterpolator(new LinearInterpolator());
        blind1.setDuration(BOTTOM_BEAR_BLINK_INTERVAL);
        blind1.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                bearViewHolder.doCloseEye();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bearViewHolder.doOpenEye();
            }
        });
        blind1.setStartDelay(300);

        // 眨眼睛2
        ValueAnimator blind2 = ValueAnimator.ofInt(0, 1);
        blind2.setInterpolator(new LinearInterpolator());
        blind2.setDuration(BOTTOM_BEAR_BLINK_INTERVAL);
        blind2.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                bearViewHolder.doCloseEye();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bearViewHolder.doOpenEye();
            }
        });
        blind2.setStartDelay(1500);

        // 眨眼睛3
        ValueAnimator blind3 = ValueAnimator.ofInt(0, 1);
        blind3.setInterpolator(new LinearInterpolator());
        blind3.setDuration(BOTTOM_BEAR_BLINK_INTERVAL);
        blind3.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                bearViewHolder.doCloseEye();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bearViewHolder.doOpenEye();
            }
        });
        blind3.setStartDelay(1800);

        // 监听
        blind3.addListener(listener);

        // 组合处理
        AnimatorSet set = new AnimatorSet();
        set.playTogether(rotation, blind1, blind2, blind3);
        set.start();
    }

    /**
     * 小熊移动到下浮层
     */
    public void doCenterMoveBottom(LeChildFloatingLayerDialog.BearViewHolder bearViewHolder,
                                   LeChildFloatingLayerDialog.BottomViewHolder bottomViewHolder,
                                   AnimatorListenerAdapter listener) {

        int totalTime = 1000; // 总耗时

        View fromView = bearViewHolder.wholeLayout;
        View toView = bottomViewHolder.bearsView;

        int[] fromPos = ViewUtils.getViewCenterPointPos(fromView);
        int[] toPos = ViewUtils.getViewCenterPointPos(toView);

        int diffX = toPos[0] - fromPos[0];
        int diffY = toPos[1] - fromPos[1];

        fromView.setPivotX(fromView.getWidth() / 2);
        fromView.setPivotY(fromView.getHeight() / 2);

        // 位移
        ObjectAnimator translationX = ObjectAnimator.ofFloat(fromView, "translationX", 0, diffX);
        translationX.setInterpolator(new LinearInterpolator());

        ObjectAnimator translationY = ObjectAnimator.ofFloat(fromView, "translationY", 0, diffY);
        translationY.setInterpolator(new AnticipateInterpolator(4f));

        // 缩放
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(fromView, "scaleX", 1f, 0.4f);
        scaleX.setInterpolator(new AnticipateInterpolator(1f));


        ObjectAnimator scaleY = ObjectAnimator.ofFloat(fromView, "scaleY", 1f, 0.4f);
        scaleY.setInterpolator(new AnticipateInterpolator(2f));

        // 组合
        AnimatorSet set = new AnimatorSet();
        set.setDuration(totalTime);
        set.addListener(listener);
        set.playTogether(translationX, translationY, scaleX, scaleY);
        set.start();
    }

    /**
     * 小熊在下浮层跳动
     */
    public void doBottomBearJump(LeChildFloatingLayerDialog.BottomViewHolder bottomViewHolder) {
        View bear = bottomViewHolder.bearsView;

        bear.setPivotX(140);
        bear.setPivotY(195);
        ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(bear, "scaleY", 1.1f, 0.9f, 1f);
        scaleY1.setInterpolator(new LinearInterpolator());

        scaleY1.setDuration(300);
        scaleY1.start();
    }

    /**
     * 小熊在下浮层眨眼睛
     */
    public void doBottomBearBlind(AnimatorListenerAdapter listener) {
        ValueAnimator animator = ValueAnimator.ofInt(0, 1);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(BOTTOM_BEAR_BLINK_INTERVAL);
        animator.addListener(listener);
        animator.start();
    }

    /**
     * 下浮层移动到顶导
     */
    public void doBottomMoveTop(View view, float moveX, float moveY, AnimatorListenerAdapter listener) {

        int totalTime = 800;

        // 位移
        ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", 0f, moveX);
        translationX.setDuration(totalTime);

        ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", 0f, moveY);
        translationY.setDuration(totalTime);


        // 缩放
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.3f);
        scaleX.setDuration(totalTime);

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.3f);
        scaleY.setDuration(totalTime);


        // 渐变
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.4f);
        alpha.setDuration(totalTime / 2);
        alpha.setStartDelay(totalTime / 2);


        // 组合
        AnimatorSet set = new AnimatorSet();
        set.addListener(listener);
        set.playTogether(translationX, translationY, scaleX, scaleY, alpha);
        set.start();
    }

    /**
     * 手指向左运动
     *
     * @param view 手指view
     */
    public void doHandMoveLeft(View view) {
        if (view != null) {
            dealHandMoveX(view, HAND_MOVE_LEFT_X);
        }
    }

    /**
     * 手指向右移动
     *
     * @param view 手指view
     */
    public void doHandMoveRight(View view) {
        if (view != null) {
            dealHandMoveX(view, HAND_MOVE_RIGHT_X);
        }
    }

    /**
     * 手指水平方向移动
     *
     * @param view 需要移动的view
     * @param toX  移动到的位置
     */
    private void dealHandMoveX(View view, float toX) {
        float fromX = view.getTranslationX();

        if (fromX != toX) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", fromX, toX);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(HAND_MOVE_DURATION);
            animator.start();
        }
    }

}
