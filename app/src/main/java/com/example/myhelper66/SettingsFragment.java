package com.example.myhelper66;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    //Requests
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    //Password
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String SHARED_PASSWORD = "Password";

    private EditText password_ET;

    private final String DefaultPasswordValue = "";
    private String passwordValue;
    private Activity mActivity;


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mActivity = this.getActivity();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //Initialize password EditText
        password_ET = view.findViewById(R.id.editTextTextPassword);

        ////Initialize Check Permssion button
        Button check_permssion = view.findViewById(R.id.check_permission);

        check_permssion.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
            checkPermission();
            }
        });

        Button set_BTN = view.findViewById(R.id.set);
        //set Button
        set_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_password();
            }
        });

        load_data();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("password", passwordValue);
        return view;




    }



    public void set_password() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PASSWORD, password_ET.getText().toString());
        editor.apply();

        Toast.makeText(getActivity(), "Password has been saved!", Toast.LENGTH_SHORT).show();
    }
    public void load_data() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE );
        passwordValue = sharedPreferences.getString(SHARED_PASSWORD, "");
        password_ET.setText(passwordValue);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.RECEIVE_SMS)
                + ContextCompat.checkSelfPermission(
                this.getActivity(),Manifest.permission.SEND_SMS)
                + ContextCompat.checkSelfPermission(
                this.getActivity(),Manifest.permission.READ_SMS)
                + ContextCompat.checkSelfPermission(
                this.getActivity(),Manifest.permission.READ_CONTACTS)
                + ContextCompat.checkSelfPermission(
                this.getActivity(),Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                + ContextCompat.checkSelfPermission(
                this.getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)
                + ContextCompat.checkSelfPermission(
                this.getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this.getActivity(),Manifest.permission.RECEIVE_SMS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this.getActivity(),Manifest.permission.SEND_SMS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this.getActivity(),Manifest.permission.READ_SMS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this.getActivity(),Manifest.permission.READ_CONTACTS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this.getActivity(),Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this.getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this.getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)
            ){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                builder.setMessage("Camera, Read Contacts and Write External" +
                        " Storage permissions are required to do the task.");
                builder.setTitle("Please grant those permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { ;
                        ActivityCompat.requestPermissions(
                                mActivity,
                                new String[]{
                                        Manifest.permission.RECEIVE_SMS,
                                        Manifest.permission.SEND_SMS,
                                        Manifest.permission.READ_SMS,
                                        Manifest.permission.READ_CONTACTS,
                                        Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION


                                },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNeutralButton("Cancel",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        this.getActivity(),
                        new String[]{
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.READ_SMS,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
                NotificationManager notificationManager =
                        (NotificationManager) this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !notificationManager.isNotificationPolicyAccessGranted()) {

                    Intent intent = new Intent(
                            android.provider.Settings
                                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                    startActivity(intent);
                }
            }
        }else {
            // Do something, when permissions are already granted
            Toast.makeText(this.getContext(),"Permissions already granted",Toast.LENGTH_SHORT).show();
        }
    }
}