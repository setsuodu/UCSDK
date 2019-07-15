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
        if (Application.platform == RuntimePlatform.Android)
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
        if (Application.platform == RuntimePlatform.Android)
            jo.Call("registerSDK");
    }

    void doInit()
    {
        Debug.Log("初始化...");
        if (Application.platform == RuntimePlatform.Android)
        {
            //jo.Call("ucSdkInit");
            //jo.Call("ucSdkInit", 556324);
            jo.Call("ucSdkInit", 1082089);
        }
    }

    void doLogin()
    {
        Debug.Log("登录中...");
        if (Application.platform == RuntimePlatform.Android)
            jo.Call("login");
    }

    void doPay()
    {
        Debug.Log("调起支付...");
        var product_name = "红钻会员";
        var amount = 0.01f;
        var order_id = "201600241516";
        if (Application.platform == RuntimePlatform.Android)
            jo.Call("doPay", product_name, amount, order_id);
    }

    void doLogout()
    {
        Debug.Log("登出...");
        if (Application.platform == RuntimePlatform.Android)
            jo.Call("exit");
    }

    public void PluginCallBack(string message)
    {
        Debug.Log("SDK回调：" + message);
    }
}
