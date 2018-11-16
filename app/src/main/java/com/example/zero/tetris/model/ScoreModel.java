package com.example.zero.tetris.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.zero.tetris.Config;
import com.example.zero.tetris.util.SocketThread;

/**
 * 分数模型
 */
public class ScoreModel {
    //当前分数
    public int score;
    //最高分数
    public int maxScore;
    //加分锁
    public boolean isLocked1;
    //第二重加分锁
    public boolean isLocked2;

    private Context context;


    public ScoreModel(Context context){
        this.context = context;
        SharedPreferences pref = context.getSharedPreferences("data",0);
        maxScore = pref.getInt("maxScore",0);
    }

    //加分规则
    public void addScore(int lines) {
        if (lines > 0) {
            score += lines * 2 - 1;
        }
        if (score > 10 && !isLocked1){
            Config.level++;
            isLocked1 = true;
        }
        if(score > 20 && !isLocked2){
            Config.level++;
            isLocked2 = true;
        }
    }

    //更新最高分数
    public void updateMaxScore(Context context){

        if(score > maxScore){
            maxScore = score;

            SharedPreferences.Editor editor = context.getSharedPreferences("data",0).edit();
            editor.putInt("maxScore",maxScore);
            editor.apply();
        }
    }

    /** 显示当前分数 */
    public void showCurrentScore(TextView textView){
        textView.setText(score + "");
    }

    /** 显示最高分数 */
    public void showMaxScore(TextView textView) {
        textView.setText(maxScore + "");
    }
}
