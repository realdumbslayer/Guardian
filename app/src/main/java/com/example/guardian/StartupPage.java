package com.example.guardian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartupPage extends AppCompatActivity {
    TextView skiptv;
    Button startbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.startup_page);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.header));

        skiptv = findViewById(R.id.skip);
        skiptv.setOnClickListener(view -> skipTutorial());

        startbtn = findViewById(R.id.startbtn);
        startbtn.setOnClickListener((view -> goToSlide()));


    }

    private void skipTutorial() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    private void goToSlide(){
        Intent intent = new Intent(this,FragmentsActivity.class);
        finish();
        startActivity(intent);
    }
}


