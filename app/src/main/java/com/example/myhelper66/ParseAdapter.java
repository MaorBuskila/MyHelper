package com.example.myhelper66;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.MyViewHolder> {

    Context context;
    ArrayList contact_id, contact_name, contact_phonenumber, contact_isBLocked;
    boolean isBLocked;

    ParseAdapter(Context context, ArrayList contact_id, ArrayList contact_name, ArrayList contact_phonenumber, ArrayList<String> contact_isBLocked){
        this.context = context;
        this.contact_id = contact_id;
        this.contact_name = contact_name;
        this.contact_phonenumber = contact_phonenumber;
        this.contact_isBLocked = contact_isBLocked;
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
    holder.contact_isBLocked_num = String.valueOf(contact_isBLocked.get(position));

    }

    @Override
    public int getItemCount() {
        return contact_id.size() ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView contact_id_txt, contact_name_text, contact_phonenumber_text;
        String contact_isBLocked_num = "1";
        LottieAnimationView lottieBlocked = itemView.findViewById(R.id.block_btn);



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            MyDataBase myDataBase = new MyDataBase(itemView.getContext());
            contact_id_txt = itemView.findViewById(R.id.contact_id);
            contact_name_text = itemView.findViewById(R.id.contact_name);
            contact_phonenumber_text = itemView.findViewById(R.id.contact_phonenumber);
            if(contact_isBLocked_num.equals("0")) {
                switchOn();
            }
            if(contact_isBLocked_num.equals("1")) {
                swtichOff();
            }
            lottieBlocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(contact_isBLocked_num.equals("0")) {
                    lottieBlocked.setMinAndMaxProgress(0.5f,1.0f);
                    lottieBlocked.playAnimation();
                    myDataBase.UnBlockContact((String) contact_phonenumber_text.getText());
                    contact_isBLocked_num="1";
                }
               else {
                    lottieBlocked.setSpeed(-1);
                    lottieBlocked. setMinAndMaxProgress(0.0f,0.5f);
                    lottieBlocked.playAnimation();
                    myDataBase.BlockContact((String) contact_phonenumber_text.getText());
                    contact_isBLocked_num="0";

                }
            }
        });


            }
        void switchOn(){
                lottieBlocked.setMinAndMaxProgress(0.5f,1.0f);
                lottieBlocked.playAnimation();

        }
        void swtichOff(){
                lottieBlocked.setSpeed(-1);
                lottieBlocked. setMinAndMaxProgress(0.0f,0.5f);
                lottieBlocked.playAnimation();

        }
    }
}
