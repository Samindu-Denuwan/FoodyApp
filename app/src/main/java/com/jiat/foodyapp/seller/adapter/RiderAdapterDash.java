package com.jiat.foodyapp.seller.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jiat.foodyapp.OrderDetailsSellerActivity;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.seller.model.RiderModel;
import com.jiat.foodyapp.seller.ui.RiderOrdersDashboardFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class RiderAdapterDash extends RecyclerView.Adapter<RiderAdapterDash.viewHolderItem> {

    private Context context;
    private ArrayList<RiderModel> riderModels;
    private RiderOrdersDashboardFragment fragment;



    public RiderAdapterDash(Context context, ArrayList<RiderModel> riderModels, RiderOrdersDashboardFragment fragment) {
        this.context = context;
        this.riderModels = riderModels;
        this.fragment = fragment;


    }

    @NonNull
    @Override
    public viewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rider_dash, parent, false);
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


        Glide.with(context)
                .load(riderImg)
                .circleCrop()
                .into(holder.rider_Image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Random random = new Random();
                int riderID = random.nextInt(200000);


                EasyDB easyDB = EasyDB.init(context, "DB_RIDER2")
                        .setTableName("RIDER2_TABLE")
                        .addColumn(new Column("rid", new String[]{"text", "unique"}))
                        .addColumn(new Column("rider_id", new String[]{"text", "not null"}))
                        .addColumn(new Column("rider_name", new String[]{"text", "not null"}))
                        .doneTableColumn();


                Boolean b = easyDB.addData("rid", riderID)
                        .addData("rider_id", riderId)
                        .addData("rider_name", riderName)
                        .doneDataAdding();


                fragment.loadOrders();

            }
        });


    }





    @Override
    public int getItemCount() {
        return riderModels.size();
    }

    class viewHolderItem extends RecyclerView.ViewHolder {

        private ImageView rider_Image;
        private TextView rider_Name;

        public viewHolderItem(@NonNull View itemView) {
            super(itemView);

            rider_Image = itemView.findViewById(R.id.rider_image);
            rider_Name = itemView.findViewById(R.id.rider_name);


        }
    }

}
