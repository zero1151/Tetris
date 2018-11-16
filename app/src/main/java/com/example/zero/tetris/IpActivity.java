package com.example.zero.tetris;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.Socket;

public class IpActivity extends AppCompatActivity {

    EditText ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);

        ipAddress = findViewById(R.id.ipAddress);
        Button okButton = findViewById(R.id.okButton);

        //连接成功则传来信息关闭此活动
        Intent closeIntent = getIntent();
        String status = closeIntent.getStringExtra("status");
        if(status != null && status.equals("ok")){
            finish();
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IpActivity.this,MainActivity.class);
                intent.putExtra("ip",ipAddress.getText().toString());
                startActivity(intent);
                Log.d("deep", "传递ip" + ipAddress.getText().toString());

            }
        });
    }

}
