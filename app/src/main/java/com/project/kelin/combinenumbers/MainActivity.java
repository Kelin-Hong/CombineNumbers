package com.project.kelin.combinenumbers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Date;


public class MainActivity extends Activity implements CombineRingView.OnGameOverListener {
    FrameLayout mContentLayout;
    TextView mGoalTextView;
    TextView mPassIndexTextView;
    CombineRingView mCombineRingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);
        mGoalTextView=(TextView)findViewById(R.id.goal);
        mPassIndexTextView=(TextView)findViewById(R.id.passIndex);
        mCombineRingView = new CombineRingView(this,1);
//        mPassIndexTextView.setText("第"+DataConstants.sPassIndex+"关");
        mPassIndexTextView.setTextSize(14f);
        mPassIndexTextView.setText("游戏规则：点击并拖动下面某个方块到相邻的方块中，将上面的数字合并，便可获得两个方块中的数字做为分数加到总分," +
                "我们需要把所有方块合并到只剩一个方块，获得的总分如果达到目标分数，便可进入下一关。");
        mGoalTextView.setTextSize(20f);
        mGoalTextView.setText("目标分数 "+CombineUtils.getMaxScore(DataConstants.sNumbers));
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
//        mContentLayout.addView(mCombineRingView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        mCombineRingView.setOnGameOverListener(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGameOver(int score) {
        if(score>=CombineUtils.getMaxScore(DataConstants.sNumbers)){
            mContentLayout.removeAllViews();;
            mGoalTextView.setTextSize(30f);
            mPassIndexTextView.setTextSize(40f);
            if(DataConstants.sPassIndex+1>10) return;
            mCombineRingView = new CombineRingView(this,++DataConstants.sPassIndex);
            mContentLayout.addView(mCombineRingView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mCombineRingView.setOnGameOverListener(this);
            mPassIndexTextView.setText("第"+DataConstants.sPassIndex+"关");
            mGoalTextView.setText("目标分数 "+CombineUtils.getMaxScore(DataConstants.sNumbers));
        }else{
            mContentLayout.removeAllViews();;
            LayoutInflater inflater=this.getLayoutInflater();
            View dialogView =inflater.inflate(R.layout.view_dialog,null);
            TextView message=(TextView)dialogView.findViewById(R.id.message);
            message.setText("您的得分："+score+" "+DataConstants.sMessage[DataConstants.sPassIndex-1]);
            final Button share=(Button)dialogView.findViewById(R.id.share);
            Button again=(Button)dialogView.findViewById(R.id.again);
            final Dialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShareImageUtils.shareMsg(MainActivity.this,"分享","IQ挑战","一般人只能玩到第4关，你也来试试！！！",Long.toHexString(new Date().getTime()));
                    dialog.cancel();
                }
            });
            again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCombineRingView = new CombineRingView(MainActivity.this,DataConstants.sPassIndex);
                    mContentLayout.addView(mCombineRingView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    mCombineRingView.setOnGameOverListener(MainActivity.this);
                    dialog.cancel();
                }
            });

            dialog.show();

        }
    }
}
