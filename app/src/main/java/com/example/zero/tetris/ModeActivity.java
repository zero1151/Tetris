package com.example.zero.tetris;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ModeActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        Button singleMode = findViewById(R.id.single_mode);
        Button doubleMode = findViewById(R.id.double_mode);

        singleMode.setOnClickListener(this);
        doubleMode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.single_mode:
                Intent singleIntent = new Intent(ModeActivity.this,MainActivity.class);
                startActivity(singleIntent);
                break;
            case R.id.double_mode:
                Intent doubleIntent = new Intent(ModeActivity.this,IpActivity.class);
                startActivity(doubleIntent);
                break;
            default:
                break;
        }
    }
}
