package com.jiat.foodyapp.seller.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.foodyapp.OrderDetailsSellerActivity;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.RegisterUserActivity;
import com.jiat.foodyapp.UserNaviActivity;
import com.jiat.foodyapp.model.Item;
import com.jiat.foodyapp.seller.model.RiderModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class RiderAdapter extends RecyclerView.Adapter<RiderAdapter.viewHolderItem> {

    private Context context;
    private ArrayList<RiderModel> riderModels;

    private ImageView imgProfileBtn;
    private TextView Name, Mobile, Email;
    private Button ApproveBtn;


    public RiderAdapter(Context context, ArrayList<RiderModel> riderModels) {
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
            holder.inactive_status.setVisibility(View.GONE);
            if(!riderStatus.equals("false")){
                holder.inactive_status.setVisibility(View.GONE);
                holder.active_Status.setVisibility(View.VISIBLE);

            }else{
                holder.inactive_status.setVisibility(View.VISIBLE);
                holder.active_Status.setVisibility(View.GONE);
            }


        } else {
            holder.active_Status.setVisibility(View.GONE);
            holder.inactive_status.setVisibility(View.GONE);
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
              riderInfo(riderModel);

            }
        });


    }

    private ImageButton CallRider;
    private void riderInfo(RiderModel riderModel) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.rider_bottomsheet, null);
        bottomSheetDialog.setContentView(view);

        imgProfileBtn = view.findViewById(R.id.imageViewUser);
        Name =view.findViewById(R.id.FullName);
        Email = view.findViewById(R.id.Email);
        Mobile= view.findViewById(R.id.PhoneNum);
        ApproveBtn = view.findViewById(R.id.btbApprove);
        CallRider = view.findViewById(R.id.callBtnRider);


        ApproveBtn.setVisibility(View.INVISIBLE);

        if(riderModel.getRegisterStatus().equals("false")){
            ApproveBtn.setVisibility(View.VISIBLE);

            ApproveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Approve a New Rider")
                            .setCancelable(false)
                            .setMessage("Are you want to Approve " +riderModel.getName() +" as a Rider ?")
                            .setPositiveButton("APPROVE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    approveRider(riderModel.getUid(), bottomSheetDialog);



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

        }else{
            ApproveBtn.setVisibility(View.INVISIBLE);
        }


        Email.setText(riderModel.getEmail());
        Name.setText(riderModel.getName());
        Mobile.setText(riderModel.getPhone());


        Glide.with(context)
                .load(riderModel.getProfileImage())
                .circleCrop()
                .into(imgProfileBtn);


        CallRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(riderModel.getPhone()))));
                bottomSheetDialog.dismiss();
                Toast.makeText(context, "Calling To Rider", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }

    private void approveRider(String riderId, BottomSheetDialog bottomSheetDialog) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("registerStatus", "" + "true");


        //save to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(riderId).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //db update
                       bottomSheetDialog.dismiss();
                        Toast.makeText(context, "Rider Approved...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });


    }

    @Override
    public int getItemCount() {
        return riderModels.size();
    }

    class viewHolderItem extends RecyclerView.ViewHolder {

        private ImageView rider_Image;
        private TextView rider_Name, rider_Mobile, approve_Status, active_Status, inactive_status;

        public viewHolderItem(@NonNull View itemView) {
            super(itemView);

            rider_Image = itemView.findViewById(R.id.riderImage);
            rider_Name = itemView.findViewById(R.id.riderName);
            rider_Mobile = itemView.findViewById(R.id.riderMobile);

            approve_Status = itemView.findViewById(R.id.approveStatus);
            active_Status = itemView.findViewById(R.id.activeStatus);
            inactive_status = itemView.findViewById(R.id.inActiveStatus);

        }
    }

}
