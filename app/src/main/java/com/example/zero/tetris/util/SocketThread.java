package com.example.zero.tetris.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.zero.tetris.Config;
import com.example.zero.tetris.control.GameControl;
import com.example.zero.tetris.model.MapsModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;

//还差“写”方法
public class SocketThread extends Thread {

    private String ip;
    private Handler handler;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Context context;
    public Handler revHandler;
    private GameControl gameControl;

    public SocketThread(String ip, Handler handler, Context context,GameControl gameControl ) {
        this.gameControl = gameControl;
        this.context = context;
        this.ip = ip;
        this.handler = handler;
//        gameControl = new GameControl(handler,context);//错，这里new一个gamecontrol，就和原来的不一样了

    }

    static class RevHandler extends Handler{

        private SocketThread socketThread;

        private RevHandler(SocketThread socketThread){
            this.socketThread = socketThread;
        }
        @Override
        public void handleMessage(Message msg) {
            //接收到已销一行的消息
            if(msg.what == 123){
                try {
                    socketThread.writer.write("1" + "\n");
                    socketThread.writer.flush();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        connect();
    }

    //先尝试一种情况
    private void connect() {
        try {
            Socket socket = new Socket(ip, 12345);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Log.d("deep", "socket线程创建成功");
            //new一个线程单独读取，因为会阻塞
            new Thread(){
                @Override
                public void run() {
                    String line = null;
                    try {
                        while ((line = reader.readLine()) != null) {
                            if (line.equals("1")) {
                                Log.d("deep", "收到1");
                                gameControl.mapsModel.addLine();
                                Message message = new Message();
                                message.obj = "invalidate";
                                handler.sendMessage(message);
                            }
                            if (line.equals("close")) {
                                break;
                            }
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }.start();

            //为当前线程初始化Looper
            Looper.prepare();
            //创建handler对象负责写
            revHandler = new RevHandler(this);
            Looper.loop();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
