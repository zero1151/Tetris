package com.example.zero.tetris.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.example.zero.tetris.Config;
import com.example.zero.tetris.MainActivity;
import com.example.zero.tetris.R;

import java.util.Random;

/**
 * 方块模型
 */
public class BoxsModel {
    //方块
    public Point[] boxs = new Point[]{};
    //方块大小
    public int boxSize;
    //方块类型
    private int boxType;
    //方块画笔
    Paint boxPaint;

    //下一块方块
    public Point[] nextBox = new Point[]{};
    //下一块方块类型
    private int nextBoxType;
    //下一块方块格子大小
    private int nextBoxSize;

    //获取图片
    private Bitmap bitmap;
    private Rect mSrcRect;

    public BoxsModel(int boxSize,Context context){
        this.boxSize = boxSize;
        boxPaint = new Paint();
//        boxPaint.setColor(Config.BOXS_RBG);
        boxPaint.setAntiAlias(true);

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.box);
        mSrcRect = new Rect(0, 0,bitmap.getWidth(),bitmap.getHeight());
    }

    public void newNext(){
        //随机数生成一个新的方块
        Random random = new Random();
        nextBoxType = random.nextInt(7);
        //生成下一个方块
        switch (nextBoxType){
            case 0:
                //田
                nextBox = new Point[]{new Point(4,0),new Point(5,0),new Point(4,1),new Point(5,1)};
                break;
            case 1:
                //L
                nextBox = new Point[]{new Point(4,1),new Point(5,0),new Point(3,1),new Point(5,1)};
                break;
            case 2:
                //反L
                nextBox = new Point[]{new Point(4,1),new Point(3,0),new Point(3,1),new Point(5,1)};
                break;
            case 3:
                //Z
                nextBox = new Point[]{new Point(4,0),new Point(3,0),new Point(4,1),new Point(5,1)};
                break;
            case 4:
                //反Z
                nextBox = new Point[]{new Point(4,0),new Point(5,0),new Point(4,1),new Point(3,1)};
                break;
            case 5:
                //T
                nextBox = new Point[]{new Point(4,0),new Point(3,0),new Point(5,0),new Point(4,1)};
                break;
            case 6:
                //I
                nextBox = new Point[]{new Point(4,0),new Point(3,0),new Point(5,0),new Point(6,0)};
                break;
            default:
                break;
        }
    }

    //新的方块建模
    public void newBoxs(){
        if (nextBox == null)  newNext();

        //当前方块 = 下一块
        boxs = nextBox;
        //当前方块类型 = 下一块类型
        boxType = nextBoxType;

        newNext();
    }
    /**移动方块*/
    public boolean move(int x,int y,MapsModel mapsModel){

        for(int i=0; i<boxs.length;i++){
            //把方块预移动的点传入边界判断
            if(checkBoundary(boxs[i].x + x,boxs[i].y + y,mapsModel)){
                return false;
            }
        }
        //遍历方块数组,每个都加上偏移的量
        for(int i=0; i<boxs.length;i++){
            boxs[i].x += x;
            boxs[i].y += y;
        }
        return true;
    }


    /** 旋转方块 */
    public boolean rotate(MapsModel mapsModel){
        //田字形方块不能旋转
        if(boxType == 0)return false;
        for(int i = 0; i<boxs.length;i++){
            int checkX = -boxs[i].y + boxs[0].y + boxs[0].x;
            int checkY = boxs[i].x - boxs[0].x + boxs[0].y;
            //检查转后是否出界
            if (checkBoundary(checkX,checkY,mapsModel)){
                return false;
            };
        }
        //遍历方块数组，每个都绕着中心旋转90度(绕第一个方块旋转）
        for(int i = 0; i<boxs.length;i++){
            //旋转算法（笛卡尔公式）
            int checkX = -boxs[i].y + boxs[0].y + boxs[0].x;
            int checkY = boxs[i].x - boxs[0].x + boxs[0].y;
            boxs[i].x = checkX;
            boxs[i].y = checkY;
        }
        return true;
    }

    /**
     * 边界判断 , 包括地图判断
     * 传入x，y 判断是否在边界外
     * @param x
     * @param y
     * @return true代表出界
     */
    public boolean checkBoundary(int x,int y,MapsModel mapsModel){
        return (x<0||y<0||x>=mapsModel.maps.length||y>=mapsModel.maps[0].length||mapsModel.maps[x][y]);
    }

    //绘制方块
    public void drawBoxs(Canvas canvas){
        if(boxs != null) {
            for (int i = 0; i < boxs.length; i++) {
//                简约线条方法
//                canvas.drawRect(boxs[i].x * boxSize, boxs[i].y * boxSize,
//                        boxs[i].x * boxSize + boxSize, boxs[i].y * boxSize + boxSize, boxPaint);
                Rect mDestRect = new Rect(boxs[i].x * boxSize, boxs[i].y * boxSize,
                        boxs[i].x * boxSize + boxSize, boxs[i].y * boxSize + boxSize);
                canvas.drawBitmap(bitmap,mSrcRect,mDestRect,boxPaint);
            }
        }
    }

    /** 绘制下一个方块 */
    public void drawNext(Canvas canvas, int width) {
        if(nextBoxSize == 0)nextBoxSize = width/6;

        for(int i = 0;i < nextBox.length; i++){
//            简约线条的方法
//            canvas.drawRect((nextBox[i].x-2) * nextBoxSize,(nextBox[i].y + 2) * nextBoxSize,
//                    (nextBox[i].x-2) * nextBoxSize + nextBoxSize,(nextBox[i].y + 2) * nextBoxSize + nextBoxSize,boxPaint);

            Rect mDestRect = new Rect((nextBox[i].x-2) * nextBoxSize,(nextBox[i].y + 2) * nextBoxSize,(nextBox[i].x-2) * nextBoxSize + nextBoxSize,(nextBox[i].y + 2) * nextBoxSize + nextBoxSize);
//            canvas.drawBitmap(bitmap,(nextBox[i].x-2) * nextBoxSize,(nextBox[i].y + 2) * nextBoxSize,nextBoxSize,nextBoxSize,boxPaint);
            canvas.drawBitmap(bitmap,mSrcRect,mDestRect,boxPaint);

        }
    }
}
