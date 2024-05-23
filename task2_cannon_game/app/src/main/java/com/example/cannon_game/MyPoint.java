package com.example.cannon_game;

public class MyPoint {
    private int x;
    private int y;
    private double angle;   //角度

    public MyPoint(int x, int y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public double getAngle() {
        return angle;
    }
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public void set(int x,int y){
        this.x=x;
        this.y=y;
    }

    //点移动的方法
    /*
     * moveStep:步长
     * boundWidth:点所有在区域的宽度
     * boundHeight:点所在区域的高度
     * */
    public void move(float moveStep,boolean isEnemy){
        double moveY=0,moveX=0;
        if(getAngle()>=0){  //子弹在右上方
            moveX=moveStep*Math.cos(getAngle());
            moveY=moveStep*Math.sin(getAngle());
        }
        else{               //子弹在左上方（角度为负数）
            moveX=-moveStep*Math.cos(-getAngle());
            moveY=moveStep*Math.sin(-getAngle());
        }
        if(!isEnemy)set((int)(getX()+moveX),(int)(getY()-moveY));
        else set((int)(getX()+moveX),(int)(getY()+moveY));
    }

    //是否离开该区域
    public boolean isOutOfBounds(int boundWidth,int boundHeight){
        if(getX()>boundWidth || getX()<0 )return true;
        else if(getY()>boundHeight || getY()<0)return true;
        else return false;
    }

    //是否离开该区域，忽略顶部
    public boolean isOutOfBoundsWithOutTop(int boundWidth,int boundHeight){
        if(getX()<0 || getX()>boundWidth)return true;
        else if(getY()>boundHeight)return true;
        else return false;
    }

    //是否发生碰撞
    public boolean isCollider(MyPoint point,int bulletRadius,int enemyRadius){
        //两个圆的圆心的距离小于两个圆的半径之和时，说明两个圆发生碰撞
        return (getX()-point.getX())*(getX()-point.getX())+(getY()-point.getY())*(getY()-point.getY()) <= (bulletRadius+enemyRadius)*(bulletRadius+enemyRadius);
    }
}
