package com.example.myhelper66;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.simform.custombottomnavigation.SSCustomBottomNavigation;

import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class MainActivity extends AppCompatActivity { ;
    private Context mContext;
    private Activity mActivity;

    private static final String SMS_RECEVIED = "android.provider.Telephony.SMS_RECEIVED";

    private String password = "";
    private double latitude ;
    private double longitude ;

    //Initialize toolBar
    private SSCustomBottomNavigation bottomNavigation;

    private FusedLocationProviderClient fusedLocationClient;

    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String SHARED_PASSWORD = "Password";




    public MainActivity() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the application context
        mContext = getApplicationContext();
        mActivity = MainActivity.this;


        // get user password
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        password = sharedPreferences.getString(SHARED_PASSWORD, "");




        //Get the toolbar reference from xml layout
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new SSCustomBottomNavigation.Model(1,R.drawable.person,"person"));
        bottomNavigation.add(new SSCustomBottomNavigation.Model(2,R.drawable.location,"location"));
        bottomNavigation.add(new SSCustomBottomNavigation.Model(3,R.drawable.ring,"ring"));
        bottomNavigation.add(new SSCustomBottomNavigation.Model(4,R.drawable.settings,"settings"));

        //some stuff
        bottomNavigation.setCount(2,"11");
        bottomNavigation.show(4,true);

        replace(new SettingsFragment());

        bottomNavigation.setOnClickMenuListener(new Function1<SSCustomBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(SSCustomBottomNavigation.Model model) {

                switch (model.getId()){
                    case 1:
                        replace(new ContactFragment());
                        break;

                    case 2:
                        replace(new LocationFragment());
                        break;

                    case 3:
                        replace(new ringFragment());
                        break;

                    case 4:
                        replace(new SettingsFragment());
                        break;


                }
                return null;
            }
        });

    }

    private void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame,fragment);
        transaction.commit();
    }


//Functions:

    //get contacts name
public String getContactNum(String name_to_search) {
     final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.STARRED,
            ContactsContract.Contacts.TIMES_CONTACTED,
            ContactsContract.Contacts.CONTACT_PRESENCE,
            ContactsContract.Contacts.PHOTO_ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
    };
     String phonenumber= "";
    String select = "(" + ContactsContract.Contacts.DISPLAY_NAME + " == \"" + name_to_search + "\" )";
    Cursor c = mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, CONTACTS_SUMMARY_PROJECTION, select, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

    if (c.moveToNext()) {
        String id = c.getString(0);
        ArrayList<String> phones = new ArrayList<String>();

        Cursor pCur = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
        while (pCur.moveToNext()) {
            phones.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            Log.i("", name_to_search + " has the following phone number " + pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            phonenumber =  pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

        }
        pCur.close();
    }
    return phonenumber;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void playRingtone() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int previous_notification_interrupt_setting = notificationManager.getCurrentInterruptionFilter();
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS);
        }

        AudioManager audioManager = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            audioManager.setRingerMode(AudioManager.ADJUST_UNMUTE);
            Toast.makeText(this, "Unmuted", Toast.LENGTH_SHORT).show();


        }
        // To set full volume
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null){
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alert);
        r.setStreamType(AudioManager.STREAM_ALARM);
        r.play();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(SMS_RECEVIED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    MyReceiver receiver = new MyReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            SmsManager smsManagerSend = SmsManager.getDefault();
            LocationFragment locationFragment = new LocationFragment();

            //Help

            if (msg.equals("Help me")) {
                smsManagerSend.sendTextMessage(phoneNo, null,
                        "Ring - 'password' help me ring\n" +
                                "Location - 'password' find my location\n" +
                                "Contact - 'password' find number ",
                        null, null);
                Toast.makeText(context, "password is:  " + password, Toast.LENGTH_LONG).show();

            }
            //Ring
            else if (msg.equals(password + " help me ring")) {

                playRingtone();


            } else if (msg.equals(password + " find my location")) {
                Toast.makeText(context, "sending location", Toast.LENGTH_SHORT).show();
                locationFragment.startLocationUpdates();
                locationFragment.getLocation();

                smsManagerSend.sendTextMessage(phoneNo, null,
                        "http://maps.google.com/?q=" + latitude + "," + longitude, null, null);
            }

            else if (msg.contains(password + " find number ")) {
                String contact = msg.substring(password.length() + 13);
                String contactNumber = getContactNum(contact);
                smsManagerSend.sendTextMessage(phoneNo, null,
                        contactNumber, null, null);
            }

            }

    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                // When request is cancelled, the results array are empty
                if (
                        (grantResults.length > 0) &&
                                        (grantResults[0]
                                        + grantResults[1]
                                        + grantResults[2]
                                        + grantResults[3]
                                        + grantResults[4]
                                        + grantResults[5]
                                        + grantResults[6]
                                        == PackageManager.PERMISSION_GRANTED
                                )
                ) {
                    // Permissions are granted
                    Toast.makeText(mContext, "Permissions granted.", Toast.LENGTH_SHORT).show();
                } else {
                    // Permissions are denied
                    Toast.makeText(mContext, "Permissions denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}