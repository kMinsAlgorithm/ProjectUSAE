package com.example.usae;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//처음 실행대기 화면에서 실행 가능 상태 화면으로 넘어가기 위해 작성한 코드.
public class MainActivity extends AppCompatActivity {

    private static int DELAY_TIME = 3000; // 3초 이 코드는 로봇과 휴대폰이 연결되는 그 사이 기간을 표현하기 위해서 작성해 둔건데 나중에 보고서 쓸때는 포함안키시는게 좋을듯

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 3초 후에 SecondActivity로 이동하는 Intent를 생성합니다.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "USAE 연결중", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
                finish();
            }
        }, DELAY_TIME);
    }
}
