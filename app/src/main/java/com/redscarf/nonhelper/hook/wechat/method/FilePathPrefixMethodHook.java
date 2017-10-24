package com.redscarf.nonhelper.hook.wechat.method;

import com.redscarf.nonhelper.hook.wechat.WechatHook;
import com.redscarf.nonhelper.utils.XposedLogUtil;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * <p>function: hook微信用户文件夹
 * <p>User: LeeJohn
 * <p>Date: 2017/08/21
 * <p>Version: 1.0
 */
public class FilePathPrefixMethodHook {

    /**
     * hook 微信消息文件前缀的方法
     * loadClass 【com.tencent.mm.kernel.h】
     * method 【vl】
     * @param loadPackageParam
     *  打印信息
     * 【com.tencent.mm.kernel.h\vl】  result : { com.tencent.mm.kernel.e@3d38ef22 } params : {  }
     */
    public void hookWechatFilePathPrefix(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {
            if ( WechatHook.WECHAT_MSG_FILE_PATH_PREFIX == null) {
                XposedBridge.hookAllMethods(WechatHook.kernel_h_class, "vl", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if ( WechatHook.WECHAT_MSG_FILE_PATH_PREFIX == null && param.getResult() != null ) {
                            Field hjj = XposedHelpers.findField(WechatHook.kernel_e_class, "hjj");
                            if( hjj != null ){
                                hjj.setAccessible(true); //设置些属性是可以访问的
                                Object o = hjj.get(param.getResult());
                                if( o != null ){
                                    WechatHook.WECHAT_MSG_FILE_PATH_PREFIX = o.toString();
                                    XposedLogUtil.log("FilePathPrefixMethodHook ---- hookWechatFilePathPrefix : " + WechatHook.WECHAT_MSG_FILE_PATH_PREFIX);
                                }
                            }
                        }
                    }
                });
            }
        }catch (Exception e){
            XposedLogUtil.log(e);
        }
    }
}
