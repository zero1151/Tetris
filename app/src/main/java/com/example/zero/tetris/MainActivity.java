package com.example.zero.tetris;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zero.tetris.control.GameControl;
import com.example.zero.tetris.util.SocketThread;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //这里使用了activity弱引用，可多复习

    //view里面没有变量 没有数据
    //游戏区域控件
    View gamePanel;
    //下一块预览控件
    View nextPanel;
    //游戏控制器
    GameControl gameControl;

    //当前分数TextView
    public TextView scoreTextView;
    //最高分数TextView
    public TextView maxTextView;

    //联机状态
    public boolean online;

    //socket线程
//    public SocketThread socketThread;

    //为了防止内存泄漏，这里写个Handler静态类
    static class MyHandler extends Handler{
        WeakReference<MainActivity> activityWeakReference;
        private MyHandler(MainActivity activity){
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = activityWeakReference.get();
            //若activity被回收则为空
            if (activity != null) {
                String type = (String) msg.obj;
                if (type == null) return;
                if (type.equals("invalidate")) {
                    //刷新重绘view
                    activity.gamePanel.invalidate();
                    Log.d("deep", "执行重绘");
                    activity.nextPanel.invalidate();
                    ((Button) activity.findViewById(R.id.btnPause)).setText(R.string.pause);
                    //刷新分数
                    activity.gameControl.scoreModel.showCurrentScore(activity.scoreTextView);
                    activity.gameControl.scoreModel.showMaxScore(activity.maxTextView);

                } else if (type.equals("pause")) {
                    if (msg.arg1 == 0) {
                        ((Button) activity.findViewById(R.id.btnPause)).setText(R.string.continue1);
                    } else {
                        ((Button) activity.findViewById(R.id.btnPause)).setText(R.string.pause);
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        Handler myHandler = new MyHandler(this);
        //实例化游戏控制器
        Intent ipIntent = getIntent();
        String ip = ipIntent.getStringExtra("ip");
        Log.d("deep", "ip = " + ip);
        gameControl = new GameControl(ip,myHandler,this);
        initView();
        initListener();
        //加载本地数据
        gameControl.scoreModel.showCurrentScore(scoreTextView);
        gameControl.scoreModel.showMaxScore(maxTextView);


//        socketThread = new SocketThread(ip,myHandler,this,gameControl);
//        socketThread.start();
    }

    /**初始化视图*/
    public void initView(){

        //1·得到父容器
        FrameLayout layoutGame = findViewById(R.id.layoutGame);

        layoutGame.setPadding(Config.PADDING,Config.PADDING,Config.PADDING,Config.PADDING);

        //2·实例化游戏区域
        gamePanel = new View(this){
            //重写游戏区域绘制

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                //绘制
                gameControl.drawGame(canvas);
            }
        };
        //3·设置游戏区域大小
        gamePanel.setLayoutParams(new ViewGroup.LayoutParams(Config.XWIDTH,Config.XHEIGHT));
        //设置背景颜色
        gamePanel.setBackgroundColor(Config.GAME_RBG);
        // 4·添加进父容器
        layoutGame.addView(gamePanel);

        //实例化信息区域,设置间距
        LinearLayout layoutInfo = findViewById(R.id.layoutInfo);
        layoutInfo.setPadding(Config.PADDING,Config.PADDING,Config.PADDING,Config.PADDING);
        layoutInfo.setBackgroundColor(Config.INFO_RBG);
        //实例化下一块预览区域
        nextPanel = new View(this){
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                gameControl.drawNext(canvas,nextPanel.getWidth());
            }
        };
        nextPanel.setLayoutParams(new ViewGroup.LayoutParams(-1,200));
        nextPanel.setBackgroundColor(Config.NEXT_RBG);

        FrameLayout layoutNext = findViewById(R.id.layoutNext);
        layoutNext.addView(nextPanel);

        scoreTextView = findViewById(R.id.textCurrentScore);
        maxTextView = findViewById(R.id.textMaxScore);
    }

    //初始化监听
    public void initListener(){
        findViewById(R.id.btnLeft).setOnClickListener(this);
        findViewById(R.id.btnUp).setOnClickListener(this);
        findViewById(R.id.btnRight).setOnClickListener(this);
        findViewById(R.id.btnDown).setOnClickListener(this);
        findViewById(R.id.btnStart).setOnClickListener(this);
        findViewById(R.id.btnPause).setOnClickListener(this);
        findViewById(R.id.btnAuxiliaryLine).setOnClickListener(this);
    }

    //捕捉点击事件
    @Override
    public void onClick(View v){
        gameControl.onClick(v.getId(),this);
        //调用重绘view
        gamePanel.invalidate();
        nextPanel.invalidate();
    }

}
