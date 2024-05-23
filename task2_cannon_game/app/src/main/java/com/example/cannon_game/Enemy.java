package com.example.cannon_game;

import android.graphics.Paint;

//敌人类
public class Enemy {
    private Paint paint;        //敌人的画笔
    private float moveStep;     //敌人每次移动的步长
    private int radius;         //敌人的半径(圆)
    public Enemy(Paint paint, int radius, float moveStep) {
        this.paint = paint;
        this.moveStep = moveStep;
        this.radius=radius;
    }
    public Paint getPaint() {
        return paint;
    }
    public void setPaint(Paint paint) {
        this.paint = paint;
    }
    public float getMoveStep() {
        return moveStep;
    }
    public void setMoveStep(float moveStep) {
        this.moveStep = moveStep;
    }
    public int getRadius() {
        return radius;
    }
    public void setRadius(int radius) {
        this.radius = radius;
    }
}