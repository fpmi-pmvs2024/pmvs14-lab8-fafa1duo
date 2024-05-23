package com.example.cannon_game;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private MyCircleColliderView colliderView;
    private TextView tv_score;
    private int score = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        tv_score = findViewById(R.id.tv_score);
        colliderView = findViewById(R.id.collider_view);
        colliderView.setBeatEnemyListener(new MyCircleColliderView.BeatEnemyListener() {
            @Override
            public void onBeatEnemy() {
                tv_score.setText("干掉了" + (++score) + "个敌人");
            }
        });
    }
}
