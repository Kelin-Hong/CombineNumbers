package com.project.kelin.combinenumbers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;


/**
 * Created by kelin on 14-7-14.
 */
public class CombineRingView extends View {
    /**
     * width and height of every rect.
     */
    private static int RECT_WIDTH;
    private static float RECT_FONT_SIZE;
    private static float SCORE_FONT_SIZE;
    /**
     * CombineRingView is composed of lots of rect,we make a part of rect to a group,
     * then give a hash map to project to every group.for instance, double dimensional array
     * 1, 2, 2, 3
     * 1, 0, 0, 3
     * 1, 0, 0, 4
     * 1, 4, 4, 4
     * every number as key of hash map except zero,the value keep list to store every rect,by the
     * way,the CombineRingView contain 12 rect and distinct to 4 group.
     */
    private HashMap<Integer, ArrayList<RectF>> mRectFListMap = new HashMap<Integer, ArrayList<RectF>>();
    /**
     * hash map store adjacent group of current group. such as,the third group,the left adjacent of
     * group is second group,and the right adjacent of group is four group.
     */
    private HashMap<Integer, Edge> mEdgeHashMap = new HashMap<Integer, Edge>();
    /**
     * we have double dimensional array to decide which position to put what number.
     * 1, 2, 2, 3                     4, 4, 4, 5
     * 1, 0, 0, 3                     4, 0, 0, 5
     * 1, 0, 0, 4 ----projection----> 4, 0, 0, 9
     * 1, 4, 4, 4                     4, 9, 9, 9
     * on the base of above projection.we can clear to know conclusion below：
     * {key=1,value=4},{key=2,value=4},{key=3,value=5},{key=4,value=9}
     */
    private HashMap<Integer, Integer> mNumHashMap = new HashMap<Integer, Integer>();
    /**
     * paint to draw numbers in the rect.
     */
    private Paint paintText = new Paint();
    /**
     * every rect fill blue color and stroke with white color.
     * when state change. such as when press. we change fill color and width of stroke.
     */
    private Paint paintFill = new Paint();
    private Paint paintStroke = new Paint();
    private Paint addScorePaint = new Paint();
    Paint addScoreTextPaint = new Paint();
    /**
     * we use individual paint to draw score in the center of view.
     */
    private Paint paintScore = new Paint();
    /**
     * count meaning how many group have.
     */
    private int mRow, mColumn, mCount;
    /**
     * array data contain group id.
     * array num contain exact number to combine.
     */
    private int[][] mData, mNum;
    /**
     * when someone is been press down,sign which group was been selected.
     * mCurrentTabIndex the group Id.
     */
    private int mCurrentTabIndex = -1;
    /**
     * initialize score
     */
    private int mScore = 0;
    private Context mContext;

    private HashMap<Integer, Path> mUnionPathMap = new HashMap<Integer, Path>();
    private HashMap<Integer, RectF> mUnionRectMap = new HashMap<Integer, RectF>();
    private HashMap<Integer, Region> mUnionRegionMap = new HashMap<Integer, Region>();

    public void setOnGameOverListener(OnGameOverListener onGameOverListener) {
        this.onGameOverListener = onGameOverListener;
    }

    private OnGameOverListener onGameOverListener;

    private int adjacentAlpha = 255;

    private TreeMap<Integer, Integer> sortIndexMap = new TreeMap<Integer, Integer>();

    public CombineRingView(Context context, int passIndex) {
        super(context);
        DataConstants.sPassIndex = passIndex;
        DataConstants.initData();
        mData = DataConstants.sProjectionArray;
        mRow = mData.length;
        mColumn = mData[0].length;
        mCount = DataConstants.sNumbers.length - 1;
        mNum = DataConstants.sNumbersArray;
        mContext = context;
        initDimens();
        initDataArray();
        initRect();
        initPaint();
        setClickable(true);
    }

    private void initDimens() {
        RECT_WIDTH = (int) getResources().getDimension(R.dimen.width_rect);
        RECT_FONT_SIZE = getResources().getDimension(R.dimen.font_rect);
        SCORE_FONT_SIZE = getResources().getDimension(R.dimen.font_score);
    }

