package com.project.kelin.combinenumbers;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.kelin.combinenumbers.R;

import org.w3c.dom.Text;

public class AnswerActivity extends Activity {
    LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        content = (LinearLayout) findViewById(R.id.layout_content);
        int[] path = CombineUtils.getPathArray();
        int number[] = CombineUtils.pathNumber.clone();
        int total = number[path[path.length - 1]];
        int totalScore = 0;
        for (int i = path.length - 2; i > 0; i--) {
            TextView textView = new TextView(this);
            textView.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
            textView.setTextSize(15);
            textView.setText(String.format(getResources().getString(R.string.answer_step),
                    path.length - i - 1, total, number[path[i]], total + number[path[i]],
                    totalScore, total + number[path[i]], total + number[path[i]] + totalScore));
            total += number[path[i]];
            totalScore += total;
            content.addView(textView);
        }

    }


}
