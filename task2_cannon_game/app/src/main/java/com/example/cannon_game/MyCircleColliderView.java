package com.example.cannon_game;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;

public class MyCircleColliderView extends View {
    private int score = 0; // 分数变量
    private static final int WIN_SCORE = 100; // 胜利分数
    private int shotsFired;
    private boolean gameOver;
    private Thread gameThread;
    private AlertDialog gameOverDialog;
    public static final int MISS_PENALTY = 2; // 未命中惩罚分数
    public static final int HIT_REWARD = 3; // 击中奖励分数
    private double timeLeft; // 剩余时间
    private double totalElapsedTime; // 总经过时间
    private Bullet bullet;  // 子弹对象
    private Artillery arti; // 大炮对象
    private Enemy enemy;    // 敌人对象
    private int maxEnemyNum = 30; // 敌人的最大数量
    private boolean isSpawning = false;   // 正在生成敌人
    private float currentRotate = -90;    // 当前大炮的旋转方向
    private List<MyPoint> bulletPoints = new ArrayList<>(); // 每一个子弹的坐标点
    private List<MyPoint> enemyPoints = new ArrayList<>();  // 每一个敌人的坐标点
    private Random positionRand = new Random();
    private BeatEnemyListener beatEnemyListener;
    private Handler handler = new Handler();
    private Runnable gameRunnable;
    private int enemiesDefeated = 0; // 记录击败的敌人数量



