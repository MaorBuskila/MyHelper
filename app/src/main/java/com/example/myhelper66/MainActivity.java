package com.example.myhelper66;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
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
import android.view.KeyEvent;
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
    private String latitude ;
    private String longitude ;
    private String name;
    private String phonenumebr;

    //Initialize toolBar
    private SSCustomBottomNavigation bottomNavigation;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String SHARED_PASSWORD = "Password";
    private static final String SHARED_LONG = "longitude";
    private static final String SHARED_LAT = "latitude";
    Ringtone r;






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
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        this.r = RingtoneManager.getRingtone(getApplicationContext(), alert);



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdates();


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
                        replace(new BlackListFragment());
                        break;

                    case 2:
                        replace(new LocationFragment());
                        getLocation();
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

    //add contact to blacklist
    public void add_to_blackList(String phonenumebr) {
        MyDataBase myDataBase = new MyDataBase(this);
        String contact_name = getContactName(phonenumebr, mContext);
        myDataBase.addToBlackList(contact_name, phonenumebr);
    }

    //check if contact is blocked
    public boolean contact_is_blocked(String phonenumebr) {
        return true;
    }


    // get user password
    public void load_password() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        password = sharedPreferences.getString(SHARED_PASSWORD, "");
    }

    //LOCATION FUNCTIONS
    void getLocation() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.

                if (location != null) {
                    // Logic to handle location object
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                }
                //check why 0 , 0
                else {
                    latitude = "6";
                    longitude = "9";

                }
            }
        });
    }

    //send location
    public void send_and_push_location() {

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_LAT, latitude);
        editor.putString(SHARED_LONG, longitude);
        editor.apply();
    }



    // Trigger new location updates at interval
    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        //location
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /* 10 secs */
        long UPDATE_INTERVAL = 60 * 1000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        /* 2 sec */
        long FASTEST_INTERVAL = 2000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(mContext).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
//        String msg = "Updated Location: " +
//                Double.toString(location.getLatitude()) + "," +
//                Double.toString(location.getLongitude());
//        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        if (location != null) {
            // Logic to handle location object
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
            send_and_push_location();
        }
    }

    //END Location function

    //get contacts number
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

    public String getContactName(final String phoneNumber, Context context)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void playRingtone() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int previous_notification_interrupt_setting = notificationManager.getCurrentInterruptionFilter();
            //check if not distrub is allowed!
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS);
        }

        AudioManager audioManager = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            audioManager.setRingerMode(AudioManager.ADJUST_UNMUTE);
            Toast.makeText(this, "Unmuted", Toast.LENGTH_SHORT).show();


        }
        // To set full volume
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
 //       Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//
//        if (alert == null){
//            // alert is null, using backup
//            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            if (alert == null){
//                // alert backup is null, using 2nd backup
//                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//            }
//        }
//        r = RingtoneManager.getRingtone(getApplicationContext(), alert);
        this.r.setStreamType(AudioManager.STREAM_ALARM);
        this.r.play();
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
    //NOT WORKING - pause ring with PWR BTN
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
            // do what you want with the power button
                this.r.stop();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    MyReceiver receiver = new MyReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            SmsManager smsManagerSend = SmsManager.getDefault();

            phonenumebr = phoneNo;
            MyDataBase myDataBase = new MyDataBase(mContext);
            boolean isblocked = myDataBase.CheckIfContactBlocked(phoneNo);

//            else if (msg.equals(password +"Unblock")) {
//                myDataBase.UnBlockContact(phonenumebr);
//            } else {
                if (msg.equals("Help me")) {
                    add_to_blackList(phoneNo);
                    if (isblocked) {
                        add_to_blackList(phoneNo);
                        smsManagerSend.sendTextMessage(phoneNo, null,
                                "You are Blocked",
                                null, null);

                    } else {
                        add_to_blackList(phoneNo);
                        smsManagerSend.sendTextMessage(phoneNo, null,
                                "Ring - 'password' help me ring\n " +
                                        "Location - 'password' find my location\n " +
                                        "Contact - 'password' find number",
                                null, null);
                    }
                }
//                if (msg.equals("Block")) {
//                    myDataBase.BlockContact(phonenumebr);
//                }
//                if (msg.equals("Unblock")) {
//                    myDataBase.UnBlockContact(phonenumebr);
//                }
                if (msg.equals("Block?")) {
                     if (myDataBase.CheckIfContactBlocked(phonenumebr)) {
                    smsManagerSend.sendTextMessage(phoneNo, null,
                            "You are Blocked",
                            null, null);
                } else {
                        smsManagerSend.sendTextMessage(phoneNo, null,
                                "No problem!",
                                null, null);
                    }

            }
                //Ring
                else if (msg.equals(password + " help me ring")) {
                    if (isblocked) {
                        smsManagerSend.sendTextMessage(phoneNo, null,
                                "You are Blocked",
                                null, null);

                    } else {
                        playRingtone();
                    }


                } else if (msg.equals(password + " find my location")) {
                    if (isblocked) {
                        smsManagerSend.sendTextMessage(phoneNo, null,
                                "You are Blocked",
                                null, null);

                    } else {
                        smsManagerSend.sendTextMessage(phoneNo, null,
                                "http://maps.google.com/?q=" + latitude + "," + longitude, null, null);
                    }
                } else if (msg.contains(password + " find number ")) {
                    if (isblocked) {
                        smsManagerSend.sendTextMessage(phoneNo, null,
                                "You are Blocked",
                                null, null);

                    } else {
                        String contact = msg.substring(password.length() + 13);
                        String contactNumber = getContactNum(contact);
                        smsManagerSend.sendTextMessage(phoneNo, null,
                                contactNumber, null, null);
                    }
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