package com.project.kelin.combinenumbers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kelin on 14-7-14.
 */
public class CombineRingView extends View {
    private final static int ITEM_WIDTH=80;
    private HashMap<Integer,ArrayList<RectF>> mRectFListMap=new HashMap<Integer, ArrayList<RectF>>();
    private HashMap<Integer,Edge> mEdgeHashMap=new HashMap<Integer, Edge>();
    private HashMap<Integer,Integer> mNumHashMap=new HashMap<Integer, Integer>();
    private Paint paintText=new Paint();
    private Paint paintFill=new Paint();
    private Paint paintStroke=new Paint();
    private Paint paintScore=new Paint();
    private Paint paint=new Paint();
    private int mRow,mColumn,mCount;
    private int[][] mData,mNum;
    private int mCurrentTabIndex=-1;
    private int mScore=0;
    private Context mContext;
    public CombineRingView(Context context,int[][] data,int row,int column,int count,int[][] num) {
        super(context);
        mData=data;
        mRow=row;
        mColumn=column;
        mCount=count;
        mNum=num;
        dealWithDataArray();
        initRect();
        mContext=context;
        setClickable(true);
    }
    private void dealWithDataArray(){
        for(int index=1;index<=mCount;index++) {
            Edge edge=new Edge();
            edge.right=(index%mCount)+1;
            edge.left=index-1==0? mCount:index-1;
            if(edge.left==0) edge.left=mCount;
            mEdgeHashMap.put(index,edge);
        }
    }

