package com.easyreader.utils;

import android.text.TextUtils;
import android.util.Log;

import com.easyreader.base.BaseApplication;

/**
 * Log工具，类似android.util.Log。
 * tag自动产生，格式: customTagPrefix:className.methodName(L:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(L:lineNumber)。
 * Date: 13-7-24
 * Time: 下午12:23
 */
public class LogUtil {

	public static String customTagPrefix = "x_log";
	public static boolean allowLog = true;

	private LogUtil() {
	}

	private static String generateTag() {
		StackTraceElement caller = new Throwable().getStackTrace()[2];
		String tag = "%s.%s(L:%d)";
		String callerClazzName = caller.getClassName();
		callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
		tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
		tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
		return tag;
	}

	public static void d(String content) {
		if (!allowLog) return;
		String tag = generateTag();

		logNoLimit(Log.DEBUG, tag, content);
	}

	public static void d(String content, Throwable tr) {
		if (!allowLog) return;
		String tag = generateTag();

		Log.d(tag, content, tr);
	}

	public static void e(String content) {
		if (!allowLog) return;
		String tag = generateTag();

		logNoLimit(Log.ERROR, tag, content);
	}

	public static void e(String content, Throwable tr) {
		if (!allowLog) return;
		String tag = generateTag();

		Log.e(tag, content, tr);
	}

	public static void i(String content) {
		if (!allowLog) return;
		String tag = generateTag();

		logNoLimit(Log.INFO, tag, content);
	}

	public static void i(String content, Throwable tr) {
		if (!allowLog) return;
		String tag = generateTag();

		Log.i(tag, content, tr);
	}

	public static void v(String content) {
		if (!allowLog) return;
		String tag = generateTag();

		logNoLimit(Log.VERBOSE, tag, content);
	}

	public static void v(String content, Throwable tr) {
		if (!allowLog) return;
		String tag = generateTag();

		Log.v(tag, content, tr);
	}

	public static void w(String content) {
		if (!allowLog) return;
		String tag = generateTag();

		logNoLimit(Log.WARN, tag, content);
	}

	public static void w(String content, Throwable tr) {
		if (!allowLog) return;
		String tag = generateTag();

		Log.w(tag, content, tr);
	}

	public static void w(Throwable tr) {
		if (!allowLog) return;
		String tag = generateTag();

		Log.w(tag, tr);
	}

	public static void i(String tag, String content) {
		if (!allowLog) return;

		logNoLimit(Log.INFO, tag, content);
	}
	public static void w(String tag, String content) {
		if (!allowLog) return;

		logNoLimit(Log.WARN, tag, content);
	}
	public static void e(String tag, String content) {
		if (!allowLog) return;

		logNoLimit(Log.ERROR, tag, content);
	}

	/**
	 * 线上包也会打印这个日志
	 */
	public static void onlineLog(String content) {
		try{
			logNoLimit(Log.INFO, BaseApplication.getInstance().getPackageName(), content);
		}catch (Exception e){}
	}

	/**
	 * 使用Log来显示调试信息,因为log在实现上每个message有4k字符长度限制
	  所以这里使用自己分节的方式来输出足够长度的message
	 * @param level
	 * @param tag
	 * @param content
	 */
	public static void logNoLimit(int level, String tag, String content) {
		if (TextUtils.isEmpty(content)){
			return;
		}
		int lineContentCapacity = 3000;
		if(content.length() > lineContentCapacity) {
			for(int i=0;i<content.length();i+=lineContentCapacity){
				if(i+lineContentCapacity < content.length()){
					log(level, tag, content.substring(i, i+lineContentCapacity));
				}else {
					log(level, tag, content.substring(i, content.length()));
				}
			}
		} else{
			log(level, tag, content);
		}
	}

	private static void log(int level, String tag, String content) {
		switch (level){
			case Log.INFO:
				Log.i(tag, content);
				break;
			case Log.DEBUG:
				Log.d(tag, content);
				break;
			case Log.WARN:
				Log.w(tag, content);
				break;
			case Log.ERROR:
				Log.e(tag, content);
				break;
			case Log.VERBOSE:
				Log.v(tag, content);
				break;
		}
	}


}