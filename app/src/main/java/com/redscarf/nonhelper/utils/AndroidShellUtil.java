package com.redscarf.nonhelper.utils;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>function: 执行cmd命令
 * <p>User: LeeJohn
 * <p>Date: 2017/08/21
 * <p>Version: 1.0
 */
public class AndroidShellUtil {
    private static final String WINDOW_DUMP_XML_PATH = "/storage/emulated/0/window_dump.xml";
    public static final String WECHAT_TINKER_PATH = "/data/data/com.tencent.mm/tinker";
    public static final String WECHAT_TINKER_FILES_PATH = WECHAT_TINKER_PATH + "/*" ;
    /**
     * 点击事件
     * @param x
     * @param y
     */
    public static void tap(int x , int y){
            execShellCmd("input tap " + x + " " + y);//点击公众号
    }

    /**
     * 输入文本事件
     * @param text
     */
    public static void inputText(String text){
        execShellCmd("input text " + text);//小米手机输入法设置成英文输入
    }

    /**
     * 发送按键事件
     * @param keyCode
     */
    public static void inputKey(String keyCode){
        execShellCmd("input keyevent " + keyCode);
    }

    /**
     * 设置文件/文件夹 访问权限
     * @param flag 权限度 000 / 777 ...
     * @param path 路径
     */
    public static void chmod(String flag ,String path ){
        execShellCmd("chmod -R " + flag + " " + path );
    }

    /**
     * 删除文件
     * @param path
     */
    public static void rm(String path ){
        if( path == null || path.equals("") || path.equals("/") || path.equals("/*") ){
            return;
        }
        execShellCmd("rm -rf " + path );
    }

    /**
     * 执行shell命令
     *
     * @param cmd
     */
    public static void execShellCmd(String cmd) {
        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            XposedLogUtil.log(t);
        }
    }


    /**
     * 通过正则表达式获取指定元素的中心坐标
     * @param regx 正则表达式
     * @return int[0] 中心 x 坐标，int[1]中心 y 坐标
     */
    public static int[] uiXmlBounds(String regx){
        try {
            int[] centerBounds = new int[2];
            AndroidShellUtil.execShellCmd("uiautomator dump");//生成当前xml界面
            Thread.sleep(1000);
            //读取文件
            String xml = FileUtils.readString(WINDOW_DUMP_XML_PATH);
            String bounds = "";
            Pattern pattern = Pattern.compile(regx);
            Matcher matcher = pattern.matcher(xml);
            while (matcher.find()) {
                bounds = matcher.group(1);
            }
            String boundsStr = bounds.replaceAll("\\[", "").replaceAll("]", ",");

            int[] boundsArray = new int[4];
            String[] boundsSplit = boundsStr.split(",");
            for (int i = 0; i < boundsSplit.length; i++) {
                boundsArray[i] = Integer.valueOf(boundsSplit[i]);
            }
            centerBounds[0] = (boundsArray[0] + boundsArray[2]) / 2;
            centerBounds[1] = (boundsArray[1] + boundsArray[3]) / 2;
            return centerBounds;
        }catch (Exception e){
            XposedLogUtil.log(e);
            return null;
        }
    }

    /**
     * 清空通讯录联系人
     */
    public static void deleteALlContract(){
        execShellCmd("pm clear com.android.providers.contacts");
    }

    /**
     * 打开某个activity,带参数
     */
    public static void openActivity(String packageName ,String className ,String paramsKey,String paramValue){
        if( paramsKey != null && !paramsKey.equals("") && paramValue != null && !paramValue.equals("")){
            execShellCmd("am start -n " + packageName + "/" + className + " --es " + paramsKey + " " + paramValue );
        }else {
            execShellCmd("am start -n " + packageName + "/" + className );
        }

    }

    /**
     * 打开某个activity,带参数
     */
    public static void openActivity(String packageName ,String className){
        execShellCmd("am start -n " + packageName + "/" + className );
    }
}
