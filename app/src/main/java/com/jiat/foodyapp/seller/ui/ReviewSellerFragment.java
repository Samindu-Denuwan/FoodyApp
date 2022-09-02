package com.jiat.foodyapp.seller.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.seller.adapter.AdapterReview;
import com.jiat.foodyapp.seller.model.ModelReview;

import java.util.ArrayList;

public class ReviewSellerFragment extends Fragment {


private RecyclerView recyclerViewReview;
private ImageView shopImage;
private TextView shopNameTv, ratingTv;
private RatingBar ratingBar;
private FirebaseAuth firebaseAuth;
private AdapterReview adapterReview;
private ArrayList<ModelReview> reviewArrayList;


    public ReviewSellerFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_seller, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Reviews");
        recyclerViewReview = view.findViewById(R.id.reviewRecycler);
        shopImage = view.findViewById(R.id.shopImgView);
        shopNameTv = view.findViewById(R.id.ShopName);
        ratingBar = view.findViewById(R.id.ratingBar);
        ratingTv = view.findViewById(R.id.ratingTv);

        firebaseAuth = FirebaseAuth.getInstance();

        loadShopDetails();
        loadReview();




        return view;
    }

    private void loadShopDetails() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String shopName = ""+snapshot.child("name").getValue();
                String shopImg = ""+snapshot.child("profileImage").getValue();

                shopNameTv.setText(shopName);
                Glide.with(getActivity()).load(shopImg).into(shopImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private float ratingSum = 0;
    private void loadReview() {
        reviewArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Ratings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                reviewArrayList.clear();
                ratingSum = 0;
                for(DataSnapshot ds: snapshot.getChildren()){
                    float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                    ratingSum = ratingSum + rating;

                    ModelReview modelReview = ds.getValue(ModelReview.class);
                    reviewArrayList.add(modelReview);
                }

                adapterReview = new AdapterReview(getActivity(), reviewArrayList);
                recyclerViewReview.setAdapter(adapterReview);

                long countReview = snapshot.getChildrenCount();
                float avg = ratingSum/countReview;
               ratingTv.setText(String.format("%.2f", avg) + "  ["+countReview+"]");
                ratingBar.setRating(avg);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}