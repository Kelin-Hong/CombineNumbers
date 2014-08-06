package com.project.kelin.combinenumbers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.project.kelin.adsdk.itl.AdsdkInterstitialManager;

import java.util.Date;
import java.util.Random;


public class MainActivity extends Activity implements CombineRingView.OnGameOverListener {
    private final static String mogoID="8f95d4d4a10049c3b7dadc923bef8c50";
    FrameLayout mContentLayout;
    TextView mGoalTextView;
    TextView mPassIndexTextView;
    TextView mShareTipTextView;
    CombineRingView mCombineRingView;
    private Dialog mShareDialog;
    private String mCurrentImageName;
    private int mGoalScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);
        mGoalTextView = (TextView) findViewById(R.id.goal);
        mPassIndexTextView = (TextView) findViewById(R.id.passIndex);
        mCombineRingView = new CombineRingView(this, 1);
        mPassIndexTextView.setTextSize(14f);
        mPassIndexTextView.setText(getResources().getString(R.string.game_introduce));
        mGoalTextView.setTextSize(20f);
        mGoalScore=CombineUtils.getMaxScore(DataConstants.sNumbers);
        mGoalTextView.setText(String.format(getResources().getString(R.string.goal_score),mGoalScore));
        mContentLayout.addView(mCombineRingView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mCombineRingView.setOnGameOverListener(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_share, null);
        mShareTipTextView=(TextView)dialogView.findViewById(R.id.share_tips);
        mShareTipTextView.setVisibility(View.GONE);
        mShareDialog = new AlertDialog.Builder(this).setView(dialogView).create();
        mShareDialog.setCanceledOnTouchOutside(false);
        initInterstitial();
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
    @Override
    protected void onResume() {
        super.onResume();
        changeCurrentActivity();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
    private void dealWithSuccess() {
        mCurrentImageName=Long.toHexString(new Date().getTime());
        ShareImageUtils.saveImage(MainActivity.this,mCurrentImageName);
        mContentLayout.removeAllViews();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_dialog, null);
//        mContentLayout.addView(dialogView);
        TextView message = (TextView) dialogView.findViewById(R.id.message);
        message.setText(getResources().getString(R.string.pass) + DataConstants.sSuccessMessage[DataConstants.sPassIndex - 1]);
        final Button nextPass = (Button) dialogView.findViewById(R.id.btn_positive);
        nextPass.setText(getResources().getString(R.string.next_pass));
        Button share = (Button) dialogView.findViewById(R.id.btn_negative);
        share.setText(getResources().getString(R.string.share));
        final Dialog dialog = new AlertDialog.Builder(this,R.style.CustomDialog).setView(dialogView).create();
        dialog.setCanceledOnTouchOutside(false);
        nextPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random=new Random();
                int n=random.nextInt(4);
                Log.v("random numbser---------------------->",n+"");
                if(n==1) {
                    showInterstitial();
                }
                mGoalTextView.setTextSize(30f);
                mPassIndexTextView.setTextSize(40f);
                gamePass();
                dialog.cancel();
                //ShareImageUtils.deleteImage(MainActivity.this,mCurrentImageName);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoalTextView.setTextSize(30f);
                mPassIndexTextView.setTextSize(40f);
                dialog.cancel();
                mShareTipTextView.setVisibility(View.GONE);
                mShareDialog.show();
                gamePass();
            }
        });

        dialog.show();

    }

    private void dealWithFail(int score) {
        mCurrentImageName=Long.toHexString(new Date().getTime());
        ShareImageUtils.saveImage(MainActivity.this,mCurrentImageName);
        mContentLayout.removeAllViews();
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.view_dialog, null);
        TextView message = (TextView) dialogView.findViewById(R.id.message);
        message.setText(getResources().getString(R.string.you_score)+ score + " " + DataConstants.sFailureMessage[DataConstants.sPassIndex - 1]);
        final Button getAnswer = (Button) dialogView.findViewById(R.id.btn_positive);
        getAnswer.setText(getResources().getString(R.string.get_answer));
        Button again = (Button) dialogView.findViewById(R.id.btn_negative);
        final Dialog dialog = new AlertDialog.Builder(this,R.style.CustomDialog).setView(dialogView).create();
        dialog.setCanceledOnTouchOutside(false);
        getAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareTipTextView.setVisibility(View.VISIBLE);
                mShareDialog.show();
//                Intent intent = new Intent(MainActivity.this, AnswerActivity.class);
//                startActivity(intent);
                dialog.cancel();
                gameAgain();

            }
        });
        again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random=new Random();
                int n=random.nextInt(4);
                Log.v("random numbser---------------------->",n+"");
                if(n==1) {
                    showInterstitial();
                }
                gameAgain();
                dialog.cancel();
                //ShareImageUtils.deleteImage(MainActivity.this,mCurrentImageName);
            }
        });

        dialog.show();
    }
    private void  gameAgain(){
        mCombineRingView = new CombineRingView(MainActivity.this, DataConstants.sPassIndex);
        mContentLayout.addView(mCombineRingView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mCombineRingView.setOnGameOverListener(MainActivity.this);
    }
    private void  gamePass(){
        if (DataConstants.sPassIndex + 1 > 10) return;
        mCombineRingView = new CombineRingView(MainActivity.this, ++DataConstants.sPassIndex);
        mContentLayout.addView(mCombineRingView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mCombineRingView.setOnGameOverListener(MainActivity.this);
        mPassIndexTextView.setText(String.format(getResources().getString(R.string.pass_index), DataConstants.sPassIndex));
        mGoalTextView.setText(String.format(getResources().getString(R.string.goal_score),CombineUtils.getMaxScore(DataConstants.sNumbers.clone())));
    }
    public void onOtherShareClick(View view){
        ShareImageUtils.starSystemShare(MainActivity.this, mCurrentImageName);
        mShareDialog.dismiss();
    }
    public void onWXShareClick(View view){
        ShareImageUtils.shareToWX(this,mCurrentImageName);
        mShareDialog.dismiss();

    }

    @Override
    public void onGameOver(int score) {


        if (score >= CombineUtils.getMaxScore(DataConstants.sNumbers.clone())) {
            dealWithSuccess();
        } else {
            dealWithFail(score);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(KeyEvent.KEYCODE_BACK == keyCode){
            ShareImageUtils.deleteImageFolder(this);
            MainActivity.this.finish();
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 改变当前栈顶的Activity对象
     * 如果当前栈顶Activit发生变化,如果未调用该方法改变Activit对象, * 有可能会导致广告无法展示或者崩溃
     */
    public void changeCurrentActivity() {
        AdsdkInterstitialManager.shareInstance().adsMogoInterstitialByAppKey(mogoID).changeCurrentActivity(this);
    }
    public void showInterstitial() {
        AdsdkInterstitialManager.shareInstance().adsMogoInterstitialByAppKey(mogoID).interstitialShow(true);
    }

    /**
     * 改变当前栈顶的Activity对象
     * 如果当前栈顶Activit发生变化，如果未调用该方法改变Activit对象，
     * 有可能会导致广告无法展示或者崩溃
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (AdsdkInterstitialManager.shareInstance()
                .containDefaultInterstitia()) {
            AdsdkInterstitialManager.shareInstance().defaultInterstitial()
                    .changeCurrentActivity(this);

        }

    }
}
