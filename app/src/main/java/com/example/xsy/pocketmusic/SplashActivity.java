package com.example.xsy.pocketmusic;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.xsy.pocketmusic.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //启动服务
        startService(new Intent(this,PlayService.class));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
                finish();
            }
        },3000);
    }
    private void startMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

    }

    private Handler handler = new Handler();
}
