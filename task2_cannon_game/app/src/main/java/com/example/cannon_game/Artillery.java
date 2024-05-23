package com.example.cannon_game;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
//大炮类
public class Artillery {
    private Matrix matrix;  //大炮的变换矩阵
    private Paint paint;    //大炮的画笔
    private Bitmap bitmap;  //大炮的图片
    private int centerX,centerY;    //大炮中心点
    public Artillery(Matrix matrix, Paint paint, Bitmap bitmap) {
        this.matrix = matrix;
        this.paint = paint;
        // 缩小大炮图片
        this.bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3, false);

    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public void setCenter(int centerX, int centerY){
        this.centerX=centerX;
        this.centerY=centerY;
    }
}