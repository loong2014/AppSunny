package sunny.player.floating;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import sunny.player.R;
import sunny.player.util.Logger;
import sunny.player.util.ViewUtils;

/**
 * Created by zhangxin17 on 2018/5/15.
 * 浮层容器
 */
public class LeChildFloatingLayerDialog extends Dialog {

    private final Logger mLogger = new Logger("LeChildFloatingLayerDialog");

    private static final int MSG_UPDATE_COUNT_DOWN_TIP = 1; // 更新倒计时提示
    private static final long COUNT_DOWN_UPDATE_DURATION = 1000; //倒计时更新间隔
    private static final int COUNT_DOWN_START_NUM = 30; // 倒计时开始值

    private Context mContext;

    private FloatingLayerListener mFloatingLayerListener;

    private BottomViewHolder mBottomViewHolder;
    private BearViewHolder mBearViewHolder;

    private FloatingLayerAnimatorManager mFloatingLayerAnimatorManager;

    private int mCountDownTipNum = COUNT_DOWN_START_NUM; // 倒计时提示的数字
    private long startCountDownTime = 0; // 开始倒计时的时间

    private boolean isExitLayer = false;  //是否退出浮层，执行浮层隐藏动画时置true

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_COUNT_DOWN_TIP:
                    delayUpdateCountDownTip();
                    break;
                default:
                    break;
            }
        }
    };

    public LeChildFloatingLayerDialog(@NonNull Context context) {
        this(context, 0);
    }

    public LeChildFloatingLayerDialog(@NonNull Context context, int theme) {
        super(context, R.style.lechild_bottom_screen_dialog);
        init(context);
    }

    public void setListener(FloatingLayerListener listener) {
        mFloatingLayerListener = listener;
    }

    private void init(Context context) {
        mContext = context;
        setContentView(R.layout.dialog_floating_layer_layout);
        mLogger.i("init");

        mFloatingLayerAnimatorManager = new FloatingLayerAnimatorManager(context);

        initView();
    }

    private void initView() {

        View rootView = findViewById(R.id.bottom_floating_layer_layout);

        mBottomViewHolder = new BottomViewHolder(rootView);
        mBottomViewHolder.initEvent();

        mBearViewHolder = new BearViewHolder(rootView);
    }

    public void setData() {
        mBottomViewHolder.setData();
    }

    /**
     * 直接隐藏dialog，不进行回调
     */
    public void doDismissDialog() {
        mFloatingLayerListener = null;
        mHandler.removeMessages(MSG_UPDATE_COUNT_DOWN_TIP);
        if (isShowing()) {
            dismiss();
        }
    }

    /**
     * 开始显示
     */
    public void doShowDialog() {
        mLogger.i("doShowDialog  :" + isShowing());

        if (isShowing()) {
            return;
        }

        show();

        if (mFloatingLayerListener != null) {
            mFloatingLayerListener.onShowWelcomeAnimation();
        }

        dealShowWelcomeAnimator();
    }

    /**
     * 显示欢迎动画
     */
    private void dealShowWelcomeAnimator() {

        mBottomViewHolder.doHide();
//        mBearViewHolder.doShow();

        View view = mBearViewHolder.boyLayout;
        mLogger.i("");

        mFloatingLayerAnimatorManager.doComeFront(mBearViewHolder, new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mLogger.i("dealShowWelcomeAnimator onAnimationEnd");
                doCenterBoyBlinkAndShake();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mLogger.i("dealShowWelcomeAnimator onAnimationCancel");
                doCenterBoyBlinkAndShake();
            }

        });
    }

    private void doCenterBoyBlinkAndShake() {
        mFloatingLayerAnimatorManager.doCenterBoyBlinkAndShake(mBearViewHolder, new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                doBearFromCenterToBottom();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                doBearFromCenterToBottom();
            }
        });

    }

    private void doBearFromCenterToBottom() {
//        mBearViewHolder.doShow();
        mFloatingLayerAnimatorManager.doCenterMoveBottom(mBearViewHolder,mBottomViewHolder, new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mLogger.i("doBearFromCenterToBottom onAnimationEnd");
                doShowBottomActivity();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mLogger.i("doBearFromCenterToBottom onAnimationCancel");
                doShowBottomActivity();
            }

        });
    }

    /**
     * 显示底部活动
     */
    private void doShowBottomActivity() {

        if (mFloatingLayerListener != null) {
            mFloatingLayerListener.onShowBottomActivity();
        }

        mBearViewHolder.doHide();
        mBottomViewHolder.doShow();

        startCountDownTime = SystemClock.uptimeMillis();
        delayUpdateCountDownTip();

        mFloatingLayerAnimatorManager.doBottomBearJump(mBottomViewHolder);
    }

    /**
     * 下浮层显示后，小熊眨眼睛
     */
    private void doBottomBearBlind() {
        mFloatingLayerAnimatorManager.doBottomBearBlind(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBottomViewHolder.doCloseEye();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBottomViewHolder.doOpenEye();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mBottomViewHolder.doOpenEye();
            }
        });
    }


    /**
     * 取消dialog的显示，需要执行移动消失动画
     * <p>
     * 倒计时结束，返回案件，点击稍后开通
     */
    private void doMoveDialogTop() {
        mLogger.i("doMoveDialogTop");
        isExitLayer = true;
        dealMoveTopByAnimator();
    }

    /**
     * 执行移动到顶部的动画
     */
    private void dealMoveTopByAnimator() {

        View bottomView = mBottomViewHolder.wholeLayout;
        // 顶导活动View的位置
        int[] toPos = null;
        if (mFloatingLayerListener != null) {
            toPos = mFloatingLayerListener.getTopActivityCenterPos();
        }

        // 下浮层整体View的位置
        int[] fromPos = ViewUtils.getViewCenterPointPos(bottomView);

        // 顶导位置为null时，原地消失
        if (toPos == null) {
            toPos = new int[2];
            toPos[0] = fromPos[0] + 400;
            toPos[1] = fromPos[1] - 900;
        }

        int moveX = toPos[0] - fromPos[0];
        int moveY = toPos[1] - fromPos[1];

        mFloatingLayerAnimatorManager.doBottomMoveTop(bottomView, moveX, moveY, new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mLogger.i("dealMoveTopByAnimator onAnimationEnd");
                if (mFloatingLayerListener != null) {
                    mFloatingLayerListener.onCancel();
                }
                doDismissDialog();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mLogger.i("dealMoveTopByAnimator onAnimationCancel");

                if (mFloatingLayerListener != null) {
                    mFloatingLayerListener.onCancel();
                }
                doDismissDialog();
            }
        });
    }

    private void onConfirmBtnClick() {
        mLogger.i("onConfirmBtnClick");

        // 点击前往开通，直接隐藏dialog
        if (mFloatingLayerListener != null) {
            mFloatingLayerListener.onClickConfirm();
        }
        doDismissDialog();
    }

    private void onCancelBtnClick() {
        mLogger.i("onCancelBtnClick");
        doMoveDialogTop();
    }

    /**
     * 延时更新倒计时提示
     */
    private void delayUpdateCountDownTip() {
        // 更新倒计时提示
        mBottomViewHolder.updateCountDownTip(mCountDownTipNum);

        // 倒计时结束，且退出动画没有执行
        if (mCountDownTipNum == 0 && !isExitLayer) {
            doMoveDialogTop();
            return;
        }

        if ((mCountDownTipNum % 3) == 2) {
            doBottomBearBlind();
        }

        // 1秒后更新倒计时提示
        mCountDownTipNum--;
        long num = COUNT_DOWN_START_NUM - mCountDownTipNum;
        long atTime = startCountDownTime + num * COUNT_DOWN_UPDATE_DURATION;
        mHandler.sendEmptyMessageAtTime(MSG_UPDATE_COUNT_DOWN_TIP, atTime);
    }

    /**
     * 监听按键，截取back按键
     */
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mLogger.i("onKeyDown  KEYCODE_BACK");
            // 当退出动画为执行时，执行退出动画
            if (!isExitLayer) {
                // 按返回按键，触发dialog的消失动画
                doMoveDialogTop();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public interface FloatingLayerListener {

        /**
         * 显示欢迎动画
         */
        void onShowWelcomeAnimation();

        /**
         * 显示底部活动
         */
        void onShowBottomActivity();

        /**
         * 点击确认
         */
        void onClickConfirm();

        /**
         * 取消
         */
        void onCancel();

        /**
         * 获取顶导活动位置，用于底部活动移动到顶导活动
         */
        int[] getTopActivityCenterPos();
    }


    /**
     * 底部控件——活动布局，两只小熊，手指
     */
    public class BottomViewHolder implements View.OnClickListener, View.OnFocusChangeListener {

        public View wholeLayout; // 整体布局
        public ImageView bearsView; // 两只小熊
        public View handView; // 手指
        public View activityLayout; // 活动布局


        public TextView mainTitleTV; // 主标题
        public TextView subTitleTV; // 副标题
        public Button confirmBtn; // 确认按键
        public Button cancelBtn; // 取消按键

        public BottomViewHolder(View rootView) {
            wholeLayout = rootView.findViewById(R.id.bottom_whole_layout);
            bearsView = (ImageView) rootView.findViewById(R.id.bottom_bear);
            handView = rootView.findViewById(R.id.bottom_hand);
            activityLayout = rootView.findViewById(R.id.bottom_activity_layout);

            mainTitleTV = (TextView) findViewById(R.id.bottom_title_main);
            subTitleTV = (TextView) findViewById(R.id.bottom_title_sub);

            confirmBtn = (Button) findViewById(R.id.bottom_confirm_btn);
            cancelBtn = (Button) findViewById(R.id.bottom_cancel_btn);
        }

        /**
         * 初始化事件处理
         */
        public void initEvent() {
            confirmBtn.setOnFocusChangeListener(this);
            cancelBtn.setOnFocusChangeListener(this);

            confirmBtn.setOnClickListener(this);
            cancelBtn.setOnClickListener(this);
        }

        public void updateCountDownTip(int countNum) {
            cancelBtn.setText(mContext.getResources().getString(R.string.lechild_bottom_open_later, countNum));
        }

        public void doShow() {
            bearsView.setVisibility(View.VISIBLE);
            handView.setVisibility(View.VISIBLE);
            activityLayout.setVisibility(View.VISIBLE);

            confirmBtn.requestFocus();
        }

        public void doHide() {
            bearsView.setVisibility(View.INVISIBLE);
            handView.setVisibility(View.INVISIBLE);
            activityLayout.setVisibility(View.INVISIBLE);
        }

        /**
         * 初始化数据
         */
        public void setData() {

            // 主标题
            String title = "儿童节大放价";
            if (TextUtils.isEmpty(title)) {
                mainTitleTV.setText("");
            } else {
                mainTitleTV.setText(title);
            }

            // 副标题  被@包裹的文案特殊处理，eg：开通会员立省@399@元。其中的399要特殊处理，并且@字符不显示
            String subTitle = "开通会员立省@399@元";
            if (TextUtils.isEmpty(subTitle)) {
                subTitleTV.setText("");
            } else {
                int startPos = subTitle.indexOf("@");
                subTitle = subTitle.replaceFirst("@", "");

                int endPos = subTitle.indexOf("@");
                subTitle = subTitle.replaceFirst("@", "");

                Spannable span = new SpannableString(subTitle);
                span.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new AbsoluteSizeSpan(45), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(new StyleSpan(Typeface.BOLD), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                subTitleTV.setText(span);
            }
        }


        public void doOpenEye() {
            bearsView.setImageResource(R.drawable.lechild_activity_bottom_bear);
        }

        public void doCloseEye() {
            bearsView.setImageResource(R.drawable.lechild_activity_bottom_bear_blink);
        }


        /**
         * 浮层中按钮焦点处理
         */
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                switch (v.getId()) {
                    case R.id.bottom_confirm_btn:
                        mLogger.i("onFocusChange  confirm");
                        mFloatingLayerAnimatorManager.doHandMoveLeft(handView);
                        break;

                    case R.id.bottom_cancel_btn:
                        mLogger.i("onFocusChange  cancel");
                        mFloatingLayerAnimatorManager.doHandMoveRight(handView);
                        break;

                    default:
                        break;
                }
            }
        }

        /**
         * 浮层中按钮点击处理
         */
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.bottom_confirm_btn:
                    mLogger.i("onClick confirm");
                    onConfirmBtnClick();
                    break;

                case R.id.bottom_cancel_btn:
                    mLogger.i("onClick cancel");
                    onCancelBtnClick();
                    break;

                default:
                    break;
            }
        }
    }


    public class BearViewHolder {

        public View wholeLayout; // 整体布局

        public View boyLayout; // 男熊布局
        public View girlLayout; // 女熊布局

        public ImageView boyHead; // 男熊-头
        public View boyBody; // 男熊-身体

        public View girlHead; // 女熊-头
        public View girlBody; // 女熊-身体

        public BearViewHolder(View rootView) {
            wholeLayout = rootView.findViewById(R.id.bottom_bear_whole_layout);
            boyLayout = rootView.findViewById(R.id.bottom_bear_boy_layout);
            girlLayout = rootView.findViewById(R.id.bottom_bear_girl_layout);

            boyHead = (ImageView) rootView.findViewById(R.id.bottom_bear_boy_head);
            boyBody = rootView.findViewById(R.id.bottom_bear_boy_body);
            girlHead = rootView.findViewById(R.id.bottom_bear_girl_head);
            girlBody = rootView.findViewById(R.id.bottom_bear_girl_body);

        }

        public void doShow() {
            wholeLayout.setVisibility(View.VISIBLE);
        }

        public void doHide() {
            wholeLayout.setVisibility(View.INVISIBLE);
        }

        public void doOpenEye() {
            boyHead.setImageResource(R.drawable.lechild_activity_bottom_bearhead_boy);
        }

        public void doCloseEye() {
            boyHead.setImageResource(R.drawable.lechild_activity_bottom_bearhead_boy_blink);
        }

    }

}
