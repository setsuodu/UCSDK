package com.egame.unipaytest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.gundam.sdk.shell.even.SDKEventKey;
import cn.gundam.sdk.shell.even.SDKEventReceiver;
import cn.gundam.sdk.shell.even.Subscribe;
import cn.gundam.sdk.shell.exception.AliLackActivityException;
import cn.gundam.sdk.shell.exception.AliNotInitException;
import cn.gundam.sdk.shell.open.ParamInfo;
import cn.gundam.sdk.shell.open.UCOrientation;
import cn.gundam.sdk.shell.param.SDKParamKey;
import cn.gundam.sdk.shell.param.SDKParams;
import cn.uc.gamesdk.UCGameSdk;
import cn.uc.paysdk.face.commons.Response;
import cn.uc.paysdk.face.commons.SDKProtocolKeys;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class MyPluginClass extends Fragment {
    private static final String TAG = "ucgamesasdk";
    private static MyPluginClass Instance = null;
    private String gameObjectName;
    public static MyPluginClass GetInstance(String gameObject) {
        if(Instance == null) {
            Instance = new MyPluginClass();
            Instance.gameObjectName = gameObject;
            UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(Instance, TAG).commit();
        }
        return Instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //UCGameSdk.defaultSdk().registerSDKEventReceiver(receiver);
    }

    @Override
    public void onDestroy(){
        //UCGameSdk.defaultSdk().unregisterSDKEventReceiver(receiver);
        super.onDestroy();
    }

    public void registerSDK() {
        UCGameSdk.defaultSdk().registerSDKEventReceiver(receiver);
        UnityPlayer.UnitySendMessage(gameObjectName,"PluginCallBack", "注册=============");
    }

    public void unregisterSDK() {
        UCGameSdk.defaultSdk().unregisterSDKEventReceiver(receiver);
    }

    public void ucSdkInit(int gameid) {
        ParamInfo gameParamInfo = new ParamInfo();

        //gameParamInfo.setGameId(556324);
        //gameParamInfo.setGameId(1082089);
        gameParamInfo.setGameId(gameid);
        gameParamInfo.setOrientation(UCOrientation.PORTRAIT);

        SDKParams sdkParams = new SDKParams();
        sdkParams.put(SDKParamKey.GAME_PARAMS, gameParamInfo);

        UnityPlayer.UnitySendMessage(gameObjectName,"PluginCallBack", "初始化SDK=============");

        try {
            //初始化SDK
            UCGameSdk.defaultSdk().initSdk(getActivity(), sdkParams);
        } catch (AliLackActivityException e) {
            e.printStackTrace();
        }
    }

    public void doPay(String product_name, float amount, String order_id) {

        SDKParams sdkParams = new SDKParams();
        sdkParams.put(SDKProtocolKeys.APP_NAME, "1378捕鱼");//应⽤名称,将显示在⽀付界⾯
        sdkParams.put(SDKProtocolKeys.PRODUCT_NAME, product_name);//商品名称,将显示在⽀付界⾯
        sdkParams.put(SDKProtocolKeys.AMOUNT, amount);//充值⾦额,最多保留⼩数点后2位
        //sdkParams.put(SDKProtocolKeys.NOTIFY_URL, "http://192.168.1.1/notifypage.do");
        sdkParams.put(SDKProtocolKeys.NOTIFY_URL, "");//"http://home.5ihuhu.com/Active/Jiuyou/notify" //服务器通知地址
        sdkParams.put(SDKProtocolKeys.ATTACH_INFO, "你的透传参数");//服务器通知地址透传参数
        //sdkParams.put(SDKProtocolKeys.CP_ORDER_ID, "2016000"+System.currentTimeMillis());
        sdkParams.put(SDKProtocolKeys.CP_ORDER_ID, order_id);//cp充值订单号
        System.out.println("支付参数============="+sdkParams.toString());

        try {
            UCGameSdk.defaultSdk().pay(getActivity(), sdkParams);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG ,"charge failed - Exception: " + e.toString() + "\n");
        }
    }

    /**
     * 如果需要接入登录，请调用本方法
     */
    public void login() {
        UnityPlayer.UnitySendMessage(gameObjectName,"PluginCallBack", "登录=============");

        try {
            //UCGameSdk.defaultSdk().login(getActivity(), null);
            SDKParams params = new SDKParams();
            params.put("offline_login",false);
            UCGameSdk.defaultSdk().login(getActivity(), params);
        } catch (AliLackActivityException e) {
            e.printStackTrace();
        } catch (AliNotInitException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出游戏前，请调用本方法
     */
    public void exit() {

        try {
            UCGameSdk.defaultSdk().exit(getActivity(), null);
        } catch (Exception e) {
            e.printStackTrace();
            exitApp();
        }
    }

    private void exitApp() {
        //finish();
        //退出程序
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    //@Override
    public void onBackPressed() {
        exit();
    }

    private String formatDate(long time, String format) {
        SimpleDateFormat dateformat = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
        dateformat.applyPattern(format);
        return dateformat.format(time);
    }

    public class SDKResponse {
        public String tag;
        public String message;
    }

    /**
     *回调事件
     */
    private SDKEventReceiver receiver = new SDKEventReceiver() {

        @Subscribe(event = SDKEventKey.ON_EXIT_SUCC)
        private void onExit(String desc) {
            Log.d(TAG, "ON_EXIT_SUCC");
            Toast.makeText(getActivity(), ">> 游戏即将退出", Toast.LENGTH_LONG).show();

            exitApp();
        }

        @Subscribe(event = SDKEventKey.ON_EXIT_CANCELED)
        private void onExitCanceled(String desc) {
            Toast.makeText(getActivity(), ">> 继续游戏", Toast.LENGTH_LONG).show();
        }

        @Subscribe(event = SDKEventKey.ON_LOGIN_SUCC)
        private void onLoginSucc(final String sid) {
            if (TextUtils.isEmpty(sid)) {
                // 离线试玩
                Toast.makeText(getActivity(), ">> 离线登录成功", Toast.LENGTH_LONG).show();
            } else {
                // 用户登录
                Toast.makeText(getActivity(), ">> 用户登录成功", Toast.LENGTH_LONG).show();
                UnityPlayer.UnitySendMessage(gameObjectName,"OnLoginSuccess", sid);
            }
        }

        @Subscribe(event = SDKEventKey.ON_LOGIN_FAILED)
        private void onLoginFailed(String desc) {
            Toast.makeText(getActivity(), ">> 登录失败", Toast.LENGTH_LONG).show();
            UnityPlayer.UnitySendMessage(gameObjectName,"OnLoginFailure", desc);
        }

        @Subscribe(event = SDKEventKey.ON_INIT_SUCC)
        private void onInitSucc() {
            Toast.makeText(getActivity(), ">> 初始化成功", Toast.LENGTH_LONG).show();
        }

        @Subscribe(event = SDKEventKey.ON_INIT_FAILED)
        private void onInitFailed(String msg) {
            Toast.makeText(getActivity(), ">> 初始化失败", Toast.LENGTH_LONG).show();
        }

        @Subscribe(event = SDKEventKey.ON_CREATE_ORDER_SUCC)
        private void onPaySucc(final Bundle data) {
            UnityPlayer.UnitySendMessage(gameObjectName,"PluginCallBack", "UC_Pay_Success");

            Toast.makeText(getActivity(), ">> 支付成功", Toast.LENGTH_LONG).show();
            Log.d(TAG, "此处为支付成功回调: callback data = " + data.getString("response"));

            String response = data.getString("response");
            // 这里执行发货，如果发货成功需要设置以下值
            data.putString("result", Response.OPERATE_SUCCESS_MSG);
            // 如果发货失败需要设置以下值
            //data.putString("result", Response.OPERATE_FAIL_MSG);
            Log.d(TAG, "pay succ" + data);
        }

        @Subscribe(event = SDKEventKey.ON_PAY_USER_EXIT)
        private void onPayFail(String data) {
            UnityPlayer.UnitySendMessage(gameObjectName,"PluginCallBack", "UC_Pay_Failure");

            Toast.makeText(getActivity(), ">> 支付失败", Toast.LENGTH_LONG).show();
            Log.d(TAG, "pay exit");
        }
    };
}

