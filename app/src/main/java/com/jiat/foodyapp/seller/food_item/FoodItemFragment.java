package com.jiat.foodyapp.seller.food_item;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jiat.foodyapp.AddItemActivity;
import com.jiat.foodyapp.AddNewCategoryActivity;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.Seller_Navi_Activity;
import com.jiat.foodyapp.UpdateItemActivity;
import com.jiat.foodyapp.ViewItemSellerActivity;

import com.jiat.foodyapp.seller.profile.SellerProfileFragment;


public class FoodItemFragment extends Fragment{

    private static final String TAG = "owner";
    Button addCategoryBtn, addItemBtn, viewAllBtn, updateItemBtn;
    Fragment fragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_food_item, container, false);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        updateItemBtn = view.findViewById(R.id.btnUpdateItem);
        updateItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UpdateItemActivity.class));
            }
        });

        viewAllBtn = view.findViewById(R.id.btnViewItem);
        viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ViewItemSellerActivity.class));
            }
        });



        addItemBtn = view.findViewById(R.id.btnAddItem);
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Button pressed");

                startActivity(new Intent(getActivity(), AddItemActivity.class));
            }
        });

        addCategoryBtn = view.findViewById(R.id.btnAddCategory);
        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Button pressed");
                /*fragment = new addCategoryFragment();
                FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerSeller, fragment);
                fragmentTransaction.addToBackStack(null).commit();*/
                startActivity(new Intent(getActivity(), AddNewCategoryActivity.class));

            }
        });



    }



}