    private void initRect() {
        int num=0;
        for (int index = 1; index <= mCount; index++) {
            ArrayList<RectF> rectFs = new ArrayList<RectF>();
            for (int i = 0; i < mRow; i++) {
                for (int j = 0; j < mColumn; j++) {
                    if (mData[i][j] == index) {
                        RectF rect = new RectF(j * ITEM_WIDTH, (i) * ITEM_WIDTH, (j + 1) * ITEM_WIDTH, (i + 1) * ITEM_WIDTH);
                        rectFs.add(rect);
                        num=mNum[i][j];
                    }
                }
            }
            mRectFListMap.put(index, rectFs);
            mNumHashMap.put(index,num);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mColumn*ITEM_WIDTH+10,mRow*ITEM_WIDTH+10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(android.R.color.transparent));
        paintFill.setAntiAlias(true);
        paintStroke.setAntiAlias(true);
        paintText.setAntiAlias(true);
        paintScore.setAntiAlias(true);
        paintFill.setStyle(Paint.Style.FILL);
        paintText.setStyle(Paint.Style.FILL);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintFill.setColor(getResources().getColor(android.R.color.holo_blue_light));
        paintStroke.setColor(getResources().getColor(android.R.color.white));
        paintText.setColor(getResources().getColor(android.R.color.white));
        paintScore.setColor(getResources().getColor(android.R.color.black));
        paintScore.setTextSize(60f);
        paintText.setTextSize(25f);
        paintStroke.setStrokeWidth(2f);
        for (int index = 1; index <= mCount; index++) {
            if(mRectFListMap.get(index)==null) continue;
            if(mCurrentTabIndex!=-1) {
                if (index == mCurrentTabIndex) {
                    paintFill.setColor(getResources().getColor(android.R.color.holo_green_light));
                } else {
                    paintFill.setColor(getResources().getColor(android.R.color.holo_blue_light));
                }
                if (index != mEdgeHashMap.get(mCurrentTabIndex).left&&index != mEdgeHashMap.get(mCurrentTabIndex).right
                        &&index!=mCurrentTabIndex) {
                    paintFill.setAlpha(100);
                    paintStroke.setStrokeWidth(7f);
                }else {
                    paintFill.setAlpha(255);
                    paintStroke.setStrokeWidth(2f);
                }
            }
            Path path=new Path();
            RectF rectFUnion=new RectF();

            for(int i=0;i<mRectFListMap.get(index).size();i++) {
                path.addRect(mRectFListMap.get(index).get(i), Path.Direction.CW);
                rectFUnion.union(mRectFListMap.get(index).get(i));
            }

            canvas.drawPath(path, paintFill);
            canvas.drawRect(rectFUnion, paintStroke);

        }
        for (int index = 1; index <= mCount; index++) {
            if(mRectFListMap.get(index)==null) continue;
            Rect textBound=new Rect();
            String text=mNumHashMap.get(index).toString();
            paintText.getTextBounds(text,0,text.length(),textBound);
            canvas.drawText(text, mRectFListMap.get(index).get(0).centerX()-paintText.measureText(mNumHashMap.get(index).toString())/2,
                    mRectFListMap.get(index).get(0).centerY()+textBound.height()/2,paintText);
        }
        Rect textBound=new Rect();
        paintText.getTextBounds(String.valueOf(mScore),0,(String.valueOf(mScore)).length(),textBound);
        Rect rect=new Rect(ITEM_WIDTH,ITEM_WIDTH,(mColumn-1)*ITEM_WIDTH,(mRow-1)*ITEM_WIDTH);
        canvas.drawText(String.valueOf(mScore),rect.centerX()-paintScore.measureText(String.valueOf(mScore))/2,rect.centerY()+textBound.height()/2,paintScore);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                for (int i=1;i<=mCount;i++){
                    if(mRectFListMap.get(i)==null) continue;
                    for (int j=0;j<mRectFListMap.get(i).size();j++){
                        if(mRectFListMap.get(i).get(j).contains(event.getX(),event.getY())){
                            mCurrentTabIndex=i;
                            invalidate();
                            return super.onTouchEvent(event);

                        }
                    }
                }
                return false;

            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:

                int left=mEdgeHashMap.get(mCurrentTabIndex).left;
                int right=mEdgeHashMap.get(mCurrentTabIndex).right;
                for(int i=0;i<mRectFListMap.get(left).size();i++){
                    if(mRectFListMap.get(left).get(i).contains(event.getX(),event.getY())){
                        mEdgeHashMap.get(left).right=mEdgeHashMap.get(mCurrentTabIndex).right;
                        mEdgeHashMap.get(mEdgeHashMap.get(mCurrentTabIndex).right).left=left;
                        mRectFListMap.get(left).addAll(mRectFListMap.get(mCurrentTabIndex));
                        mRectFListMap.remove(mCurrentTabIndex);
                        mEdgeHashMap.remove(mCurrentTabIndex);
                        mScore+=mNumHashMap.get(mCurrentTabIndex)+mNumHashMap.get(left);
                        Toast.makeText(mContext, "+(" + mNumHashMap.get(mCurrentTabIndex) + "+" + mNumHashMap.get(left) + ")", Toast.LENGTH_SHORT).show();
                        mNumHashMap.put(left,mNumHashMap.get(left) + mNumHashMap.get(mCurrentTabIndex));
                        mCurrentTabIndex=-1;
                        invalidate();
                        return super.onTouchEvent(event);
                    }
                }
                for(int i=0;i<mRectFListMap.get(right).size();i++){
                    if(mRectFListMap.get(right).get(i).contains(event.getX(),event.getY())){
                        mEdgeHashMap.get(right).left=mEdgeHashMap.get(mCurrentTabIndex).left;
                        mEdgeHashMap.get(mEdgeHashMap.get(mCurrentTabIndex).left).right=right;
                        mRectFListMap.get(right).addAll(mRectFListMap.get(mCurrentTabIndex));
                        mRectFListMap.remove(mCurrentTabIndex);
                        mEdgeHashMap.remove(mCurrentTabIndex);
                        mScore+=mNumHashMap.get(mCurrentTabIndex)+mNumHashMap.get(right);
                        Toast.makeText(mContext,"+("+mNumHashMap.get(mCurrentTabIndex)+"+"+mNumHashMap.get(right)+")",Toast.LENGTH_SHORT).show();
                        mNumHashMap.put(right,mNumHashMap.get(mCurrentTabIndex)+mNumHashMap.get(right));
                        mCurrentTabIndex=-1;
                        invalidate();
                        return super.onTouchEvent(event);
                    }
                }
                invalidate();



        }
        return super.onTouchEvent(event);
    }
    private static class Edge{
        int left;
        int right;
    }
}
