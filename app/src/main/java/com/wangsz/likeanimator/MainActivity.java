package com.wangsz.likeanimator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private IconAnimatorView iconAnimatorView1;
    private IconAnimatorView iconAnimatorView2;

    private Button button1;
    private Button button2;

    private CountAnimatorView countAnimatorView;
    private Button buttonUp;
    private Button buttonDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iconAnimatorView1 = findViewById(R.id.ica_1);
        iconAnimatorView2 = findViewById(R.id.ica_2);

        button1 = findViewById(R.id.btn_1);
        button2 = findViewById(R.id.btn_2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconAnimatorView1.setIconSelected(!iconAnimatorView1.getIconSelected(),true);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconAnimatorView2.setIconSelected(!iconAnimatorView2.getIconSelected(),true);
            }
        });

        countAnimatorView = findViewById(R.id.cav);
        buttonUp = findViewById(R.id.btn_up);
        buttonDown = findViewById(R.id.btn_down);
        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countAnimatorView.addOne();
            }
        });
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countAnimatorView.minusOne();
            }
        });

    }
}
