package com.custom.ui.textview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * with fade anim text view
 *
 * @author lijia
 * @date 18-3-26
 */

public class FadeTextView extends android.support.v7.widget.AppCompatTextView {

    private static final boolean DEBUG = true;
    private static final String TAG = FadeTextView.class.getSimpleName();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    /****
     * 动画执行时间间隔
     */
    private static final int DEFAULT_DURATION = 10;
    /**
     * 执行逐个显示
     **/
    private static final int MSG_HANDLER_FADE = 0;

    /**
     * 执行放大动画
     **/
    private static final int MSG_HANDLER_ANIM = 1;

    private String fadeText;
    private int length;

    private FadeHandler mHandler;

    private boolean isFadeRuning = false;

    private FadeTextViewAnimListener mFadeTextViewAnimListener;

    public FadeTextView(Context context) {
        this(context, null);
    }

    public FadeTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FadeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * init
     */
    private void init() {
        mHandler = new FadeHandler(this);
    }

    private void startAnim() {
        //组合动画
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1, 1.25f,1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1, 1.25f,1);

        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        //两个动画同时开始
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                resetFadeRuning();
                if (mFadeTextViewAnimListener != null) {
                    mFadeTextViewAnimListener.onAnimFinish();
                }
            }
        });
    }


    /**
     * 设置要显示的文字并开始逐个显示
     * @param text
     * @param fadeTextViewAnimListener 动画执行的回调方法，可以为null
     */
    public void setTextFade(String text,FadeTextViewAnimListener fadeTextViewAnimListener) {
        if (TextUtils.isEmpty(text)) {
            if (DEBUG) {
                Log.i(TAG, "FadeTextView text is not allowed empty ");
            }
            return;
        }
        if (isFadeRuning) {
            if (DEBUG) {
                Log.i(TAG, "FadeTextView fade is running ");
            }
            return;
        }
        this.mFadeTextViewAnimListener = fadeTextViewAnimListener;
        this.fadeText = text;
        this.length = fadeText.length();

        startFade(0);
        isFadeRuning = true;
    }

    private void resetFadeRuning() {
        isFadeRuning = false;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * 开始逐个显示
     *
     * @param index
     */
    private void startFade(final int index) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                //截取要填充的字符串
                final String subStr = fadeText.substring(0, index);

                post(new Runnable() {
                    @Override
                    public void run() {
                        setText(subStr);
                    }
                });
                //每次多截取一个
                int nextIndex = index;
                nextIndex++;
                //如果还有文字，那么继续开启线程，相当于递归的感觉
                if (nextIndex <= length) {
                    Message msg = Message.obtain();
                    msg.what = MSG_HANDLER_FADE;
                    msg.arg1 = nextIndex;
                    msg.obj = subStr;
                    mHandler.sendMessageDelayed(msg, DEFAULT_DURATION);
                } else {
                    if (DEBUG) {
                        Log.i(TAG, "run index: " + nextIndex + "===length:" + length);
                    }
                    Message msg = Message.obtain();
                    msg.what = MSG_HANDLER_ANIM;
                    mHandler.sendMessageDelayed(msg, DEFAULT_DURATION);
                }

            }
        });
    }

    private static class FadeHandler extends Handler {

        WeakReference<FadeTextView> weakReference = null;

        public FadeHandler(FadeTextView fadeTextView) {
            this.weakReference = new WeakReference<FadeTextView>(fadeTextView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FadeTextView fadeTextView = weakReference.get();
            switch (msg.what) {
                case MSG_HANDLER_FADE:
                    int nIndex = msg.arg1;
                    if (fadeTextView != null) {
                        fadeTextView.startFade(nIndex);
                    }
                    break;
                case MSG_HANDLER_ANIM:
                    if (fadeTextView != null) {
                        fadeTextView.startAnim();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 动画执行的监听
     */
    interface FadeTextViewAnimListener {
        /**
         * 动画执行完毕
         */
        void onAnimFinish();
    }

}
