package com.jiat.foodyapp.seller.orders;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiat.foodyapp.R;


public class SellerOrdersFragment extends Fragment {

    private TextView FilterTv;
    private ImageButton BtnFilter;
    private RecyclerView orderSellerRecycler;

    public SellerOrdersFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_orders, container, false);
        FilterTv = view.findViewById(R.id.filterOrderTv);
        BtnFilter = view.findViewById(R.id.filterBtn);
        orderSellerRecycler = view.findViewById(R.id.order_recycler_seller);

        return view;
    }
}