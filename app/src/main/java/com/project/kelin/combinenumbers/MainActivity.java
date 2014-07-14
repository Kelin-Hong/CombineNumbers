package com.project.kelin.combinenumbers;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;


public class MainActivity extends Activity {
    FrameLayout mContentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContentLayout=(FrameLayout)findViewById(R.id.layout_content);
        /*
        test data
         */
        int data[][]={{1,2,3,4,5},
                     {16,0,0,0,6},
                     {15,0,0,0,7},
                     {14,0,0,0,8},
                     {13,12,11,10,9}

        };
        int num[][]={{1,2,2,4,4},
                    {3,0,0,0,4},
                    {3,0,0,0,4},
                    {3,0,0,0,6},
                    {5,6,6,6,6}

        };
        int data1[][]={{1,2,2,3},
                       {1,0,0,3},
                       {1,0,0,4},
                       {1,4,4,4},


        };
        int num1[][]={{4,4,4,5},
                     {4,0,0,5},
                     {4,0,0,9},
                     {4,9,9,9},


        };
        CombineRingView combineRingView=new CombineRingView(this,data,data.length,data[0].length,16,num);
        mContentLayout.addView(combineRingView,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
}
