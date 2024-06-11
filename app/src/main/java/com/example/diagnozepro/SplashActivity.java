package com.example.diagnozepro;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.diagnozepro.auth.LoginActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // Sleep for 3000 milliseconds (3 seconds)
                    sleep(3000);
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    // Handle any interruption exceptions and show a toast message
                    Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        thread.start();

    }
}