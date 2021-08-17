package com.example.myhelper66;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;


public class BlackListFragment extends Fragment {

    RecyclerView recyclerView;

    //Initialize DataBase
    MyDataBase dataBase;

    //Initialize arraylist of value for blacklist
    ArrayList<String> contact_id, contact_name, contact_phonenumber, contact_isBLocked;

    ParseAdapter parseAdapter;

    boolean isBlocked = false;


    private Activity mActivity;
    private Context mContext;
    View view;

    public BlackListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getContext();

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blacklist, container, false);

        dataBase = new MyDataBase(mContext);
        contact_id = new ArrayList<>();
        contact_name = new ArrayList<>();
        contact_phonenumber = new ArrayList<>();
        contact_isBLocked = new ArrayList<>();
        parseAdapter = new ParseAdapter(mContext , contact_id, contact_name, contact_phonenumber,contact_isBLocked);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(parseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        storeDataInArray();



        // Inflate the layout for this fragment
        return view;
    }

    void storeDataInArray() {
        Cursor cursor = dataBase.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(getContext(), "No data.", Toast.LENGTH_SHORT).show();
        }
        else {
            while (cursor.moveToNext()){
                contact_id.add(cursor.getString(0));

                contact_name.add(cursor.getString(1));
                contact_phonenumber.add(cursor.getString(2));
                contact_isBLocked.add(cursor.getString(3));


            }
        }
    }

}