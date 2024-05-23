package com.example.cannon_game;

import android.graphics.Paint;

//子弹类
public class Bullet {
    private Paint paint;    //子弹的画笔
    private int radius;     //子弹的半径
    private float moveStep; //每次移动的步长
    public Bullet(Paint paint,int radius,int moveStep){
        this.paint=paint;
        this.radius=radius;
        this.moveStep=moveStep;
    }
    public Paint getPaint() {
        return paint;
    }
    public void setPaint(Paint paint) {
        this.paint = paint;
    }
    public int getRadius() {
        return radius;
    }
    public void setRadius(int radius) {
        this.radius = radius;
    }
    public float getMoveStep() {
        return moveStep;
    }
    public void setMoveStep(float moveStep) {
        this.moveStep = moveStep;
    }
}
