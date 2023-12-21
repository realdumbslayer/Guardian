package com.example.guardian.Shake;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class Reactivate extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Check: ","Receiver Started");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, Sensor.class));
        } else {
            context.startService(new Intent(context, Sensor.class));
        }
    }
}

