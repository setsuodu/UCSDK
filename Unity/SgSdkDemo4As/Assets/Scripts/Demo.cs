using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Demo : MonoBehaviour
{
    private string className = "com.egame.unipaytest.MyPluginClass";
    private string methodName = "GetInstance";
    AndroidJavaObject jo;
    AndroidJavaClass jc;

    public Button initBtn;
    public Button loginBtn;
    public Button payBtn;
    public Button logoutBtn;

    void Awake()
    {
        initBtn.onClick.AddListener(doInit);
        loginBtn.onClick.AddListener(doLogin);
        payBtn.onClick.AddListener(doPay);
        logoutBtn.onClick.AddListener(doLogout);
    }

    void OnDestroy()
    {
        Debug.Log("卸载...");
        jo.Call("unregisterSDK");

        initBtn.onClick.RemoveListener(doInit);
        loginBtn.onClick.RemoveListener(doLogin);
        payBtn.onClick.RemoveListener(doPay);
        logoutBtn.onClick.RemoveListener(doLogout);
    }

    void Start()
    {
        jc = new AndroidJavaClass(className);
        jo = jc.CallStatic<AndroidJavaObject>(methodName, gameObject.name);

        Debug.Log("注册...");
        jo.Call("registerSDK");
    }

    void doInit()
    {
        Debug.Log("初始化...");
        jo.Call("ucSdkInit");
    }

    void doLogin()
    {
        Debug.Log("登录中...");
        jo.Call("login");
    }

    void doPay()
    {
        Debug.Log("调起支付...");
        jo.Call("doPay");
    }

    void doLogout()
    {
        Debug.Log("登出...");
        jo.Call("exit");
    }

    public void PluginCallBack(string message)
    {
        Debug.Log("回调：" + message);
    }
}
