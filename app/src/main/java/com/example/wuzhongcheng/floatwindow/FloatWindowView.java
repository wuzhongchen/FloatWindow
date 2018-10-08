package com.example.wuzhongcheng.floatwindow;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

public class FloatWindowView extends LinearLayout {
    
    public static int viewWidth;
    public static int viewHeight;
    private static int statusBarHeight;
    private static FloatWindowView floatWindowView;
    private WindowManager.LayoutParams mParams;
    private float mFingerInScreenX;
    private float mFingerInScreenY;
    private float mFingerDownInScreenX;
    private float mFingerDownInScreenY;
    private float mFloatWindowInViewX;
    private float mFloatWindowInViewY;
    private boolean isStretch = false;
    WindowManager windowManager;
    ImageView mNextImg;
    ImageView mPlayImg;
    ImageView mBackImg;
    ImageView mIconImg;
    ImageView mCancelImg;
    View mParentView;
    View mBackGround;
    LayoutParams mRightParams;
    Context mContext;
    boolean isPlay = false;

    public static FloatWindowView getInstance(Context mContext) {
        if (floatWindowView == null) {
            floatWindowView = new FloatWindowView(mContext);
        }
        return floatWindowView;
    }

    private FloatWindowView(final Context context) {
        super(context);
        mContext = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        LayoutInflater.from(context).inflate(R.layout.float_windows,this);
        mParentView = findViewById(R.id.parent_layout);
        viewWidth = mParentView.getLayoutParams().width;
        viewHeight = mParentView.getLayoutParams().height;

        mIconImg = (ImageView) findViewById(R.id.image_display_imageview);
        mNextImg = (ImageView) findViewById(R.id.tv_note);
        mBackImg = (ImageView) findViewById(R.id.tv_inbox);
        mPlayImg = (ImageView) findViewById(R.id.tv_addSchedule);
        mCancelImg = (ImageView) findViewById(R.id.tv_cancel);
        mBackGround = findViewById(R.id.blank);
        mIconImg.setImageResource(R.drawable.window_icon);
        mRightParams = (LinearLayout.LayoutParams) mParentView.getLayoutParams();
    }

    public void setImgToPause() {
        mPlayImg.setImageResource(R.drawable.stopmusic_btn);
        isPlay = false;
    }

    public void setImgToPlay() {
        mPlayImg.setImageResource(R.drawable.window_pause);
        isPlay = true;
    }

    private void removeClickListener() {
        mPlayImg.setClickable(false);
        mCancelImg.setClickable(false);
    }

    private void initClickListener() {
        mPlayImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlay){
                    setImgToPause();
                } else {
                    setImgToPlay();
                }
            }
        });
        mCancelImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                MyWindowManager.updateWindowStatus(mContext,true);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指按下时记录必要的数据  纵坐标的值都需要减去状态栏的高度
                mFloatWindowInViewX = event.getX();
                mFloatWindowInViewY = event.getY();
                mFingerDownInScreenX = event.getRawX();
                mFingerDownInScreenY = event.getRawY() - getStatusBarHeight();
                mFingerInScreenX = event.getRawX();
                mFingerInScreenY = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                mFingerInScreenX = event.getRawX();
                mFingerInScreenY = event.getRawY() - getStatusBarHeight();
                //手指移动时更新悬浮窗的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                //如果手指离开屏幕  mFingerDownInScreenX和mFingerInScreenX相同 且 mFingerDownInScreenY和mFingerInScreenY相等,则视为触发了单机事件
                if(mFingerDownInScreenX == mFingerInScreenX && mFingerDownInScreenY == mFingerInScreenY) {
                    if(!isStretch ) {
                        setRightParams();
                        drawOutWindowToRight();
                        isStretch = true;
                    }
                    else {
                        removeClickListener();
                        pushWindowFromRightToBack();
                        isStretch = false;
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    public float dip2px(Context context, float dpValue) {
        if (context != null && context.getResources() != null && context.getResources().getDisplayMetrics() != null) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return  dpValue * scale + 0.5f;
        } else {
            return dpValue * 3; //default value
        }
    }

    public void setPosition(View view, int top, int left, int width, int height) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
        lp.topMargin = top;
        lp.leftMargin = left;
        lp.width = width;
        lp.height = height;
        view.setLayoutParams(lp);
    }

    private void setRightParams() {
        mRightParams.width = (int) dip2px(mContext,200);
        mParentView.setLayoutParams(mRightParams);

    }

    private void removeRightParams() {
        mRightParams.width = (int) dip2px(mContext,40);
        mParentView.setLayoutParams(mRightParams);
    }

    public void reset() {
        isStretch = false;
        setPosition(mBackGround, mBackGround.getTop(), mBackGround.getLeft()/* + offsetX*/, 60, mBackGround.getMeasuredHeight());
        removeRightParams();
    }

    private void pushWindowFromRightToBack() {
        ValueAnimator valAnim = ValueAnimator.ofInt(0, 480);
        valAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int width = (int) animation.getAnimatedValue();
                setPosition(mBackGround, mBackGround.getTop(), mBackGround.getLeft()/* + offsetX*/, 540 - width, mBackGround.getMeasuredHeight());
            }
        });
        valAnim.setDuration(600);
        valAnim.start();

        valAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                removeRightParams();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void drawOutWindowToRight() {
        mBackGround.setPivotX(0);
        ValueAnimator valAnim = ValueAnimator.ofInt(0, 600);
        valAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int width = (int) animation.getAnimatedValue();
                setPosition(mBackGround, mBackGround.getTop(), mBackGround.getLeft()/* + offsetX*/, width, mBackGround.getMeasuredHeight());
            }
        });
        valAnim.setDuration(600);
        valAnim.start();

        valAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                initClickListener();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    private void updateViewPosition() {
        //在移动的过程中  需要减去触摸点相对于控件的位置   否则就会出现移动时 窗口跟着左上角走, 而不是正中间
        mParams.x = (int) (mFingerInScreenX - mFloatWindowInViewX);
        mParams.y = (int) (mFingerInScreenY - mFloatWindowInViewY);
        windowManager.updateViewLayout(this, mParams);
    }

    //获取状态栏的高度
    private int getStatusBarHeight() {
        if(statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = field.getInt(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (ClassNotFoundException | NoSuchFieldException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}
