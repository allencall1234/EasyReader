package com.easyreader.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Paint;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.Uri;
import android.os.IBinder;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import com.easyreader.base.BaseApplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class CommonUtils {

    /**
     * 获取版本号
     *
     * @return
     */
    public static String getVersionName() {
        String version = "";
        try {
            Context context = BaseApplication.getInstance().getApplicationContext();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String getVersionCode() {
        String code = "-1";
        try {
            Context context = BaseApplication.getInstance().getApplicationContext();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            code = String.valueOf(packInfo.versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return code;
    }

    /**
     * 获取Android Manifest配置信息，比如极光key，友盟渠道，地图key等等
     *
     * @param context
     * @param key
     * @return
     */
    public static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return info.metaData.getString(key);
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    public static boolean isNullOrEmpty(Map map) {
        if (map == null || map.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 判断字符串是否为null或�?空字符串
     *
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        boolean result = false;
        if (null == str || "".equals(str.trim())) {
            result = true;
        }
        return result;
    }

    /**
     * str != null && !("".equals(str))
     *
     * @param str
     * @return
     */
    public static boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    /**
     * 判断数组是否是null或size()==0
     *
     * @param list
     * @return
     */
    public static boolean isNullOrEmpty(List<?> list) {
        boolean result = false;
        if (null == list || list.size() == 0) {
            result = true;
        }
        return result;
    }

    /**
     * list != null && lists.size() != 0;
     *
     * @param list
     * @return
     */
    public static boolean isNotNullOrEmpty(List<?> list) {
        return !isNullOrEmpty(list);
    }


    /**
     * 打开系统软键盘
     */
    public static void openSoftKeyBoard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 关闭系统软键盘
     */
    public static void closeSoftKeyBoard(Activity activity) {
        if (activity == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void closeSoftKeyBoard(Window window, Context context) {
        if (window == null || context == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && window.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(window.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void closeSoftKeyBoard(IBinder windowToken, Context context) {
        if (windowToken == null || context == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean hasOpenGps(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    /**
     * 设置textview中划线
     *
     * @param textView
     */
    public static void setTextViewLineThrough(TextView textView) {
        textView.getPaint().setAntiAlias(true);//抗锯齿
        textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
//		textView.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线
    }

    /**
     * 开启activity不带参数
     */
    public static void startActivity(Context packageContext, Class<?> cls) {
        Intent intent = new Intent(packageContext, cls);
        packageContext.startActivity(intent);
    }

    /**
     * 调用系统拨打电话
     */
    public static void callPhone(Context mcontext, String phoneStr) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:" + phoneStr);
            intent.setData(data);
            mcontext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param tv
     * @param needUnderLine
     * @param str1
     * @param str2
     * @param str1ColorId     resource id
     * @param str2ColorId
     * @param onClickListener
     */
    public static void setTextWithSpan(final TextView tv, final boolean needUnderLine, String str1, final String str2, int str1ColorId, final int str2ColorId, final View.OnClickListener onClickListener) {
        String str3 = str1 + str2;
        SpannableString msp = new SpannableString(str3);
        msp.setSpan(new ForegroundColorSpan(BaseApplication.getInstance().getResources().getColor(str1ColorId)), 0, str1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(BaseApplication.getInstance().getResources().getColor(str2ColorId));       //设置文字颜色
                ds.setUnderlineText(needUnderLine);      //设置下划线
            }

            @Override
            public void onClick(View widget) {
                if (onClickListener != null) {
                    onClickListener.onClick(widget);
                }
            }
        }, str1.length(), str3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件
        tv.setText(msp);
    }

    public static void setTextWithSpan(final TextView tv, String str11, final String str12, final String str21, final String str22, int normalColorId, final int clickColorId, final View.OnClickListener listener1, final View.OnClickListener listener2) {
        String str = str11 + str12 + str21 + str22;
        int len = 0;
        SpannableString msp = new SpannableString(str);
        msp.setSpan(new ForegroundColorSpan(BaseApplication.getInstance().getResources().getColor(normalColorId)), 0, str11.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        len += str11.length();

        msp.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(BaseApplication.getInstance().getResources().getColor(clickColorId));       //设置文字颜色
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View widget) {
                if (listener1 != null) {
                    listener1.onClick(widget);
                }
            }
        }, len, len + str12.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        len += str12.length();
        msp.setSpan(new ForegroundColorSpan(BaseApplication.getInstance().getResources().getColor(normalColorId)), len, len + str21.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        len += str21.length();
        msp.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(BaseApplication.getInstance().getResources().getColor(clickColorId));       //设置文字颜色
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View widget) {
                if (listener2 != null) {
                    listener2.onClick(widget);
                }
            }
        }, len, len + str22.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件
        tv.setText(msp);
    }


    /**
     * 如：已阅读并同意《即有钱包激活协议》及代扣还款并出具本《代扣服务授权书》
     * <p>
     * 适用于两个可点链接，str4不传可实现，如：的事项，将按要求上传《相关凭证以证明》未用于非法用途
     *
     * @param tv
     * @param str1
     * @param str2
     * @param str3
     * @param str4
     * @param str1ColorId
     * @param str2ColorId
     * @param onClickListener1
     * @param onClickListener2
     */
    public static void setTextWithSpan(TextView tv, final boolean needUnderLine, String str1, String str2, String str3, String str4, int str1ColorId, final int str2ColorId, final View.OnClickListener onClickListener1, final View.OnClickListener onClickListener2) {
        String str5 = str1 + str2 + str3 + str4;
        SpannableString msp = new SpannableString(str5);
        msp.setSpan(new ForegroundColorSpan(BaseApplication.getInstance().getResources().getColor(str1ColorId)), 0, str1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(BaseApplication.getInstance().getResources().getColor(str2ColorId));
                ds.setUnderlineText(needUnderLine);      //设置下划线
            }

            @Override
            public void onClick(View widget) {
                if (onClickListener1 != null) {
                    onClickListener1.onClick(widget);
                }
            }
        }, str1.length(), (str1 + str2).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        msp.setSpan(new ForegroundColorSpan(BaseApplication.getInstance().getResources().getColor(str1ColorId)), (str1 + str2).length(), (str1 + str2 + str3).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(BaseApplication.getInstance().getResources().getColor(str2ColorId));
                ds.setUnderlineText(needUnderLine);      //设置下划线
            }

            @Override
            public void onClick(View widget) {
                if (onClickListener2 != null) {
                    onClickListener2.onClick(widget);
                }
            }
        }, (str1 + str2 + str3).length(), str5.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件
        tv.setText(msp);
    }

    /**
     * 判断拍照是否可用
     *
     * @return
     */
    public static boolean isCameraUseable() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
// setParameters 是针对魅族MX5。MX5通过Camera.openCamera()拿到的Camera对象不为null
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        } finally {
            if (mCamera != null) {
                mCamera.release();
            }
        }
        return canUse;
    }

}
