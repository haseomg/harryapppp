package com.example.goldentoads;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class Load extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        startLoading();
    }

    private void startLoading() {

            //누군가대신 일을 처리해줄 필요가 있을때
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run(){
                    Intent intent = new Intent(getBaseContext(), LogInActivity.class);
                    startActivity(intent);
                    finish();
                }
            },2000);

        }
        }

