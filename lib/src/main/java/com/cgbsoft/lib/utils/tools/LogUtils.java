package com.cgbsoft.lib.utils.tools;

/**
 * desc  打印日志工具类
 * author wangyongkui  wangyongkui@simuyun.com
 * 日期 17/4/12-16:46
 */
public class LogUtils {
    private static boolean IsShowTage = true;
    public static final String TestTage = "wang";

    public static void Log(String Tag, String Inf) {
        if (IsShowTage)
            android.util.Log.i(Tag, Inf);
    }
}
