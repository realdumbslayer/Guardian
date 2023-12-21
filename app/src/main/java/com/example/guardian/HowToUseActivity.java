package com.example.guardian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class HowToUseActivity extends AppCompatActivity {
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setTheme(R.style.AppTheme);
            setContentView(R.layout.activity_how_to_use);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.header));
        }

        public void goBack(View view) {
            Intent intent = new Intent(this, MainActivity.class);
            finish();
            startActivity(intent);
        }
    }