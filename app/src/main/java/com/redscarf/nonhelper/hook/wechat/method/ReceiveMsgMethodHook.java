package com.redscarf.nonhelper.hook.wechat.method;

import android.content.ContentValues;

import com.redscarf.nonhelper.hook.wechat.WechatHook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * <p>function: hook微信接收消息方法
 * <p>User: LeeJohn
 * <p>Date: 2017/09/08
 * <p>Version: 1.0
 */
public class ReceiveMsgMethodHook {

    /**
     * hook 微信接收消息方法
     * loadClass 【com.tencent.mm.bh.g】
     * method 【a】
     * 与LauncherUI 同个一进程 ，不需要重新建netty对象
     * 接收消息 统一在 com.tencent.mm.bh.g(a) 方法上拦截，区分好status关系 。
     * 自己发表情没有status字段。自己发图片（包括自己操作手机和接口调用），
     * 需要拦截获取本地图片的路径 自己发语音的代码 与逻辑 优化
     */
    public void hookReceiveMsg() {
        try{
            XposedHelpers.findAndHookMethod(WechatHook.bh_g_class,"a",String.class,String.class,ContentValues.class,boolean.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    XposedLogUtil.printMethodInfo(WechatHook.bh_g_class, param);
                    if(WechatHook.WECHAT_MSG_FILE_PATH_PREFIX != null && param.args != null && param.args.length == 4){
                        String table = (String)param.args[0];
                        if( table != null && table.equals("message") ){//message 消息表
                            ContentValues result = (ContentValues)param.args[2];
                            //文本字段
                            Integer status  = result.getAsInteger("status"); //status =3 是他人发
                            Long msgId  = result.getAsLong("msgId");
                            Long msgSeq  = result.getAsLong("msgSeq");
                            Long createTime  = result.getAsLong("createTime");
                            Long msgSvrId  = result.getAsLong("msgSvrId");
                            String talker  = result.getAsString("talker");
                            String content = result.getAsString("content");
                            Integer isSend = result.getAsInteger("isSend");
                            Integer type = result.getAsInteger("type");
                            Long bizChatId = result.getAsLong("bizChatId");
                            Integer talkerId = result.getAsInteger("talkerId");
                            Integer flag = result.getAsInteger("flag");

                            String bizClientMsgId = result.getAsString("bizClientMsgId");//图片有的字段
                            String imgPath = result.getAsString("imgPath");//图片,视频,音频有的字段

                            if( type != null  ) { // 个人发送自定义和专辑表情时，status=null
                                switch (type){
                                    case 1: //文本，或系统自带表情
                                        break;
                                    case 3: //图片
                                        break;
                                    case 34: //音频,可在此上传文件
                                        break;
                                    case 42: //名片
                                        break;
                                    case 43: //视频
                                        break;
                                    case 47: //自定义表情或专辑表情,在此上传表情文件
                                        break;
                                    case 48: //位置消息
                                        break;
                                    case 49: //来自应用的链接(群邀请链接|公众号文章|音乐|链接|收藏内容)
                                        break;
                                    case 10000: //系统的提示消息, 此时的 status 为 4
                                        break;
                                    case 419430449: //转账
                                        break;
                                    case 436207665: //红包
                                        luckMoneyMsg(content,talker,createTime,isSend);
                                        break;
                                    default:
                                        return;
                                }
                            }
                        }
                    }
                }
            });

        }catch (Exception e){
            XposedBridge.log(e);
        }
    }

    private void luckMoneyMsg(String content, String talker,Long createTime, Integer isSend) {
        //自动领取红包
        LuckyMoneyHook.autoReceive(talker,content);
    }

}
