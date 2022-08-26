package com.jiat.foodyapp.seller.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jiat.foodyapp.OrderDetailsSellerActivity;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.seller.model.RiderModel;

import java.util.ArrayList;
import java.util.Random;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class RiderAdapterOrder extends RecyclerView.Adapter<RiderAdapterOrder.viewHolderItem> {

    private Context context;
    private ArrayList<RiderModel> riderModels;
    public int deliverID;



    public RiderAdapterOrder(Context context, ArrayList<RiderModel> riderModels) {
        this.context = context;
        this.riderModels = riderModels;



    }

    @NonNull
    @Override
    public viewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rider_layout, parent, false);
        return new viewHolderItem(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull viewHolderItem holder, int position) {

        //getData
        RiderModel riderModel = riderModels.get(position);


        String riderId = riderModel.getUid();
        String riderName = riderModel.getName();
        String riderMobile = riderModel.getPhone();
        String riderStatus = riderModel.getOnline();
        String riderRegisterStatus = riderModel.getRegisterStatus();
        String riderAddress = riderModel.getAddress();
        String riderImg = riderModel.getProfileImage();
        String latitude = riderModel.getLatitude();
        String timestamp = riderModel.getTimestamp();
        String longitude = riderModel.getLongitude();
        String email = riderModel.getEmail();
        String accountType = riderModel.getAccountType();





        //set Data
        holder.rider_Name.setText(riderName);
        holder.rider_Mobile.setText(riderMobile);

        if (!riderRegisterStatus.equals("false")) {
            holder.active_Status.setVisibility(View.VISIBLE);
            holder.approve_Status.setVisibility(View.GONE);
            if(!riderStatus.equals("false")){
                holder.active_Status.setText("Active");
                holder.approve_Status.setTextColor(Color.GREEN);
            }else{
                holder.active_Status.setText("Inactive");
                holder.approve_Status.setTextColor(Color.RED);
            }


        } else {
            holder.active_Status.setVisibility(View.GONE);
            holder.approve_Status.setVisibility(View.VISIBLE);
            holder.approve_Status.setText("New Rider");
        }

        Glide.with(context)
                .load(riderImg)
                .circleCrop()
                .into(holder.rider_Image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select Rider")
                        .setCancelable(false)
                        .setMessage("Are you want to Select " +riderName +" as a Rider ?")
                        .setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Random random = new Random();
                                deliverID = random.nextInt(200000);


                                EasyDB easyDB = EasyDB.init(context, "DELIVER_DB")
                                        .setTableName("DELIVER_TABLE")
                                        .addColumn(new Column("deliver_id", new String[]{"text", "unique"}))
                                        .addColumn(new Column("rider_id", new String[]{"text", "not null"}))
                                        .addColumn(new Column("rider_name", new String[]{"text", "not null"}))
                                        .addColumn(new Column("rider_mobile", new String[]{"text", "not null"}))
                                        .addColumn(new Column("rider_img", new String[]{"text", "not null"}))
                                        .doneTableColumn();


                                Boolean b = easyDB.addData("deliver_id", deliverID)
                                        .addData("rider_id", riderId)
                                        .addData("rider_name",riderName)
                                        .addData("rider_mobile", riderMobile)
                                        .addData("rider_img", riderImg)
                                        .doneDataAdding();



                                if (context instanceof OrderDetailsSellerActivity) {
                                    ((OrderDetailsSellerActivity)context).dismissBottomSheet();
                                    ((OrderDetailsSellerActivity)context).deliverStatus();
                                }

                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //cancel
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });

    }



    @Override
    public int getItemCount() {
        return riderModels.size();
    }

    class viewHolderItem extends RecyclerView.ViewHolder {

        private ImageView rider_Image;
        private TextView rider_Name, rider_Mobile, approve_Status, active_Status;


        public viewHolderItem(@NonNull View itemView) {
            super(itemView);

            rider_Image = itemView.findViewById(R.id.riderImage);
            rider_Name = itemView.findViewById(R.id.riderName);
            rider_Mobile = itemView.findViewById(R.id.riderMobile);

            approve_Status = itemView.findViewById(R.id.approveStatus);
            active_Status = itemView.findViewById(R.id.activeStatus);


        }
    }

}
