package com.example.guardian;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guardian.Contacts.CustomAdapter;
import com.example.guardian.Contacts.Database;
import com.example.guardian.Contacts.Template;
import com.example.guardian.Shake.Reactivate;
import com.example.guardian.Shake.Sensor;

import java.util.List;

public class ContactsFragment extends AppCompatActivity {

    private static final int IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1002;
    private static final int PICK_CONTACT = 1;

    // create instances of various classes to be used
    Button button1, button2;
    ListView listView;
    Database db;
    List<Template> list;
    CustomAdapter customAdapter;
    Template c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_contacts_fragment);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.header));

        // start the service
        Sensor sensorService = new Sensor();
        Intent intent = new Intent(this, sensorService.getClass());
        if (!isMyServiceRunning(sensorService.getClass())) {
            startService(intent);
        }

        button1 = findViewById(R.id.Button1);
        listView = findViewById(R.id.ListView);
        db = new Database(this);
        list = db.getAllContacts();
        customAdapter = new CustomAdapter(this, list);
        listView.setAdapter(customAdapter);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling of getContacts()
                if (db.count() != 5) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                } else {
                    Toast.makeText(ContactsFragment.this, "Can't Add more than 5 Contacts", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // method to check if the service is running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }

    @Override
    protected void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Reactivate.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }


    private void callNum(View v) {
        String s = c.getPhoneNo();
        s= "tel:" +s;
        Uri u = Uri.parse(s);
        Intent intent = new Intent(Intent.ACTION_DIAL, u);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permissions Denied!\n Can't use the App!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get the contact from the PhoneBook of device
        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {

                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        @SuppressLint("Range") String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String phone = null;
                        try {
                            if (hasPhone.equalsIgnoreCase("1")) {
                                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                                phones.moveToFirst();
                                phone = phones.getString(phones.getColumnIndex("data1"));
                            }
                            @SuppressLint("Range") String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            db.addcontact(new Template(0, name, phone));
                            list = db.getAllContacts();
                            customAdapter.refresh(list);
                        } catch (Exception ex) {
                        }
                    }
                }
                break;
        }
    }

}