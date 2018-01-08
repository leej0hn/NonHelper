package com.redscarf.nonhelper.hook.wechat;

import android.app.Activity;

import com.redscarf.nonhelper.hook.wechat.method.FilePathPrefixMethodHook;
import com.redscarf.nonhelper.hook.wechat.method.LauncherUIMethodHook;
import com.redscarf.nonhelper.hook.wechat.method.LuckyMoneyHook;
import com.redscarf.nonhelper.hook.wechat.method.ReceiveMsgMethodHook;
import com.redscarf.nonhelper.hook.wechat.method.WechatSQLMethodHook;
import com.redscarf.nonhelper.utils.AndroidShellUtil;
import com.redscarf.nonhelper.utils.XposedLogUtil;

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * wechat version 6.5.10
 * <p>function: 微信Hook
 * <p>User: LeeJohn
 * <p>Date: 2017/09/08
 * <p>Version: 1.0
 */
public class WechatHook {
    public static boolean IS_DELETE_TINKER = false;
    public static final String WECHAT_PACKAGE_NAME                                                       = "com.tencent.mm";                                                                 //微信主包
    public static final String ui_LauncherUI_class_name                                                  = "com.tencent.mm.ui.LauncherUI";                                                   //微信主界面包类名
    public static final String plugin_webview_ui_tools_webviewUI_class_name                              = "com.tencent.mm.plugin.webview.ui.tools.WebViewUI";                               //浏览器相关类--调用【k】拦截uin,key
    public static final String plugin_search_ui_FTSMainUI_class_name                                     = "com.tencent.mm.plugin.search.ui.FTSMainUI";                                      //搜索界面，可直接startActivity打开
    public static final String ui_bindmobile_MobileFriendUI_class_name                                   = "com.tencent.mm.ui.bindmobile.MobileFriendUI";                                    //添加手机通讯录界面，可直接命令am start -n 打开
    public static final String plugin_setting_ui_setting_SelfQRCodeUI_class_name                         = "com.tencent.mm.plugin.setting.ui.setting.SelfQRCodeUI";                          //查看二维码界面


