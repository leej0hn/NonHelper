package com.redscarf.nonhelper.hook;

import com.redscarf.nonhelper.hook.system.ContextHook;
import com.redscarf.nonhelper.hook.wechat.WechatHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * <p>function: Hook方法入口
 * <p>User: LeeJohn
 * <p>Date: 2017/09/08
 * <p>Version: 1.0
 */
public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(LoadPackageParam loadPackageParam) throws Throwable {
        ContextHook contextMethodHook = new ContextHook();
        contextMethodHook.handleLoadPackage(loadPackageParam);

        WechatHook wechatHook = new WechatHook();
        wechatHook.handleLoadPackage(loadPackageParam);
    }

}
