package com.easyreader.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


/**
 */
public class BaseApplication extends Application {
    private static BaseApplication mInstance;
    public List<Activity> undestroyActivities;
    private Timer tokenTimer;
    private String beforePayActivity;
    private boolean isInMainProcess;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mInstance = this;
            undestroyActivities = new ArrayList<Activity>();

            isInMainProcess = true;
            int pid = android.os.Process.myPid();
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
            if (runningApps != null && !runningApps.isEmpty()) {
                for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                    if (procInfo.pid == pid) {
                        if (procInfo.processName.equals("com.giveu.shoppingmall:pushcore")) {
                            isInMainProcess = false;
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isInMainProcess() {
        return isInMainProcess;
    }


    public String getBeforePayActivity() {
        return beforePayActivity;
    }

    public void setBeforePayActivity(String beforePayActivity) {
        this.beforePayActivity = beforePayActivity;
    }


    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }


    public static BaseApplication getInstance() {
        return mInstance;
    }



    public void addActivity(Activity activity) {
        undestroyActivities.add(activity);
    }

    public void removeActivity(Activity activity) {
        undestroyActivities.remove(activity);
    }


    /**
     * 判断Activity任务栈中是否包含指定的Activity
     *
     * @param clzz 目标Activity
     * @return -1，当app被系统回收后任务栈只包含前台Acitivty,状态不明
     * <p>0，任务栈中不包含指定Activity
     * <p>1，任务栈中包含指定Activity
     */
    public int containsActivity(Class clzz) {
        if (undestroyActivities.size() <= 1) {
            return -1;
        }

        for (Activity activity : undestroyActivities) {
            if (activity.getClass() == clzz) {
                return 1;
            }
        }

        return 0;
    }


    /**
     * 退出登录后需finsh所有activity
     */
    public void finishAllActivity() {
        for (Activity activity : undestroyActivities) {
            if (activity != null) {
                activity.finish();
            }
        }
    }


}