    private static final String sdk_platformtools_h_class_name                                           = "com.tencent.mm.sdk.platformtools.h";                                             //加载微信 用户文件路径类包名
    private static final String s_n_class_name                                                           = "com.tencent.mm.s.n";                                                             //hook 群ID与群名称相关
    private static final String storage_x_class_name                                                     = "com.tencent.mm.storage.x";                                                       //hook 群ID与群名称相关
    private static final String kernel_h_class_name                                                      = "com.tencent.mm.kernel.h";                                                        //hook 微信消息文件前缀的方法相关类
    private static final String kernel_e_class_name                                                      = "com.tencent.mm.kernel.e";                                                        //hook 微信消息文件前缀的方法相关类
    private static final String storage_aw_class_name                                                    = "com.tencent.mm.storage.aw";                                                      //hook 微信接收消息方法相关类
    private static final String c_b_i_class_name                                                         = "com.tencent.mm.c.b.i";                                                           //hook 发送语音的对象相关类
    private static final String bh_g_class_name                                                          = "com.tencent.mm.bh.g";                                                            //hook 视频和图像自动加载相关类,SQL数据库对象
    private static final String modelcdntran_d_class_name                                                = "com.tencent.mm.modelcdntran.d";                                                  //hook 视频和图像自动加载相关类--调用【a】生成field_mediaId
    private static final String modelcdntran_b_class_name                                                = "com.tencent.mm.modelcdntran.b";                                                  //hook 视频和图像自动加载相关类--调用【a】下载文件
    private static final String modelcdntran_c_class_name                                                = "com.tencent.mm.modelcdntran.c";                                                  //自动下载图片前，初始化网络环境类
    private static final String modelcdntran_i_class_name                                                = "com.tencent.mm.modelcdntran.i";                                                  //微信请求下载文件参数信息类
    private static final String booter_notification_a_h_class_name                                       = "com.tencent.mm.booter.notification.a.h";                                         //hook 微信ID与微信昵称
    private static final String modelvoice_q_class_name                                                  = "com.tencent.mm.modelvoice.q";                                                    //发送音频消息线程--调用【F】静态方法通过音频文件名获取音频文件路径
    private static final String modelvoice_u_class_name                                                  = "com.tencent.mm.modelvoice.u";                                                    //发送音频消息线程--调用【mb】静态方法通过转发用户名获取生成的音频文件名
    private static final String modelvoice_o_class_name                                                  = "com.tencent.mm.modelvoice.o";                                                    //发送音频消息线程--调用【ls】静态方法通过文件名获取文件的大小名
    private static final String modelmulti_j_class_name                                                  = "com.tencent.mm.modelmulti.j";                                                    //发送文本消息线程--文本的对象
    private static final String s_ao_class_name                                                          = "com.tencent.mm.s.ao";                                                            //发送文本消息线程--发送文本的对象
    private static final String af_n_class_name                                                          = "com.tencent.mm.af.n";                                                            //发送图片相关类
    private static final String plugin_webview_c_e_class_name                                            = "com.tencent.mm.plugin.webview.c.e";                                              //hook 搜索微信公众号结果
    private static final String modelsimple_l_class_name                                                 = "com.tencent.mm.modelsimple.l";                                                   //hook 微信内部打开一个web视图时返回的url地址相关类
    private static final String s_c_class_name                                                           = "com.tencent.mm.s.c";                                                             //操作群聊--调用【wr】静态方法生成【com.tencent.mm.plugin.messenger.foundation.a.a.d】对象, 生成的对象调用【b】方法传入【com.tencent.mm.al.p】对象退出群聊
                                                                                                                                                                                             //       --调用【uV】静态方法生成【com.tencent.mm.storage.t】对象, 生成的对象调用【get】方法传入【2】和【null】生成【com.tencent.mm.al.p】的第一个构造参数
    private static final String af_h_class_name                                                          = "com.tencent.mm.af.h";                                                            // 拦截自己操作发送图片时的图片路径
    private static final String al_p_class_name                                                          = "com.tencent.mm.al.p";                                                            //主动退出群聊的参数对象, 构造时传入【com.tencent.mm.storage.t】的【get】方法生成的对象和【String xxx@chatroom】群聊id
    private static final String e_b_av_class_name                                                        = "com.tencent.mm.e.b.av";                                                          //hook 收到好友验证消息的通知
    private static final String pluginsdk_model_m_class_name                                             = "com.tencent.mm.pluginsdk.model.m";                                               //这个对象存储好友验证消息信息, ticket,scene,username
    private static final String w_n_class_name                                                           = "com.tencent.mm.w.n";                                                             //【com.tencent.mm.s.ao】调用【uH】生成该对象，生成的对象调用【a】传入【com.tencent.mm.pluginsdk.model.m】对象和【0】
    private static final String pluginsdk_model_h_class_name                                             = "com.tencent.mm.pluginsdk.model.h";                                               //【com.tencent.mm.pluginsdk.model.h】发送视频关键类
    private static final String sdk_f_e_class_name                                                       = "com.tencent.mm.sdk.f.e";                                                         //【com.tencent.mm.sdk.f.e】发送视频关键类
    private static final String plugin_luckmoney_c_ae_class_name                                         = "com.tencent.mm.plugin.luckymoney.c.ae";                                          //【com.tencent.mm.plugin.luckymoney.c.ae】自动领取红包相关类
    private static final String plugin_luckmoney_c_ab_class_name                                         = "com.tencent.mm.plugin.luckymoney.c.ab";                                          //【com.tencent.mm.plugin.luckymoney.c.ab】自动领取红包相关类
    private static final String pluginsdk_ui_preference_NormalUsesrHeaderPreference_class_name           = "com.tencent.mm.pluginsdk.ui.preference.NormalUserHeaderPreference";              //【com.tencent.mm.pluginsdk.ui.preference.NormalUserHeaderPreference】hook 微信联系人详细资料UI界面
    private static final String ui_chatting_cw_class_name                                                = "com.tencent.mm.ui.chatting.cw";                                                  //为了获取「ChatFooter」对象,hook这个类的构造方法
    private static final String ui_chatting_En_5b8fbb1e$a_class_name                                     = "com.tencent.mm.ui.chatting.En_5b8fbb1e$a";                                       //群聊at群友--调用【nvi】字段的【X】和【o】方法at好友
    private static final String sdk_platformtools_d_class_name                                           = "com.tencent.mm.sdk.platformtools.d";                                             //hook 群聊二维码的byte数组信息


