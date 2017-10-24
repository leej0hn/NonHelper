package com.redscarf.nonhelper.hook.wechat.method;

import android.net.Uri;

import com.redscarf.nonhelper.hook.wechat.WechatHook;
import com.redscarf.nonhelper.utils.XposedLogUtil;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * <p>function: hook微信红包
 * <p>User: LeeJohn
 * <p>Date: 2017/10/12
 * <p>Version: 1.0
 */

public class LuckyMoneyHook {
    private static final String default_version = "v1.0";

    /**
     * hook 领到红包
     * loadClass 【com.tencent.mm.plugin.luckymoney.c.ae】
     * method 【a】
     */
    public void hookLuckyMoneyReceiveUI() {
        try {
            XposedHelpers.findAndHookMethod(WechatHook.plugin_luckmoney_c_ae_class, "a", Integer.TYPE, String.class, JSONObject.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam methodHookParam) {
                    try {
                        final String str = (String) XposedHelpers.getObjectField(methodHookParam.thisObject, "nhg");
                        final String talkerBy = SendIDToTalkerMap.Instance().getTalkerBy(str);
                        if (talkerBy == null) {
                            return;
                        }
                        if (((Integer) XposedHelpers.getObjectField(methodHookParam.thisObject, "nkj")).intValue() == 0) {
                            sendAbMessage(methodHookParam.thisObject, talkerBy, default_version);
                            return;
                        }
                        return;
                    } catch (Throwable th) {
                        XposedLogUtil.log(th);
                        th.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            XposedLogUtil.log("LuckyMoneyHook hookLuckyMoneyReceiveUI " + e.getMessage());
        }
    }

    /**
     * 触发自动领红包
     * @param talker
     * @param content
     */
    public static void autoReceive(String talker,String content){
        try {
            String access$000 = getFromXml(content, "nativeurl");
            Uri parse = Uri.parse(access$000);
            int parseInt = Integer.parseInt(parse.getQueryParameter("msgtype"));
            int parseInt2 = Integer.parseInt(parse.getQueryParameter("channelid"));
            String queryParameter = parse.getQueryParameter("sendid");
            SendIDToTalkerMap.Instance().addSendidToTalker(queryParameter, talker);
            if (sendAeMessage(parseInt2, queryParameter, access$000, parseInt, default_version)) {
                XposedLogUtil.log("6510 send  ae message successed");
            } else {
                XposedLogUtil.log("send  ae message failed.");
            }
        }catch (Exception e){
            XposedLogUtil.log("LuckyMoneyHook autoReceive " + e.getMessage());
        }
    }

    private static boolean sendAeMessage(int i, String str, String str2, int i2, String str3) {
        boolean z = false;
        try {
            Object newInstance = XposedHelpers.newInstance(WechatHook.plugin_luckmoney_c_ae_class, new Object[]{Integer.valueOf(i), str, str2, Integer.valueOf(i2), str3});
            if (newInstance == null) {
                XposedLogUtil.log("Create ae instance failed.");
            } else {
                z = doAkVyFunction(newInstance, 0);
            }
        } catch (Throwable th) {
            XposedLogUtil.log(th);
        }
        return z;
    }

    private boolean sendAbMessage(Object obj, String str, String str2) {
        boolean z = false;
        try {
            Object newInstance = XposedHelpers.newInstance(WechatHook.plugin_luckmoney_c_ab_class, new Object[]{XposedHelpers.getObjectField(obj, "msgType"), XposedHelpers.getObjectField(obj, "nlx"), XposedHelpers.getObjectField(obj, "nhg"), XposedHelpers.getObjectField(obj, "hGb"), "", "", str, str2, XposedHelpers.getObjectField(obj, "nlP")});
            if (newInstance == null) {
                XposedLogUtil.log("Create ae instance failed.");
            } else {
                z = doAkVyFunction(newInstance, 0);
            }
        } catch (Throwable th) {
            XposedLogUtil.log(th);
        }
        return z;
    }

    private static boolean doAkVyFunction(Object obj, int i) {
        try {
            Object callStaticMethod = XposedHelpers.callStaticMethod(WechatHook.s_ao_class, "uH", new Object[0]);
            if (callStaticMethod == null) {
                XposedLogUtil.log("call ak.vy message failed.");
                return false;
            }
            XposedHelpers.callMethod(callStaticMethod, "a", new Object[]{obj, Integer.valueOf(i)});
            return true;
        } catch (Throwable th) {
            XposedLogUtil.log(th);
            return false;
        }
    }

    private static String getFromXml(String str, String str2) throws Exception {
        String substring = str.substring(str.indexOf("<msg>"));
        XmlPullParserFactory newInstance = XmlPullParserFactory.newInstance();
        newInstance.setNamespaceAware(true);
        XmlPullParser newPullParser = newInstance.newPullParser();
        newPullParser.setInput(new StringReader(substring));
        substring = "";
        for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
            if (eventType == 2 && newPullParser.getName().equals(str2)) {
                newPullParser.nextToken();
                return newPullParser.getText();
            }
        }
        return substring;
    }

    static class SendIDToTalkerMap {
        private static SendIDToTalkerMap instance;
        public static Map<String, String> sendidToTalker = new HashMap();

        private SendIDToTalkerMap() {
        }

        public static SendIDToTalkerMap Instance() {
            if (instance != null) {
                return instance;
            }
            synchronized (SendIDToTalkerMap.class) {
                if (instance != null) {
                    SendIDToTalkerMap sendIDToTalkerMap = instance;
                    return sendIDToTalkerMap;
                }
                instance = new SendIDToTalkerMap();
                return instance;
            }
        }

        public synchronized void addSendidToTalker(String str, String str2) {
            if (!(str == null || str2 == null)) {
                if (!(str.isEmpty() || str2.isEmpty())) {
                    if (sendidToTalker.containsKey(str)) {
                        sendidToTalker.remove(str);
                    }
                    sendidToTalker.put(str, str2);
                }
            }
        }

        public synchronized String getTalkerBy(String str) {
            String str2;
            str2 = null;
            if (sendidToTalker.containsKey(str)) {
                str2 = (String) sendidToTalker.get(str);
                sendidToTalker.remove(str);
            }
            return str2;
        }

    }

}
