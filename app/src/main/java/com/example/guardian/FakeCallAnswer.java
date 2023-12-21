package com.example.guardian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class FakeCallAnswer extends AppCompatActivity {

    ImageButton endCallBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_fake_call_answer);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.black2));

        endCallBut = findViewById(R.id.endCall);
        endCallBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeActivity();
            }
        });

    }
    private void closeActivity() {
        Intent i = new Intent(this, MainActivity.class);
        finish();
        startActivity(i);
    }
}