    public static final String booter_NotifyReceiver_$_NotifyService_class_name                          = "com.tencent.mm.booter.NotifyReceiver$NotifyService";                             //hook 主进程的通知服务



    public static Class ui_LauncherUI_class;                                                  //微信主界面包类名
    public static Class sdk_platformtools_h_class;                                            //生成微信消息文件夹路径的类
    public static Class s_n_class;                                                            // hook 群ID与群名称相关
    public static Class storage_x_class;                                                      // hook 群ID与群名称相关
    public static Class kernel_h_class;                                                       // hook 微信消息文件前缀的方法相关类
    public static Class kernel_e_class;                                                       // hook 微信消息文件前缀的方法相关类
    public static Class storage_aw_class;                                                     // hook 微信接收消息方法相关类
    public static Class c_b_i_class;                                                          // hook 发送语音的对象相关类
    public static Class bh_g_class;                                                           // hook 视频和图像自动加载相关类
    public static Class modelcdntran_d_class;                                                 // hook 视频和图像自动加载相关类--调用【a】生成field_mediaId
    public static Class modelcdntran_b_class;                                                 // hook 视频和图像自动加载相关类--调用【a】下载文件
    public static Class modelcdntran_c_class;                                                 // 自动下载图片前，初始化网络环境类
    public static Class modelcdntran_i_class;                                                 // hook 视频和图像自动加载相关类--调用【a】下载文件
    public static Class booter_notification_a_h_class;                                        // hook 微信ID与微信昵称
    public static Class modelvoice_q_class;                                                   // 发送音频消息线程--调用【F】静态方法通过音频文件名获取音频文件路径
    public static Class modelvoice_u_class;                                                   // 发送音频消息线程--调用【mb】静态方法通过转发用户名获取生成的音频文件名
    public static Class modelvoice_o_class;                                                   // 发送音频消息线程--调用【ls】静态方法通过文件名获取文件的大小名
    public static Class modelmulti_j_class;                                                   // 发送文本消息线程--文本的对象
    public static Class s_ao_class;                                                           // 发送文本消息线程--发送文本的对象
    public static Class af_n_class;                                                           // 发送图片相关类
    public static Class plugin_webview_ui_tools_webviewUI_class;                              // 浏览器相关类--调用【k】拦截uin,key
    public static Class plugin_webview_c_e_class;                                             // hook 搜索微信公众号结果
    public static Class ui_bindmobile_MobileFriendUI_class;                                   // hook 微信关联手机通讯录联系人界面
    public static Class modelsimple_l_class;                                                  // hook 微信内部打开一个web视图时返回的url地址相关类
    public static Class s_c_class;                                                            // 操作群聊--调用【wr】静态方法生成【com.tencent.mm.plugin.messenger.foundation.a.a.d】对象, 生成的对象调用【b】方法传入【com.tencent.mm.al.p】对象退出群聊
                                                                                              //       --调用【uV】静态方法生成【com.tencent.mm.storage.t】对象, 生成的对象调用【get】方法传入【2】和【null】生成【com.tencent.mm.al.p】的第一个构造参数
    public static Class al_p_class;                                                           // 主动退出群聊的参数对象, 构造时传入【com.tencent.mm.storage.t】的【get】方法生成的对象和【String xxx@chatroom】群聊id
    public static Class af_h_class;                                                           // 拦截自己操作发送图片时的图片路径
    public static Class e_b_av_class;                                                         // hook 收到好友验证消息的通知
    public static Class pluginsdk_model_m_class;                                              // 这个对象存储好友验证消息信息, ticket,scene,username
    public static Class w_n_class;                                                            //【com.tencent.mm.s.ao】调用【uH】生成该对象，生成的对象调用【a】传入【com.tencent.mm.pluginsdk.model.m】对象和【0】
    public static Class pluginsdk_model_h_class;                                              //【com.tencent.mm.pluginsdk.model.h】发送视频关键类
    public static Class sdk_f_e_class;                                                        //【com.tencent.mm.pluginsdk.model.h】发送视频关键类
    public static Class plugin_luckmoney_c_ae_class;                                          //【com.tencent.mm.plugin.luckymoney.c.ae】自动领取红包相关类
    public static Class plugin_luckmoney_c_ab_class;                                          //【com.tencent.mm.plugin.luckymoney.c.ab】自动领取红包相关类
    public static Class pluginsdk_ui_preference_NormalUsesrHeaderPreference_class;            //【com.tencent.mm.pluginsdk.ui.preference.NormalUserHeaderPreference】hook 微信联系人详细资料UI界面
    public static Class ui_chatting_cw_class;                                                 //为了获取「ChatFooter」对象,hook这个类的构造方法
    public static Class ui_chatting_En_5b8fbb1e$a_class;                                      //群聊at群友--调用【nvi】字段的【X】和【o】方法at好友
    public static Class plugin_setting_ui_setting_SelfQRCodeUI_class;                         //微信查看二维码的视图UI
    public static Class sdk_platformtools_d_class;                                            //hook 群聊二维码的byte数组信息


