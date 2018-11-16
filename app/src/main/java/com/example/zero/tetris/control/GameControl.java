package com.example.zero.tetris.control;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.zero.tetris.Config;
import com.example.zero.tetris.R;
import com.example.zero.tetris.model.BoxsModel;
import com.example.zero.tetris.model.MapsModel;
import com.example.zero.tetris.model.ScoreModel;
import com.example.zero.tetris.util.SocketThread;


/**
 * 游戏控制器
 */
public class GameControl {

    private Handler handler;
    private Context context;
    //地图模型
    public MapsModel mapsModel;
    //方块模型
    private BoxsModel boxsModel;
    //分数模型
    public ScoreModel scoreModel;

    //重新开始状态
    private boolean reset;
    //暂停状态
    private boolean isPause;
    //结束状态
    private boolean isOver;
    //辅助线开关
    private boolean isOpen;

    private String ip;

    //自动下落线程
    private Thread downThread;

    //socket线程
    private SocketThread socketThread;

    public GameControl(String ip,Handler handler,Context context ) {
        this.handler = handler;
        this.context = context;
        this.ip = ip;
        initData(context);
    }


    /**初始化数据*/
    private void initData(Context context){
        //获得屏幕宽度
        int width = getScreenWidth(context);
        //设置游戏屏幕宽度为2/3
        Config.XWIDTH = width * 2/3 ;
        //游戏区域高度为宽度的2倍
        Config.XHEIGHT = Config.XWIDTH * 2;
        //间距 = 屏幕宽度/20
        Config.PADDING = width/Config.PADDING_SPLIT;

        //初始化方块大小 = 游戏区域宽度/10
        int boxSize = Config.XWIDTH/ Config.MAPX;
        //实例化地图模型
        mapsModel = new MapsModel(boxSize,Config.XWIDTH,Config.XHEIGHT,context);
        //实例化方块模型
        boxsModel = new BoxsModel(boxSize,context);
        //实例化分数模型
        scoreModel = new ScoreModel(context);

        socketThread = new SocketThread(ip,handler,context,this);
        socketThread.start();

    }
    /**游戏开始 */
    private void startGame(){
        isOver = false;
        isPause = false;
        reset = true;
        scoreModel.score = 0;
        //重置难度以及释放加分锁
        Config.level = 1;
        scoreModel.isLocked1 = false;
        scoreModel.isLocked2 = false;
        //通知主线程刷新view
        Message msg = new Message();
        msg.obj = "invalidate";
        handler.sendMessage(msg);
        //第一步是清除地图
        mapsModel.cleanMaps();

        if(downThread == null) {
            boxsModel.newBoxs(); //若线程为空则执行这个，才会有BOX，否则需要点2下Start按钮
            downThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    int time = 0;
                    while (true) {
                        try {
                            //休眠500毫秒
                            sleep(600 - Config.level * 100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //判断游戏是否结束
                        //游戏结束，重新计时
                        if(isOver)time = 0;

                        //执行下落之前判断是否处于暂停状态
                        if (isPause || isOver)
                            //继续循环
                            continue;
                        //执行一次下落
                        time +=  600 - Config.level * 100;
                        moveBottom();

                        if(time > 2000) {
                            mapsModel.addLine();
                            time = 0;
                        }
                        //通知主线程刷新view
                        Message msg = new Message();
                        msg.obj = "invalidate";
                        handler.sendMessage(msg);
                    }
                }
            };
            downThread.start();
        }
        //生成新的方块
        boxsModel.newBoxs();
    }

    /**下落 */
    public boolean moveBottom(){
        //1·移动成功 不做处理
        if(boxsModel.move(0,1,mapsModel))
            return true;
        //2·移动失败  堆积处理
        for(int i = 0; i < boxsModel.boxs.length; i++) mapsModel.maps[boxsModel.boxs[i].x][boxsModel.boxs[i].y] = true;
        //2·5 消行处理
        int lines = mapsModel.cleanLine();
        if(lines > 0){
            //给对手加行
            Message message = new Message();
            message.what = 123;
            socketThread.revHandler.sendMessage(message);

        }
        //加分
        scoreModel.addScore(lines);
        //更新最高分
        scoreModel.updateMaxScore(context);
        //3·生成新的方块
        boxsModel.newBoxs();
        //4·游戏结束判断
        isOver = checkOver();
        //通知主线程刷新view
        Message msg = new Message();
        msg.obj = "invalidate";
        handler.sendMessage(msg);
        return false;
    }

    /**结束判断 */
    public boolean checkOver(){
        for (int i = 0; i < boxsModel.boxs.length; i++) {
            if(mapsModel.maps[boxsModel.boxs[i].x][boxsModel.boxs[i].y])
                return true;
        }
        return false;
    }


    /** 暂停 */
    public void setPause(){
        isPause = (isPause?false:true);
        Message msg = new Message();
        msg.obj = "pause";
        msg.arg1 = (isPause?0:1);//0代表已经暂停，1代表未暂停
        handler.sendMessage(msg);
    }

    /** 设置辅助线开关 */
    private void setAuxiliaryLine() {
        isOpen = (isOpen?false:true);
    }

    /*获取屏幕宽度*/
    public static int getScreenWidth(Context context){
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }


    public void onClick(int id,Context context) {
        switch (id){
            //left
            case R.id.btnLeft:
                if(isPause)return;
                boxsModel.move(-1,0,mapsModel);
                break;
            //up
            case R.id.btnUp:
                if(isPause)return;
                boxsModel.rotate(mapsModel);
                break;
            //right
            case R.id.btnRight:
                if(isPause)return;
                boxsModel.move(1,0,mapsModel);
                break;
            //down
            case R.id.btnDown:
                if(isPause)return;
                if(isOver)return;
                while (true)
                    if(!moveBottom())break;
                break;
            //start
            case R.id.btnStart:
                startGame();
                reset = false;
                break;
            //pause
            case R.id.btnPause:
                setPause();
                break;
            case R.id.btnAuxiliaryLine:
                setAuxiliaryLine();
                break;
            default:
                break;
        }
    }


    /** 绘制游戏区域 */
    public void drawGame(Canvas canvas){
        //绘制地图
        mapsModel.drawMaps(canvas);
        //绘制方块
        boxsModel.drawBoxs(canvas);
        //绘制地图辅助线
        mapsModel.drawLines(canvas,isOpen);
        //绘制游戏状态
        mapsModel.drawStatus(canvas,isPause,isOver);
    }

    /** 绘制下一块预览区域 */
    public void drawNext(Canvas canvas,int width) {
        boxsModel.drawNext(canvas,width);
    }
}
