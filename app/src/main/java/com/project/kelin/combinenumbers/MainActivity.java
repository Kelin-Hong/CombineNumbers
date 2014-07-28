package com.project.kelin.combinenumbers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.adsmogo.offers.MogoOffer;
import com.adsmogo.offers.MogoOfferChooserAdapter;
import com.adsmogo.offers.MogoOfferListCallback;
import com.adsmogo.offers.MogoOfferPointCallBack;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements CombineRingView.OnGameOverListener,MogoOfferPointCallBack,MogoOfferListCallback {
    FrameLayout mContentLayout;
    TextView mGoalTextView;
    TextView mPassIndexTextView;
    CombineRingView mCombineRingView;
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
//        mGoalTextView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mGoalTextView.setTextSize(30f);
//                mGoalTextView.setText("目标分数 "+CombineUtils.getMaxScore(DataConstants.sNumbers));
//
//            }
//        },60000);
        mContentLayout.addView(mCombineRingView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mCombineRingView.setOnGameOverListener(MainActivity.this);


        MogoOffer.init(this, "8f95d4d4a10049c3b7dadc923bef8c50");

       //设置顺序展示模式下，选择积分墙入口的弹出框标题；
        MogoOffer.setOfferListTitle("获取积分");
        MogoOffer.setOfferEntranceMsg("商城");

        MogoOffer.setMogoOfferScoreVisible(false);

        MogoOffer.setMogoOfferListCallback(this);
        MogoOffer.addPointCallBack(this);
    }
    @Override
    protected void onResume() {
        MogoOffer.RefreshPoints(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        MogoOffer.clear(this);
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
                mGoalTextView.setTextSize(30f);
                mPassIndexTextView.setTextSize(40f);
                gamePass();
                dialog.cancel();
                ShareImageUtils.deleteImage(MainActivity.this,mCurrentImageName);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                dealWithShare();
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

//                 dealWithShare();
//                Intent intent = new Intent(MainActivity.this, AnswerActivity.class);
//                startActivity(intent);
                MogoOffer.showOffer(MainActivity.this);
                dialog.cancel();
                gameAgain();

            }
        });
        again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameAgain();
                dialog.cancel();
                ShareImageUtils.deleteImage(MainActivity.this,mCurrentImageName);
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
    }
    public void onWXShareClick(View view){
        ShareImageUtils.shareToWX(this,mCurrentImageName);
    }
    private void dealWithShare(){
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_share, null);
        final Dialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
    public void updatePoint(long l) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(KeyEvent.KEYCODE_BACK == keyCode){
            MainActivity.this.finish();
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void showOfferListDialog(final Context context, String dialogTitle, String[] tips)
    {
        // TODO Auto-generated method stub

        final AlertDialog dialog = new AlertDialog.Builder(context).create();

        MogoOfferChooserAdapter adapter = new MogoOfferChooserAdapter(context, tips);

        dialog.setTitle(dialogTitle);

        ListView listView = new ListView(context);
        listView.setBackgroundColor(0xffffffff);
        listView.setPadding(0, 0, 0, 0);
        listView.setCacheColorHint(0x00000000);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int which, long arg3)
            {
                // TODO Auto-generated method stub

                if (dialog != null)
                {
                    dialog.dismiss();
                }

                MogoOffer.showSingleOffer(context, which);
            }
        });
        dialog.setView(listView);
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

    }
}
