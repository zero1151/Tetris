package com.example.zero.tetris.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.example.zero.tetris.Config;
import com.example.zero.tetris.R;

import java.util.HashSet;
import java.util.Random;

public class MapsModel {

    //全局
    private Context context;
    //地图
    public boolean[][] maps;
    //地图宽度
    private int xWidth;
    //地图高度
    private int xHeight;
    //地图画笔
    private Paint mapPaint;
    //辅助线画笔
    private Paint linePaint;
    //状态画笔
    private Paint statusPaint;
    //方块大小
    private int boxSize;

    //获取图片
    private Bitmap bitmap;
    private Rect mSrcRect;


    public MapsModel(int boxSize,int xWidth,int xHeight,Context context) {
        this.boxSize = boxSize;
        this.xWidth = xWidth;
        this.xHeight = xHeight;
        this.context = context;
        //初始化地图数据
        maps = new boolean[Config.MAPX][Config.MAPY];
        //地图画笔
        mapPaint = new Paint();
//        mapPaint.setColor(Config.MAP_RBG);
        mapPaint.setAntiAlias(true);

        //辅助线画笔
        linePaint = new Paint();
        linePaint.setColor(Config.AUXILIARY_RBG);
        linePaint.setAntiAlias(true);

        //状态画笔
        statusPaint = new Paint();
        statusPaint.setColor(Config.STATE_RBG);
        statusPaint.setTextSize(100);
        statusPaint.setAntiAlias(true);//抗锯齿

        //获取图片
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.box_grey);
        mSrcRect = new Rect(0, 0,bitmap.getWidth(),bitmap.getHeight());
    }

    public void drawStatus(Canvas canvas,boolean isPause,boolean isOver){
        //游戏结束
        if(isOver){
            canvas.drawText("游戏结束",xWidth/2 - statusPaint.measureText("游戏结束")/2,xHeight/2,statusPaint);
        }
        //画暂停状态
        if(isPause&&!isOver){
            canvas.drawText("暂停",xWidth/2 - statusPaint.measureText("暂停")/2,xHeight/2,statusPaint);
        }

    }

    /** 加行，一行空3个 */
    public void addLine(){
        Log.d("deep", "addLine: ");
        HashSet<Integer> hashSet = new HashSet<>();
        Random random = new Random();
        while (hashSet.size() < 7){
            hashSet.add(random.nextInt(10));
        }
        for (int i = 0; i < maps.length; i++) {
            for (int j = 0; j < maps[0].length - 1; j++) {//到倒数第二行
                maps[i][j] = maps[i][j+1];
            }
        }
        for (int i = 0; i < maps.length; i++) {
            if(hashSet.contains(i)){
                maps[i][Config.MAPY - 1] = true;
            }else {
                maps[i][Config.MAPY - 1] = false;
            }
        }
    }

    /** 地图绘制 */
    public void drawMaps(Canvas canvas) {

        for (int x = 0; x < maps.length; x++) {
            for (int y = 0; y < maps[0].length; y++) {
                if (maps[x][y]) {
                    Rect mDestRect = new Rect(x * boxSize, y * boxSize, x * boxSize + boxSize, y * boxSize + boxSize);
                    //简约绘制方法
//                    canvas.drawRect(x * boxSize, y * boxSize,
//                            x * boxSize + boxSize,y * boxSize +boxSize,mapPaint);
                    canvas.drawBitmap(bitmap, mSrcRect, mDestRect, mapPaint);
                }
            }
        }

    }

    public void cleanMaps(){
        for (int x = 0; x < maps.length; x++) {
            for (int y = 0; y < maps[0].length; y++) {
                maps[x][y] = false;
            }
        }
    }

    /** 地图辅助线 */
    public void drawLines(Canvas canvas,boolean isOpen){
        if (!isOpen)return;
         else if(isOpen) {
            for (int x = 0; x < maps.length; x++) {
                canvas.drawLine(x * boxSize, 0, x * boxSize, xHeight, linePaint);
            }
            for (int y = 0; y < maps[0].length; y++) {
                canvas.drawLine(0, y * boxSize, xWidth, y * boxSize, linePaint);
            }
        }
    }

    /** 消行处理 */
    public int cleanLine(){
        int lines = 0;
        for (int y = maps[0].length - 1; y > 0; y--) {
            if (checkLine(y)) {
                //执行消行
                deleteLine(y);
                //从消掉的那一行继续，防止连续2行却只消一行
                y++;
                lines++;
            }
        }
        return lines;
    }
    /** 消行判断 */
    private boolean checkLine(int y){
        for (boolean[] map : maps) {
            //如果有一个不为true则不消行
            if (!map[y])
                return false;
        }
        return true;
        /*for (int x = 0; x < maps.length; x++) {
            //如果有一个不为true则不消行
            if(!maps[x][y])
                return false;
        }
        return true;*/
    }

    /** 执行消行 */
    private void deleteLine(int dy){
        for (int y = dy; y > 0; y--) {
            for (int x = 0; x < maps.length; x++) {
                maps[x][y] = maps[x][y-1];
            }
        }
    }



}
