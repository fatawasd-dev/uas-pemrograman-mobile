package com.example.utsmobile_fahmiagungtajulabidin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {
    private Handler handler;
    private int dotCount = 0;
    private TextView loadingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_screen_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingTextView = findViewById(R.id.loading_text);
        handler = new Handler();
        startLoadingAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, NewsPortalDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    private void startLoadingAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (dotCount) {
                    case 0:
                        loadingTextView.setText("Loading.");
                        break;
                    case 1:
                        loadingTextView.setText("Loading..");
                        break;
                    case 2:
                        loadingTextView.setText("Loading...");
                        break;
                }

                dotCount = (dotCount + 1) % 3;
                startLoadingAnimation();
            }
        }, 1000);
    }
}