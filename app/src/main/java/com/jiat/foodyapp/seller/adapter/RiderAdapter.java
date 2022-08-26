package com.jiat.foodyapp.seller.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.model.Item;
import com.jiat.foodyapp.seller.model.RiderModel;

import java.util.ArrayList;

public class RiderAdapter extends RecyclerView.Adapter<RiderAdapter.viewHolderItem> {

    private Context context;
    private ArrayList<RiderModel> riderModels;


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
