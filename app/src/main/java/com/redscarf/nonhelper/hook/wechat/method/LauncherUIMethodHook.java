package com.redscarf.nonhelper.hook.wechat.method;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;

import com.redscarf.nonhelper.hook.wechat.WechatHook;
import com.redscarf.nonhelper.utils.XposedLogUtil;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

/**
 * <p>function: hook微信主界面
 * <p>User: LeeJohn
 * <p>Date: 2017/09/08
 * <p>Version: 1.0
 */
public class LauncherUIMethodHook {

    /**
     * hook 微信主页
     * loadClass 【com.tencent.mm.ui.LauncherUI】
     * method 【ag】
     */
    public void hookLauncherUi() {
        try {
            XposedBridge.hookAllMethods(WechatHook.ui_LauncherUI_class,"onCreate" , new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedLogUtil.log("LauncherUIMethodHook onCreate");
                }
            });

            XposedBridge.hookAllMethods(WechatHook.ui_LauncherUI_class,"ag" , new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    initThread(param);
                }
            });

        }catch (Exception e){
            XposedLogUtil.log(e);
        }
    }

    /**
     * 初始化打开微信后的线程
     */
    private void initThread(XC_MethodHook.MethodHookParam param){

        if( WechatHook.LauncherUI_Activity == null ) {
            WechatHook.LauncherUI_Activity = (Activity) param.thisObject;
            //禁止睡眠，只需要在一条线程中添加即可,锁是activity级别
            PowerManager pm = (PowerManager ) WechatHook.LauncherUI_Activity.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"LauncherUI_Activity_Lock");
            wakelock.acquire();
        }

    }
}
