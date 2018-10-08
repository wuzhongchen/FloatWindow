package com.example.wuzhongcheng.floatwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

import static com.example.wuzhongcheng.floatwindow.MainActivity.KEY_IS_TOGGLE_BUTTON;

class MyWindowManager {
    public static FloatWindowView sFloatView;
    public static WindowManager.LayoutParams sLayoutParams;
    public static WindowManager sWindowManager;

    public static void updateWindowStatus(Context context, Boolean isActivitySurface) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();

        if(sFloatView == null && !isActivitySurface && KEY_IS_TOGGLE_BUTTON) {
            sFloatView = FloatWindowView.getInstance(context);
            if(sLayoutParams == null) {
                sLayoutParams = new WindowManager.LayoutParams();
                sLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                sLayoutParams.format = PixelFormat.RGBA_8888;
                sLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                sLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
                sLayoutParams.width = FloatWindowView.viewWidth;
                sLayoutParams.height = FloatWindowView.viewHeight;
                sLayoutParams.x = screenWidth ;
                sLayoutParams.y = (screenHeight /3) *2 ;
            }
            sFloatView.setParams(sLayoutParams);
            windowManager.addView(sFloatView, sLayoutParams);
            MediaPlayerService.registerListener(FloatWindowView.getInstance(context));
        } else if(sFloatView != null) {
            windowManager.removeView(sFloatView);
            sFloatView = null;
            FloatWindowView.getInstance(context).reset();
        }

        if (MediaPlayerService.mIsPlaying) {
            FloatWindowView.getInstance(getApplicationContext()).setImgToPlay();
        } else {
            FloatWindowView.getInstance(getApplicationContext()).setImgToPause();
        }
    }

    private static WindowManager getWindowManager(Context context) {
        if(sWindowManager == null) {
            sWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return sWindowManager;
    }
}