    public MyCircleColliderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        gameOver = true; // 初始化为游戏结束状态
    }

    private void init() {
        Bitmap artilleryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.artillery_image); // 确保图片文件位于 res/mipmap 目录下
        if (artilleryBitmap == null) {
            throw new IllegalArgumentException("Resource not found: R.mipmap.arti");
        }
        arti = new Artillery(new Matrix(), new Paint(), artilleryBitmap);
        arti.setCenter(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT - arti.getBitmap().getHeight() / 2);
        Paint bulletPaint = new Paint();
        bulletPaint.setColor(Color.RED);
        bullet = new Bullet(bulletPaint, 25, 20);
        Paint enemyPaint = new Paint();
        enemyPaint.setColor(Color.BLUE);
        enemy = new Enemy(enemyPaint, 25, 2);


        timeLeft = 100; // 初始化剩余时间为100毫秒
        totalElapsedTime = 0; // 初始化总经过时间
        gameRunnable = new Runnable() {
            @Override
            public void run() {
                totalElapsedTime += 0.1; // 每次调用增加0.1秒
                timeLeft -= 0.1; // 剩余时间减少0.1秒
                if (timeLeft <= 0) {
                    timeLeft = 0;
                    showGameOverDialog(R.string.game_over);
                    stopGame();
                } else {
                    handler.postDelayed(this, 100);
                }
                invalidate();
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 在游戏循环中更新剩余时间
        totalElapsedTime += 0.1; // 每次调用增加0.1秒
        timeLeft -= 0.1; // 剩余时间减少0.1秒

        // 如果剩余时间小于等于0，设置为0.0并显示游戏结束对话框
        if (timeLeft <= 0) {
            timeLeft = 0.0;
            gameOver = true;
            showGameOverDialog(R.string.lose);
        }

        // 判断游戏胜利条件
        if (isGameWon()) {
            showGameOverDialog(R.string.win);
            gameOver = true;
        }

        // 绘制剩余时间
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);
        canvas.drawText("时间: " + String.format("%.1f", timeLeft), 50, 50, textPaint);

        // 缩小大炮的绘制
        int scaledWidth = Constants.SCREEN_WIDTH / 3; // 缩小为屏幕宽度的1/3
        int scaledHeight = scaledWidth * arti.getBitmap().getHeight() / arti.getBitmap().getWidth();

        arti.getMatrix().reset();
        arti.getMatrix().postTranslate(arti.getCenterX() - scaledWidth / 2, Constants.SCREEN_HEIGHT - scaledHeight); // 将大炮置于底部中心
        arti.getMatrix().postScale((float)scaledWidth / arti.getBitmap().getWidth(), (float)scaledHeight / arti.getBitmap().getHeight(), arti.getCenterX(), Constants.SCREEN_HEIGHT);
        canvas.drawBitmap(arti.getBitmap(), arti.getMatrix(), arti.getPaint());

        for (int i = 0; i < bulletPoints.size(); i++) { // 移动所有的点
            canvas.drawCircle(bulletPoints.get(i).getX(), bulletPoints.get(i).getY(), bullet.getRadius(), bullet.getPaint());
            bulletPoints.get(i).move(bullet.getMoveStep(), false);

            // 是否发生碰撞
            for (int j = 0; j < enemyPoints.size(); j++) {
                // 在击中敌人时增加分数
                if (bulletPoints.get(i).isCollider(enemyPoints.get(j), bullet.getRadius(), enemy.getRadius())) {
                    // 移除子弹
                    bulletPoints.remove(i--);
                    // 移除敌人
                    enemyPoints.remove(j);

                    // 增加分数
                    score += HIT_REWARD;
                    // 发生监听事件
                    if (beatEnemyListener != null){
                        beatEnemyListener.onBeatEnemy();}
                    // 检查是否达到胜利条件
                    if (isGameWon()) {
                        showGameOverDialog(R.string.win);
                        gameOver = true;
                    }
                    break;
                }
            }
        }

        for (int i = 0; i < bulletPoints.size(); i++) { // 移除离开屏幕区域的点
            if (bulletPoints.get(i).isOutOfBounds(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT)) {
                bulletPoints.remove(i--);
            }
        }

        for (int i = 0; i < enemyPoints.size(); i++) {
            canvas.drawCircle(enemyPoints.get(i).getX(), enemyPoints.get(i).getY(), enemy.getRadius(), enemy.getPaint());
            enemyPoints.get(i).move(enemy.getMoveStep(), true);
        }

        for (int i = 0; i < enemyPoints.size(); i++) { // 移除离开屏幕区域的点
            if (enemyPoints.get(i).isOutOfBoundsWithOutTop(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT)) {
                enemyPoints.remove(i--);
            }
        }

        // 如果敌人未到达最大数量，并且不在生成敌人，继续生成敌人
        if (enemyPoints.size() < maxEnemyNum && !isSpawning) {
            isSpawning = true;
            postDelayed(spawnRunnable, 300);
        }

        // 绘制分数
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);
        canvas.drawText("分数: " + score, 50, 100, textPaint);

        postInvalidateDelayed(5);
    }
    // 判断游戏胜利条件的方法
    private boolean isGameWon() {
        // 在这里添加您的游戏胜利条件判断逻辑
        // 返回 true 表示游戏胜利，返回 false 表示游戏未胜利
        return enemiesDefeated >= 50; // 当击败的敌人数量达到50时，游戏胜利
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 计算水平轴与 大炮中心和点击的位置连成的直线 之间的夹角
                currentRotate = (float) Math.toDegrees(Math.atan((arti.getCenterY() - event.getY()) / (event.getX() - arti.getCenterX())));
                // 将点击的位置放入点的集合中
                bulletPoints.add(new MyPoint(arti.getCenterX(), arti.getCenterY(), Math.toRadians(currentRotate)));
                break;
        }
        return true;
    }

    // 生成敌人
    private void instantiateEnemy() {
        // 将敌人位置保存到集合中
        enemyPoints.add(new MyPoint(positionRand.nextInt(Constants.SCREEN_WIDTH), -5, Math.toRadians(-90)));
        isSpawning = false;
    }

    private Runnable spawnRunnable = new Runnable() {
        @Override
        public void run() {
            instantiateEnemy();
        }
    };

    // 设计击中敌人时的监听
    public interface BeatEnemyListener {
        void onBeatEnemy();
    }

    public void setBeatEnemyListener(BeatEnemyListener listener) {
        this.beatEnemyListener = listener;
    }

// 显示游戏结束对话框方法
    private void showGameOverDialog(final int messageId) {
        if (gameOverDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(messageId));
            builder.setMessage("游戏结束");
            builder.setPositiveButton("重新开始", (dialog, which) -> startGame());
            builder.setNegativeButton("退出", (dialog, which) -> stopGame());
            gameOverDialog = builder.create();
        }

        if (!gameOverDialog.isShowing()) {
            gameOverDialog.show();
        }
    }

    public void startGame() {
        shotsFired = 0;
        timeLeft = 100; // 重置时间
        totalElapsedTime = 0; // 重置总经过时间
        bulletPoints.clear(); // 清空子弹
        enemyPoints.clear(); // 清空敌人
        handler.postDelayed(gameRunnable, 100); // 启动计时器
        isSpawning = false;
        invalidate();
        if (gameOver) {
            gameOver = false;
            gameThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!gameOver) {
                        // 执行绘制和更新游戏逻辑
                        postInvalidate(); // 请求重新绘制
                        try {
                            Thread.sleep(16); // 大约60帧每秒
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            gameThread.start();
        }
    }


    // 停止游戏方法
    public void stopGame() {
        handler.removeCallbacks(gameRunnable);
        isSpawning = false;
        gameOver = true;
        if (gameThread != null) {
            try {
                gameThread.join(); // 等待线程结束
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
