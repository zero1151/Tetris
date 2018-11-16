package com.example.zero.tetris;

public class Config {

    //地图X方向格子数
    public static final int MAPX = 10;
    //地图Y方向格子数
    public static final int MAPY = 20;

    //地图X方向宽度
    public static int XWIDTH;
    //地图Y方向高度
    public static int XHEIGHT;

    //间距
    public static int PADDING;
    //间距分母
    public static final int PADDING_SPLIT = 40;


    //难度
    public static int level;

    //游戏面板颜色
    public static final int GAME_RBG = 0x10000000;
    //信息面板颜色
    public static final int INFO_RBG = 0x10000000;
    //辅助线颜色
    public static final int AUXILIARY_RBG = 0xff666666;//灰色
    //下落方块颜色
    public static final int BOXS_RBG = 0xff000000;
    //地图方块颜色
    public static final int MAP_RBG = 0x50000000;
    //状态颜色
    public static final int STATE_RBG = 0xffff0000;//红色
    //下一块预览背景颜色
    public static final int NEXT_RBG = 0x20000000;

}
