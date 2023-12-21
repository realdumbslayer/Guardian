package com.example.guardian;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guardian.Contacts.Database;
import com.example.guardian.Contacts.Template;
import com.example.guardian.Menu.DrawerAdapter;
import com.example.guardian.Menu.DrawerItem;
import com.example.guardian.Menu.SimpleItem;
import com.example.guardian.Menu.SpaceItem;
import com.example.library.SlidingRootNav;
import com.example.library.SlidingRootNavBuilder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback, DrawerAdapter.OnItemSelectedListener {

    private static final int POS_DASHBOARD = 0;
    private static final int POS_CONTACTS = 1;
    private static final int POS_FAKE_CALL = 2;
    private static final int POS_PRIVACY_POLICY = 3;
    private static final int POS_SHARE = 4;
    private static final int POS_LOGOUT = 6;

    private String[] screenTitles;
    private Drawable[] screenIcons;
    private SlidingRootNav slidingRootNav;
    private NfcAdapter mNfcAdapter;
    private static final int IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1002;

    Button btn1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(view -> goToTutorial());

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // check for runtime permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS}, 100);
            }
        }

        // this is a special permission required only by devices using
        // Android Q and above. The Access Background Permission is responsible
        // for populating the dialog with "ALLOW ALL THE TIME" option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 100);
        }

        // check for BatteryOptimization,
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                askIgnoreOptimization();
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.header));
        }

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_CONTACTS),
                createItemFor(POS_FAKE_CALL),
                createItemFor(POS_PRIVACY_POLICY),
                createItemFor(POS_SHARE),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        adapter.setSelected(POS_DASHBOARD);
    }

    public void share (View v){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody =  "http://play.google.com/store/apps/detail?id=" + getPackageName();
        String shareSub = "Try now";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    public void policy (View v){
        Intent browserIntent  = new Intent(Intent.ACTION_VIEW , Uri.parse("https://www.termsfeed.com/live/cf3fb504-3a8c-43ad-b083-90001733dc5f"));
        startActivity(browserIntent);
    }

    public void email (View v){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto" , "guardian@email.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "mail body.");
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

    public void howToUse(View v){
        Intent tutorialIntent = new Intent(this, HowToUseActivity.class);
        startActivity(tutorialIntent);
    }

    public void faq(View v){
        Intent faqIntent = new Intent(this, FAQ.class);
        startActivity(faqIntent);
    }

    public void tipsandhelp(View v){
        Intent tipsIntent = new Intent (this, TipsAndHelp.class);
        startActivity(tipsIntent);
    }

    public void goToTutorial(){
        Intent intent = new Intent(this, Tutorial.class);
        finish();
        startActivity(intent);

    }

    @Override
    public void onItemSelected(int position) {
        if (position == POS_LOGOUT) {
            System.exit(0);
            finish();
        }
        slidingRootNav.closeMenu();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(position == POS_CONTACTS){
            Intent intent = new Intent(this, ContactsFragment.class);
            startActivity(intent);
        }

        if(position == POS_FAKE_CALL){
            Intent intent = new Intent(this, FakeCall.class);
            startActivity(intent);
        }


        if (position == POS_SHARE){
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody =  "http://play.google.com/store/apps/detail?id=" + getPackageName();
            String shareSub = "Try now";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }

        if(position == POS_PRIVACY_POLICY){
            Intent browserIntent  = new Intent(Intent.ACTION_VIEW , Uri.parse("https://www.termsfeed.com/live/cf3fb504-3a8c-43ad-b083-90001733dc5f"));
            startActivity(browserIntent);
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @SuppressWarnings("rawtypes")
    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mNfcAdapter != null) {
            Bundle options = new Bundle();
            // Work around for some broken Nfc firmware implementations that poll the card too fast
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);

            // Enable ReaderMode for all types of card and disable platform sounds
            mNfcAdapter.enableReaderMode(this,
                    this,
                    NfcAdapter.FLAG_READER_NFC_A |
                            NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F |
                            NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_NFC_BARCODE |
                            NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS, options);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableReaderMode(this);
    }

    // This method is run in another thread when a card is discovered
    // !!!! This method cannot cannot direct interact with the UI Thread
    // Use `runOnUiThread` method to change the UI from this method

    public void onTagDiscovered(Tag tag) {

        // Read and or write to Tag here to the appropriate Tag Technology type class
        // in this example the card should be an Ndef Technology Type
        Ndef mNdef = Ndef.get(tag);

        // Check that it is an Ndef capable card
        if (mNdef != null) {

            // If we want to read
            // As we did not turn on the NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
            // We can get the cached Ndef message the system read for us.

            // create FusedLocationProviderClient to get the user location
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

            // use the PRIORITY_BALANCED_POWER_ACCURACY (for optimised power;it will only use GPS at this very moment)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
                return;
            }
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, new CancellationToken() {
                @Override
                public boolean isCancellationRequested() {
                    return false;
                }

                @NonNull
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                    return null;
                }
            }).addOnSuccessListener(location -> {
                // check if location is null, We'll create different messages for both cases

                if (location != null) {

                    // get the SMSManager
                    SmsManager smsManager = SmsManager.getDefault();

                    // get the list of all the contacts in Database
                    Database db = new Database(MainActivity.this);
                    List<Template> list = db.getAllContacts();

                    // send SMS to each contact
                    for (Template c : list) {
                        String message = "Hey, " + c.getName() + " I am in DANGER, I need help. Please urgently reach me out. Here are my coordinates.\n " + "http://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                        smsManager.sendTextMessage(c.getPhoneNo(), null, message, null, null);

                        NdefMessage mNdefMessage = mNdef.getCachedNdefMessage();

                        // Or if we want to write a Ndef message
                        // Create a Ndef Record
                        //  NdefRecord mRecord = NdefRecord.createTextRecord("en","English String");
                        String smsUri = "sms:" + c.getPhoneNo() + "?body" + message;
                        NdefRecord smsRecord = NdefRecord.createUri(smsUri);

                        // Add to a NdefMessage
                        NdefMessage mMsg = new NdefMessage(smsRecord);

                        // Catch errors
                        try {
                            mNdef.connect();
                            mNdef.writeNdefMessage(mMsg);

                            // Success if got to here
                            runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(),
                                        "Write to NFC Success",
                                        Toast.LENGTH_SHORT).show();
                            });

                            // Make a Sound
                            try {
                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                                        notification);
                                r.play();
                            } catch (Exception e) {
                                // Some error playing sound
                            }

                        } catch (FormatException e) {
                            // if the NDEF Message to write is malformed
                        } catch (TagLostException e) {
                            // Tag went out of range before operations were complete
                        } catch (IOException e) {
                            // if there is an I/O failure, or the operation is cancelled
                        } finally {
                            // Be nice and try and close the tag to
                            // Disable I/O operations to the tag from this TagTechnology object, and release resources.
                            try {
                                mNdef.close();
                            } catch (IOException e) {
                                // if there is an I/O failure, or the operation is cancelled
                            }
                        }

                    }

                }
            });
        }
    }

    // this method prompts the user to remove any
    // battery optimisation constraints from the App
    private void askIgnoreOptimization() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, IGNORE_BATTERY_OPTIMIZATION_REQUEST);
        }

    }
}