    public static Class booter_NotifyReceiver_$_NotifyService_class;            //hook 主进程的通知服务


    public static String WECHAT_MSG_FILE_PATH_PREFIX ;//微信消息文件存在路径前缀
    public static LoadPackageParam loadPackageParam; //全局静态Xposed LoadPackageParam

    //数据库连接对象
    public static Object bh_g_obj;
    //发送音频对象
    public static Object c_b_i_obj;
    //微信主页activity
    public static Activity LauncherUI_Activity;
    //自动下载图片前，初始化网络环境类对象
    public static Object modelcdntran_b_obj;
    public static Object modelcdntran_c_obj;
    //UI 对象
    public static Object pluginsdk_ui_chat_ChatFooter_obj;

    public void handleLoadPackage(LoadPackageParam loadPackageParam) throws Throwable {
        if ( !loadPackageParam.packageName.equals(WECHAT_PACKAGE_NAME)) {
            return;
        }
        init(loadPackageParam);

        WechatSQLMethodHook wechatSQLMethodHook = new WechatSQLMethodHook();
        wechatSQLMethodHook.hookSQLObject(loadPackageParam);

        LauncherUIMethodHook launcherUIMethodHook = new LauncherUIMethodHook();
        launcherUIMethodHook.hookLauncherUi();

        LuckyMoneyHook luckyMoneyHook = new LuckyMoneyHook();
        luckyMoneyHook.hookLuckyMoneyReceiveUI();

        ReceiveMsgMethodHook receiveMsgMethodHook = new ReceiveMsgMethodHook();
        receiveMsgMethodHook.hookReceiveMsg();

    }

    /**
     * 初始化环境
     * @param loadPackageParam
     */
    private void init(LoadPackageParam loadPackageParam){
        deleteTinker();
        WechatHook.loadPackageParam = loadPackageParam;
        initLoadClass();
        FilePathPrefixMethodHook filePathPrefixMethodHook = new FilePathPrefixMethodHook();
        filePathPrefixMethodHook.hookWechatFilePathPrefix(loadPackageParam);
    }

