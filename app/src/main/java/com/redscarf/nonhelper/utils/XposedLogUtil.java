package com.redscarf.nonhelper.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

/**
 * <p>function: Xposed日志工具类
 * <p>User: LeeJohn
 * <p>Date: 2017/9/8
 * <p>Version: 1.0
 */
public class XposedLogUtil {
    public static SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

    public static void printMethodInfo(final Class clazz ,XC_MethodHook.MethodHookParam param){
        String methodName = param.method.getName();
        StringBuilder argsString = new StringBuilder();
        if( param.args != null && param.args.length > 0){
            for (int i = 0; i < param.args.length; i++) {
                argsString.append( i + " : " + param.args[i] + " , ");
            }
        }
        log("testHookWeixin 【"+ clazz.getName() + "\\" + methodName + "】  result : { " +  param.getResult() + " } params : { " +  argsString + " }" );
    }

    public static void log(Object str) {
        XposedBridge.log("[" + df.format(new Date()) + "]:  " + str.toString());
    }
}
