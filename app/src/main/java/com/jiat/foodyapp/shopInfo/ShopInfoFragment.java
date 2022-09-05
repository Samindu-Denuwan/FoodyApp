package com.jiat.foodyapp.shopInfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.jiat.foodyapp.seller.ui.ReviewSellerFragment;


public class ShopInfoFragment extends Fragment {

    private ImageView profileImg;
    private TextView username , address, mobileNumberView, emailView;
    private ImageButton  btnCall, btnReview;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private  String ShopUid = null;

    public ShopInfoFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_info, container, false);

        profileImg = view.findViewById(R.id.profileImgView);
        username = view.findViewById(R.id.profileName);
        btnCall = view.findViewById(R.id.callBtn);
        btnReview = view.findViewById(R.id.reviewBtn);
        address = view.findViewById(R.id.addressTv);
        mobileNumberView = view.findViewById(R.id.mobileTv);
        emailView = view.findViewById(R.id.emailTv);
        ShopUid = "d6RHVgGQoNZMkciEMVl16lSSsIw2";

        loadShopInfo();

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ReviewSellerFragment();
                FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(mobileNumberView.getText().toString()))));

                Toast.makeText(getActivity(), "Calling To Shop", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void loadShopInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(ShopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (getActivity() == null) {
                            return;
                        }
                        for(DataSnapshot ds : snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();
                            String user_address = ""+ds.child("address").getValue();
                            String img = ""+ds.child("profileImage").getValue();
                            String phone = ""+ds.child("phone").getValue();

                            username.setText(name);
                            mobileNumberView.setText(phone);
                            emailView.setText(email);
                            address.setText(user_address);
                            Glide.with(getActivity()).load(img).circleCrop()
                                    .into(profileImg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}