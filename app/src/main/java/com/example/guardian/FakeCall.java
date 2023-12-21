package com.example.guardian;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class FakeCall extends AppCompatActivity {

    ImageButton declineBut, answerBut;
    Ringtone r;
    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_fake_call);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.black2));

        declineBut = findViewById(R.id.decline);
        answerBut = findViewById(R.id.answer);

        // Make a Sound
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            r = RingtoneManager.getRingtone(getApplicationContext(),
                    notification);
            r.play();
        } catch (Exception e) {
            // Some error playing sound
        }

        // Get instance of Vibrator from current Context
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Start without a delay
        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {0, 200, 200, 600, 200, 2000, 200, 1000, 100, 300, 400, 600, 800, 100,
                200, 200, 600, 200, 2000, 200, 1000, 100, 300, 400, 600, 800, 100,
                200, 200, 600, 200, 2000, 200, 1000, 100, 300, 400, 600, 800, 100,
                200, 200, 600, 200, 2000, 200, 1000, 100, 300, 400, 600, 800, 100,
                200, 200, 600, 200, 2000, 200, 1000, 100, 300, 400, 600, 800, 100,
                200, 200, 600, 200, 2000, 200, 1000, 100, 300, 400, 600, 800, 100};

        // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
        v.vibrate(pattern, -1);

        declineBut.setOnClickListener(view -> crashActivity());

        answerBut.setOnClickListener(view -> answerActivity());
    }

    private void answerActivity() {
        Intent i = new Intent(this, FakeCallAnswer.class);
        r.stop();
        v.cancel();
        finish();
        startActivity(i);
    }

    private void crashActivity() {
        Intent i = new Intent(this, MainActivity.class);
        r.stop();
        v.cancel();
        finish();
        startActivity(i);
    }
}