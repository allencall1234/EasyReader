package com.easyreader.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.easyreader.base.BaseApplication;


/**
 * Created by zhengfeilong on 16/5/12.
 */
public class ToastUtils {

    public static Toast mToast;

    public static void showShortToast(String str) {
        showToast(BaseApplication.getInstance(), str, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(int strId) {
        String str = BaseApplication.getInstance().getResources().getString(strId);
        showShortToast(str);
    }

    public static void showLongToast(String str) {
        showToast(BaseApplication.getInstance(), str, Toast.LENGTH_LONG);
    }

    public static void showLongToast(int strId) {
        String str = BaseApplication.getInstance().getResources().getString(strId);
        showLongToast(str);
    }


    private static void showToast(final Context context, final String str, final int duration) {
        if (context != null && !TextUtils.isEmpty(str)) {
            //用Handler解决在线程中调用的问题
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (context != null) {
                        if (mToast == null) {
                            mToast = Toast.makeText(context, str, duration);
                        } else {
                            mToast.setText(str);
                            mToast.setDuration(duration);
                        }
//            toast.setGravity(Gravity.CENTER, 0, 0);
                        mToast.show();
                    }
                }
            });
        }
    }


}
