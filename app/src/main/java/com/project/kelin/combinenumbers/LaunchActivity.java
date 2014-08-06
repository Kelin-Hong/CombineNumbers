package com.project.kelin.combinenumbers;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.project.kelin.adsdk.itl.AdsdkInterstitialManager;

public class LaunchActivity extends Activity {
    private final static String mogoID="8f95d4d4a10049c3b7dadc923bef8c50";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        initInterstitial();
        findViewById(R.id.ads).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitial();
            }
        });
        //
    }

    /**
     * 初始化全插屏对象
     * 初始化之前必须设置默认的AppKey和Activity
     */
    public void initInterstitial() {
        //设置当前Activity对象
        AdsdkInterstitialManager.setInitActivity(this);
        //初始化(必须先设置默认的Activity对象才能通过此方法初始化SDK)
        AdsdkInterstitialManager.shareInstance().adsMogoInterstitialByAppKey(mogoID);
        //设置 回调
        AdsdkInterstitialManager.shareInstance().adsMogoInterstitialByAppKey(mogoID);//.setAdsdkInterstitialListener(adsMogoInterstitialListener);
    }

    /**
     * 进入 展示时 机 *当应用需要展示全屏广告调用interstitialShow(boolean isWait);
     * 通知SDK进入展示时机,SDK会竭尽全力展示出广告,当然由于网络等问题不能立即展示 *广告的,您可以通过参数isWait来控制授权SDK在获得到广告后立即展示广告。
     */
    public void showInterstitial() {
        AdsdkInterstitialManager.shareInstance().adsMogoInterstitialByAppKey(mogoID).interstitialShow(true);
    }

    /**
     * 退出 展示时 机 *如果您之前进入了展示时机,并且isWait参数设置为YES,那么在需要取消等待广告展示的 *时候调用方法interstitialCancel();来通知SDK
     */
    public void cancelShow() {
        AdsdkInterstitialManager.shareInstance().adsMogoInterstitialByAppKey(mogoID).interstitialCancel();
    }

    /**
     * 改变当前栈顶的Activity对象
     * 如果当前栈顶Activit发生变化,如果未调用该方法改变Activit对象, * 有可能会导致广告无法展示或者崩溃
     */
    public void changeCurrentActivity() {
        AdsdkInterstitialManager.shareInstance().adsMogoInterstitialByAppKey(mogoID).changeCurrentActivity(this);
    }


    public void removeInterstitial() {
        AdsdkInterstitialManager.shareInstance().removeInterstitialInstanceByAppKey(mogoID);
    }
}
