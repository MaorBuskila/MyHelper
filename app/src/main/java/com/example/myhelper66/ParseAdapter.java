package com.example.myhelper66;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.MyViewHolder> {

    Context context;
    ArrayList contact_id, contact_name, contact_phonenumber;

    ParseAdapter(Context context, ArrayList contact_id, ArrayList contact_name, ArrayList contact_phonenumber){
        this.context = context;
        this.contact_id = contact_id;
        this.contact_name = contact_name;
        this.contact_phonenumber = contact_phonenumber;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParseAdapter.MyViewHolder holder, int position) {
    holder.contact_id_txt.setText(String.valueOf(contact_id.get(position)));
    holder.contact_name_text.setText(String.valueOf(contact_name.get(position)));
    holder.contact_phonenumber_text.setText(String.valueOf(contact_phonenumber.get(position)));
    }

    @Override
    public int getItemCount() {
        return contact_id.size() ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView contact_id_txt, contact_name_text, contact_phonenumber_text;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            contact_id_txt = itemView.findViewById(R.id.contact_id);
            contact_name_text = itemView.findViewById(R.id.contact_name);
            contact_phonenumber_text = itemView.findViewById(R.id.contact_phonenumber);
        }
    }
}
