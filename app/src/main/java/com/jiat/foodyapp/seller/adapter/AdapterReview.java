package com.jiat.foodyapp.seller.adapter;


import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.seller.model.ModelReview;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.ViewHolderIems> {
    private Context context;
    private ArrayList<ModelReview> reviewArrayList;

    public AdapterReview(Context context, ArrayList<ModelReview> reviewArrayList) {
        this.context = context;
        this.reviewArrayList = reviewArrayList;
    }

    @NonNull
    @Override
    public ViewHolderIems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(context).inflate(R.layout.ratings, parent, false);
        return new ViewHolderIems(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderIems holder, int position) {
        ModelReview modelReview = reviewArrayList.get(position);
        String uid = modelReview.getUid();
        String rating = modelReview.getRatings();
        String review = modelReview.getReview();
        String time = modelReview.getTimestamp();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(time));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        loadCustomerName(modelReview, holder);
        holder.ratingBarReview.setRating(Float.parseFloat(rating));
        holder.customerReview.setText(review);
        holder.date.setText(dateFormat);


    }

    private void loadCustomerName(ModelReview modelReview, ViewHolderIems holder) {
        String uid = modelReview.getUid();
        DatabaseReference  reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String name = ""+snapshot.child("name").getValue();
                    String img = ""+snapshot.child("profileImage").getValue();
                    holder.customerName.setText(name);
                    Glide.with(context).load(img).into(holder.userImg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

    class ViewHolderIems extends RecyclerView.ViewHolder{

        private ImageView userImg;
        private TextView customerName, customerReview, date;
        private RatingBar ratingBarReview;
        public ViewHolderIems(@NonNull View itemView) {
            super(itemView);

            userImg = itemView.findViewById(R.id.userImg);
            customerName = itemView.findViewById(R.id.NameTv);
            ratingBarReview = itemView.findViewById(R.id.ratingBar);
            customerReview = itemView.findViewById(R.id.reviewUser);
            date = itemView.findViewById(R.id.dateTv);

        }
    }
}