    /**
     * initialize EdgeHashMap.
     */
    private void initDataArray() {
        for (int index = 1; index <= mCount; index++) {
            Edge edge = new Edge();
            edge.right = (index % mCount) + 1;
            edge.left = index - 1 == 0 ? mCount : index - 1;
            if (edge.left == 0) edge.left = mCount;
            mEdgeHashMap.put(index, edge);
        }
    }

    /**
     * initialize Rect.
     */
    private void initRect() {
        int num = 0;
        for (int index = 1; index <= mCount; index++) {
            ArrayList<RectF> rectFs = new ArrayList<RectF>();
            for (int i = 0; i < mRow; i++) {
                for (int j = 0; j < mColumn; j++) {
                    if (mData[i][j] == index) {
                        RectF rect = new RectF(j * RECT_WIDTH, (i) * RECT_WIDTH, (j + 1) * RECT_WIDTH, (i + 1) * RECT_WIDTH);
                        //rect.inset(1,1);
                        rectFs.add(rect);
                        num = mNum[i][j];
                    }
                }
            }

            mRectFListMap.put(index, rectFs);
            mNumHashMap.put(index, num);
        }
    }

    private void initPaint() {
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
        paintScore.setColor(getResources().getColor(android.R.color.holo_blue_light));
        paintScore.setTextSize(SCORE_FONT_SIZE);
        paintText.setTextSize(RECT_FONT_SIZE);
        paintStroke.setStrokeWidth(3f);

        addScorePaint = paintFill;
        addScoreTextPaint.setAntiAlias(true);
        addScoreTextPaint.setColor(getResources().getColor(android.R.color.white));
        addScoreTextPaint.setTextSize(RECT_FONT_SIZE / 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mColumn * RECT_WIDTH, mRow * RECT_WIDTH);
    }

