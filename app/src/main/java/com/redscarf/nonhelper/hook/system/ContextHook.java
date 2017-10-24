package com.redscarf.nonhelper.hook.system;

import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * <p>function: Android 系统上下文
 * <p>User: LeeJohn
 * <p>Date: 2017/09/08
 * <p>Version: 1.0
 */
public class ContextHook {
    public static final String APPLICATION_CONTEXT_PACKAGE = "android";
    public static final String APPLICATION_CONTEXT_CLASS = "android.content.ContextWrapper";

    public static Context applicationContext;

    public void handleLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {
        try {
            if ( !loadPackageParam.packageName.equals(APPLICATION_CONTEXT_PACKAGE)) {
                return;
            }
            final Class ContextClass = loadPackageParam.classLoader.loadClass(APPLICATION_CONTEXT_CLASS);
            XposedHelpers.findAndHookMethod(ContextClass, "getApplicationContext", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    initThread(loadPackageParam,param);
                }
            });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    /**
     * 初始化
     */
    private void initThread(final LoadPackageParam loadPackageParam, XC_MethodHook.MethodHookParam param){
        if( applicationContext == null ){
            applicationContext = (Context) param.getResult();
//            AppRunningMonitorThread appRunningMonitorThread = new AppRunningMonitorThread();//不同进程（同一个应用进程也可能不同），虚拟机不同
//            appRunningMonitorThread.start();
            XposedBridge.log("applicationContext : " + applicationContext.hashCode() + " packageName : " + loadPackageParam.packageName );
        }
    }

}