    /**
     * 初始化加载类
     */
    private void initLoadClass(){
        try {
            ui_LauncherUI_class                                                 = loadPackageParam.classLoader.loadClass(ui_LauncherUI_class_name);
            sdk_platformtools_h_class                                           = loadPackageParam.classLoader.loadClass(sdk_platformtools_h_class_name);
            s_n_class                                                           = loadPackageParam.classLoader.loadClass(s_n_class_name);
            storage_x_class                                                     = loadPackageParam.classLoader.loadClass(storage_x_class_name);
            kernel_h_class                                                      = loadPackageParam.classLoader.loadClass(kernel_h_class_name);
            kernel_e_class                                                      = loadPackageParam.classLoader.loadClass(kernel_e_class_name);
            storage_aw_class                                                    = loadPackageParam.classLoader.loadClass(storage_aw_class_name);
            c_b_i_class                                                         = loadPackageParam.classLoader.loadClass(c_b_i_class_name);
            bh_g_class                                                          = loadPackageParam.classLoader.loadClass(bh_g_class_name);
            modelcdntran_d_class                                                = loadPackageParam.classLoader.loadClass(modelcdntran_d_class_name);
            modelcdntran_b_class                                                = loadPackageParam.classLoader.loadClass(modelcdntran_b_class_name);
            booter_notification_a_h_class                                       = loadPackageParam.classLoader.loadClass(booter_notification_a_h_class_name);
            modelcdntran_i_class                                                = loadPackageParam.classLoader.loadClass(modelcdntran_i_class_name);
            modelvoice_q_class                                                  = loadPackageParam.classLoader.loadClass(modelvoice_q_class_name);
            modelvoice_u_class                                                  = loadPackageParam.classLoader.loadClass(modelvoice_u_class_name);
            modelvoice_o_class                                                  = loadPackageParam.classLoader.loadClass(modelvoice_o_class_name);
            modelmulti_j_class                                                  = loadPackageParam.classLoader.loadClass(modelmulti_j_class_name);
            s_ao_class                                                          = loadPackageParam.classLoader.loadClass(s_ao_class_name);
            af_n_class                                                          = loadPackageParam.classLoader.loadClass(af_n_class_name);
            plugin_webview_ui_tools_webviewUI_class                             = loadPackageParam.classLoader.loadClass(plugin_webview_ui_tools_webviewUI_class_name);
            plugin_webview_c_e_class                                            = loadPackageParam.classLoader.loadClass(plugin_webview_c_e_class_name);
            ui_bindmobile_MobileFriendUI_class                                  = loadPackageParam.classLoader.loadClass(ui_bindmobile_MobileFriendUI_class_name);
            booter_NotifyReceiver_$_NotifyService_class                         = loadPackageParam.classLoader.loadClass(booter_NotifyReceiver_$_NotifyService_class_name);
            modelsimple_l_class                                                 = loadPackageParam.classLoader.loadClass(modelsimple_l_class_name);
            modelcdntran_c_class                                                = loadPackageParam.classLoader.loadClass(modelcdntran_c_class_name);
            s_c_class                                                           = loadPackageParam.classLoader.loadClass(s_c_class_name);
            al_p_class                                                          = loadPackageParam.classLoader.loadClass(al_p_class_name);
            af_h_class                                                          = loadPackageParam.classLoader.loadClass(af_h_class_name);
            e_b_av_class                                                        = loadPackageParam.classLoader.loadClass(e_b_av_class_name);
            pluginsdk_model_m_class                                             = loadPackageParam.classLoader.loadClass(pluginsdk_model_m_class_name);
            w_n_class                                                           = loadPackageParam.classLoader.loadClass(w_n_class_name);
            pluginsdk_model_h_class                                             = loadPackageParam.classLoader.loadClass(pluginsdk_model_h_class_name);
            sdk_f_e_class                                                       = loadPackageParam.classLoader.loadClass(sdk_f_e_class_name);
            plugin_luckmoney_c_ae_class                                         = loadPackageParam.classLoader.loadClass(plugin_luckmoney_c_ae_class_name);
            plugin_luckmoney_c_ab_class                                         = loadPackageParam.classLoader.loadClass(plugin_luckmoney_c_ab_class_name);
            pluginsdk_ui_preference_NormalUsesrHeaderPreference_class           = loadPackageParam.classLoader.loadClass(pluginsdk_ui_preference_NormalUsesrHeaderPreference_class_name);
            ui_chatting_cw_class                                                = loadPackageParam.classLoader.loadClass(ui_chatting_cw_class_name);
            ui_chatting_En_5b8fbb1e$a_class                                     = loadPackageParam.classLoader.loadClass(ui_chatting_En_5b8fbb1e$a_class_name);
            plugin_setting_ui_setting_SelfQRCodeUI_class                        = loadPackageParam.classLoader.loadClass(plugin_setting_ui_setting_SelfQRCodeUI_class_name);
            sdk_platformtools_d_class                                           = loadPackageParam.classLoader.loadClass(sdk_platformtools_d_class_name);

        }catch (ClassNotFoundException cnfe){
            XposedLogUtil.log(cnfe.getMessage());
        }
    }

    private void deleteTinker(){
        if( !IS_DELETE_TINKER ){
            //删除tinker文件
            AndroidShellUtil.rm(AndroidShellUtil.WECHAT_TINKER_FILES_PATH);
            AndroidShellUtil.chmod("000",AndroidShellUtil.WECHAT_TINKER_PATH);
            IS_DELETE_TINKER = true;
        }

    }


}
