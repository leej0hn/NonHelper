package com.redscarf.nonhelper.hook.wechat.method;

import com.redscarf.nonhelper.hook.wechat.WechatHook;
import com.redscarf.nonhelper.utils.XposedLogUtil;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * <p>function: hook 数据库连接对象
 * <p>User: LeeJohn
 * <p>Date: 2017/09/08
 * <p>Version: 1.0
 */
public class WechatSQLMethodHook {
    /**
     * hook 数据库连接对象
     * @param loadPackageParam
     * loadClass 【com.tencent.mm.bh.g】
     * method 【Constructors】
     */
    public void hookSQLObject(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {
            XposedBridge.hookAllConstructors(WechatHook.bh_g_class ,new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    XposedLogUtil.log("【com.tencent.mm.bh.g】 Constructors : " + param.thisObject.hashCode());
                    synchronized (this){
                        if( WechatHook.bh_g_obj == null ){
                            WechatHook.bh_g_obj = param.thisObject;
                        }
                    }
                }
            });
        }catch (Exception e){
            XposedLogUtil.log(e);
        }
    }
}