    private ArrayList<Integer> sortIndex() {
        for (int index = 1; index <= mCount; index++) {
            if (mRectFListMap.get(index) == null) continue;
            sortIndexMap.put(index, mRectFListMap.get(index).size());
        }
        ArrayList<Map.Entry<Integer, Integer>> entryArrayList = new ArrayList<Map.Entry<Integer, Integer>>(sortIndexMap.entrySet());
        Collections.sort(entryArrayList, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> lhs, Map.Entry<Integer, Integer> rhs) {
                return lhs.getValue().compareTo(rhs.getValue());
            }
        });
        ArrayList<Integer> sortIndexList = new ArrayList<Integer>();
        for (Map.Entry<Integer, Integer> item : entryArrayList) {
            sortIndexList.add(item.getKey());
        }
        return sortIndexList;
    }

    private void drawRegion(Region rgn, Canvas canvas) {
        RegionIterator iter = new RegionIterator(rgn);
        Rect r = new Rect();
        while (iter.next(r)) {
            canvas.drawRect(r, paintStroke);
        }
        iter = new RegionIterator(rgn);
        while (iter.next(r)) {
            canvas.drawRect(r, paintFill);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(android.R.color.transparent));
        mUnionPathMap.clear();
        mUnionRectMap.clear();
        for (int index = 1; index <= mCount; index++) {
            if (mRectFListMap.get(index) == null) continue;
            if (mCurrentTabIndex != -1) {
                if (index == mCurrentTabIndex || (isMoveIntoLeft && index == mEdgeHashMap.get(mCurrentTabIndex).left) || (isMoveIntoRight && index == mEdgeHashMap.get(mCurrentTabIndex).right)) {
                    paintFill.setColor(getResources().getColor(android.R.color.holo_green_light));
                    paintFill.setAlpha(255);
                } else {
                    paintFill.setColor(getResources().getColor(android.R.color.holo_blue_light));
                }
                if (index != mEdgeHashMap.get(mCurrentTabIndex).left && index != mEdgeHashMap.get(mCurrentTabIndex).right
                        && index != mCurrentTabIndex) {
                    paintFill.setAlpha(100);

                } else {
                    if (index != mCurrentTabIndex) {
                        paintFill.setAlpha(adjacentAlpha);

                    } else {
                        paintFill.setAlpha(255);

                    }
                }
            } else {
                paintFill.setColor(getResources().getColor(android.R.color.holo_blue_light));
                paintFill.setAlpha(255);

            }
            Path path = new Path();
            RectF rectFUnion = new RectF();
            Region region = new Region();
            for (int i = 0; i < mRectFListMap.get(index).size(); i++) {
                RectF rectF = mRectFListMap.get(index).get(i);

                path.addRect(rectF, Path.Direction.CW);

                rectFUnion.union(mRectFListMap.get(index).get(i));
                Rect rect = new Rect();
                mRectFListMap.get(index).get(i).roundOut(rect);
                region.op(rect, Region.Op.UNION);
            }

            mUnionPathMap.put(index, path);
            mUnionRectMap.put(index, rectFUnion);
            mUnionRegionMap.put(index, region);

            drawRegion(region, canvas);
        }

        for (int index = 1; index <= mCount; index++) {
            if (mRectFListMap.get(index) == null) continue;
            Rect textBound = new Rect();
            String text = mNumHashMap.get(index).toString();
            paintText.getTextBounds(text, 0, text.length(), textBound);
            canvas.drawText(text, mRectFListMap.get(index).get(0).centerX() - paintText.measureText(mNumHashMap.get(index).toString()) / 2,
                    mRectFListMap.get(index).get(0).centerY() + textBound.height() / 2, paintText);
        }
        Rect textBound = new Rect();
        paintText.getTextBounds(String.valueOf(mScore), 0, (String.valueOf(mScore)).length(), textBound);
        Paint.FontMetricsInt fontMetrics = paintScore.getFontMetricsInt();
        Rect targetRect = new Rect(0, 0, mColumn * RECT_WIDTH, mRow * RECT_WIDTH);
        int baseline = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        paintScore.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(mScore), targetRect.centerX(), baseline, paintScore);
        drawAddScore(canvas);
    }

    private Timer timer = new Timer();
    private TimerTask timerTask;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
        }
    };

    private void TimeScheduleAlpha() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (adjacentAlpha == 255) {
                    adjacentAlpha = 100;
                } else {
                    adjacentAlpha = 255;
                }
                handler.sendEmptyMessage(1);
            }
        };
        timer.schedule(timerTask, 0, 300);
    }

    private void timeScheduleAddScore() {

        addScoreCircleX1 = mCurrentRectFs.get(0).centerX();
        addScoreCircleY1 = mCurrentRectFs.get(0).centerY();
        addScoreCircleX2 = mOtherRectFs.get(0).centerX();
        addScoreCircleY2 = mOtherRectFs.get(0).centerY();
        TimerTask timerTask1 = new TimerTask() {
            @Override
            public void run() {
                isCurrentScoreCanDraw = true;
                float xSpeed = (mColumn * RECT_WIDTH / 2 - mCurrentRectFs.get(0).centerX()) / 10;
                float ySpeed = (mRow * RECT_WIDTH / 2 - mCurrentRectFs.get(0).centerY()) / 10;
                addScoreCircleX1 += xSpeed;
                addScoreCircleY1 += ySpeed;
                if (Math.abs(addScoreCircleX1 - mColumn * RECT_WIDTH / 2) <= 1 && Math.abs(addScoreCircleY1 - mRow * RECT_WIDTH / 2) <= 1) {
                    isCurrentScoreCanDraw = false;
                    mScore += mCurrentAddScore;
                    //mCombineCount++;
                    if (mCombineCount == mCount - 1) {
                        CombineRingView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                CombineRingView.this.onGameOverListener.onGameOver(mScore);
                            }
                        });
                    }
                    this.cancel();
                }
                handler.sendEmptyMessage(1);
            }
        };
        timer.schedule(timerTask1, 0, 100);
        TimerTask timerTask2 = new TimerTask() {
            @Override
            public void run() {
                isOtherScoreCanDraw = true;
                float xSpeed = (mColumn * RECT_WIDTH / 2 - mOtherRectFs.get(0).centerX()) / 10;
                float ySpeed = (mRow * RECT_WIDTH / 2 - mOtherRectFs.get(0).centerY()) / 10;
                addScoreCircleX2 += xSpeed;
                addScoreCircleY2 += ySpeed;
                if (Math.abs(addScoreCircleX2 - mColumn * RECT_WIDTH / 2) <= 1 && Math.abs(addScoreCircleY2 - mRow * RECT_WIDTH / 2) <= 1) {
                    isOtherScoreCanDraw = false;
                    mScore += mOtherAddScore;
                    mCombineCount++;
                    if (mCombineCount == mCount - 1) {
                        CombineRingView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                CombineRingView.this.onGameOverListener.onGameOver(mScore);
                            }
                        });

                    }
                    this.cancel();
                }
                handler.sendEmptyMessage(1);
            }
        };
        timer.schedule(timerTask2, 200, 100);
    }

    boolean isMoveIntoLeft = false;
    boolean isMoveIntoRight = false;
    boolean isCurrentScoreCanDraw = false, isOtherScoreCanDraw = false;
    private ArrayList<RectF> mCurrentRectFs;
    private ArrayList<RectF> mOtherRectFs;
    private int mCurrentAddScore, mOtherAddScore;
    float addScoreCircleX1, addScoreCircleY1, addScoreCircleX2, addScoreCircleY2;

    private void drawAddScore(Canvas canvas) {
        if (isCurrentScoreCanDraw) {
            canvas.drawCircle(addScoreCircleX1, addScoreCircleY1, RECT_WIDTH / 4, addScorePaint);
            Rect textBound = new Rect();
            String text = "+" + mCurrentAddScore;
            addScoreTextPaint.getTextBounds(text, 0, text.length(), textBound);
            canvas.drawText(text, addScoreCircleX1 - addScoreTextPaint.measureText(text) / 2,
                    addScoreCircleY1 + textBound.height() / 2, addScoreTextPaint);
        }
        if (isOtherScoreCanDraw) {
            canvas.drawCircle(addScoreCircleX2, addScoreCircleY2, RECT_WIDTH / 4, addScorePaint);
            Rect textBound = new Rect();
            String text = "+" + mOtherAddScore;
            addScoreTextPaint.getTextBounds(text, 0, text.length(), textBound);
            canvas.drawText(text, addScoreCircleX2 - addScoreTextPaint.measureText(text) / 2,
                    addScoreCircleY2 + textBound.height() / 2, addScoreTextPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int left;
        int right;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isCurrentScoreCanDraw || isOtherScoreCanDraw) return false;
                for (int i = 1; i <= mCount; i++) {
                    if (mRectFListMap.get(i) == null) continue;
                    for (int j = 0; j < mRectFListMap.get(i).size(); j++) {
                        if (mRectFListMap.get(i).get(j).contains(event.getX(), event.getY())) {
                            mCurrentTabIndex = i;
                            mCurrentRectFs = mRectFListMap.get(mCurrentTabIndex);
                            TimeScheduleAlpha();
                            invalidate();
                            return super.onTouchEvent(event);

                        }
                    }
                }
                return false;

            case MotionEvent.ACTION_MOVE:
                left = mEdgeHashMap.get(mCurrentTabIndex).left;
                right = mEdgeHashMap.get(mCurrentTabIndex).right;
                for (int i = 0; i < mRectFListMap.get(left).size(); i++) {
                    if (mRectFListMap.get(left).get(i).contains(event.getX(), event.getY()) && !isMoveIntoLeft && !isMoveIntoRight) {
                        mRectFListMap.get(left).addAll(mRectFListMap.get(mCurrentTabIndex));
                        mRectFListMap.remove(mCurrentTabIndex);
                        isMoveIntoLeft = true;
                        isMoveIntoRight = false;
                        mNumHashMap.put(left, mNumHashMap.get(left) + mNumHashMap.get(mCurrentTabIndex));
                        invalidate();
                        return super.onTouchEvent(event);
                    }
                }
                for (int i = 0; i < mRectFListMap.get(right).size(); i++) {
                    if (mRectFListMap.get(right).get(i).contains(event.getX(), event.getY()) && !isMoveIntoRight && !isMoveIntoLeft) {
                        mRectFListMap.get(right).addAll(mRectFListMap.get(mCurrentTabIndex));
                        mRectFListMap.remove(mCurrentTabIndex);
                        isMoveIntoRight = true;
                        isMoveIntoLeft = false;
                        mNumHashMap.put(right, mNumHashMap.get(mCurrentTabIndex) + mNumHashMap.get(right));
                        invalidate();
                        return super.onTouchEvent(event);
                    }

                }
                for (int i = 0; i < mCurrentRectFs.size(); i++) {
                    if (mCurrentRectFs.get(i).contains(event.getX(), event.getY())) {
                        if (isMoveIntoRight || isMoveIntoLeft) {
                            mRectFListMap.put(mCurrentTabIndex, mCurrentRectFs);

                        }
                        if (isMoveIntoLeft) {
                            mRectFListMap.get(left).removeAll(mCurrentRectFs);
                            mNumHashMap.put(left, mNumHashMap.get(left) - mNumHashMap.get(mCurrentTabIndex));
                        }
                        if (isMoveIntoRight) {
                            mRectFListMap.get(right).removeAll(mCurrentRectFs);
                            mNumHashMap.put(right, -mNumHashMap.get(mCurrentTabIndex) + mNumHashMap.get(right));
                        }
                        isMoveIntoRight = false;
                        isMoveIntoLeft = false;
                        invalidate();
                        return super.onTouchEvent(event);
                    }

                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                timerTask.cancel();
                left = mEdgeHashMap.get(mCurrentTabIndex).left;
                right = mEdgeHashMap.get(mCurrentTabIndex).right;
                mCurrentAddScore = mNumHashMap.get(mCurrentTabIndex);
                if (isMoveIntoRight || isMoveIntoLeft) {
                    mRectFListMap.put(mCurrentTabIndex, mCurrentRectFs);

                }
                if (isMoveIntoLeft) {
                    mRectFListMap.get(left).removeAll(mCurrentRectFs);
                    mNumHashMap.put(left, mNumHashMap.get(left) - mNumHashMap.get(mCurrentTabIndex));
                    mOtherRectFs = mRectFListMap.get(left);
                    mOtherAddScore = mNumHashMap.get(left);
                    combineLeft(left);
                    timeScheduleAddScore();
                }
                if (isMoveIntoRight) {
                    mRectFListMap.get(right).removeAll(mCurrentRectFs);
                    mNumHashMap.put(right, -mNumHashMap.get(mCurrentTabIndex) + mNumHashMap.get(right));
                    mOtherRectFs = mRectFListMap.get(right);
                    mOtherAddScore = mNumHashMap.get(right);
                    combineRight(right);
                    timeScheduleAddScore();
                }
                isMoveIntoRight = false;
                isMoveIntoLeft = false;
                mCurrentTabIndex = -1;
                invalidate();
        }
        return super.onTouchEvent(event);
    }

    private int mCombineCount = 0;

    private void combineLeft(int left) {
        mEdgeHashMap.get(left).right = mEdgeHashMap.get(mCurrentTabIndex).right;
        mEdgeHashMap.get(mEdgeHashMap.get(mCurrentTabIndex).right).left = left;
        mRectFListMap.get(left).addAll(mRectFListMap.get(mCurrentTabIndex));
        mRectFListMap.remove(mCurrentTabIndex);
        mEdgeHashMap.remove(mCurrentTabIndex);
        //mCombineCount++;
        //mScore += mNumHashMap.get(mCurrentTabIndex) + mNumHashMap.get(left);
        //Toast.makeText(mContext, "+(" + mNumHashMap.get(mCurrentTabIndex) + "+" + mNumHashMap.get(left) + ")", Toast.LENGTH_SHORT).show();
        mNumHashMap.put(left, mNumHashMap.get(left) + mNumHashMap.get(mCurrentTabIndex));
        mCurrentTabIndex = -1;
        //invalidate();
    }

    private void combineRight(int right) {
        mEdgeHashMap.get(right).left = mEdgeHashMap.get(mCurrentTabIndex).left;
        mEdgeHashMap.get(mEdgeHashMap.get(mCurrentTabIndex).left).right = right;
        mRectFListMap.get(right).addAll(mRectFListMap.get(mCurrentTabIndex));
        mRectFListMap.remove(mCurrentTabIndex);
        mEdgeHashMap.remove(mCurrentTabIndex);
        // mScore += mNumHashMap.get(mCurrentTabIndex) + mNumHashMap.get(right);
        //Toast.makeText(mContext, "+(" + mNumHashMap.get(mCurrentTabIndex) + "+" + mNumHashMap.get(right) + ")", Toast.LENGTH_SHORT).show();
        mNumHashMap.put(right, mNumHashMap.get(mCurrentTabIndex) + mNumHashMap.get(right));
        mCurrentTabIndex = -1;
        //mCombineCount++;
        //invalidate();
    }

    private static class Edge {
        int left;
        int right;
    }

    public static interface OnGameOverListener {
        public void onGameOver(int score);
    }
}
