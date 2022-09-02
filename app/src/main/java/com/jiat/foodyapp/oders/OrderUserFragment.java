package com.jiat.foodyapp.oders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.adapter.AdapterOrderUser;
import com.jiat.foodyapp.model.ModelOrderUser;

import java.util.ArrayList;

public class OrderUserFragment extends Fragment {



    public OrderUserFragment() {
        // Required empty public constructor
    }


    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private RecyclerView OrderRecyclerView;
    private ArrayList<ModelOrderUser> orderList;
    private AdapterOrderUser adapterOrderUser;
    private TextView noOrderText;
    private ImageView noOrderImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_user, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Orders");
        OrderRecyclerView = view.findViewById(R.id.order_recycler);
        firebaseAuth = FirebaseAuth.getInstance();

        noOrderImage = view.findViewById(R.id.noOrderImg);
        noOrderText = view.findViewById(R.id.noOrderTv);
        noOrderText.setVisibility(View.GONE);
        noOrderImage.setVisibility(View.GONE);

        loadOrders();
        return view;
    }

    private void loadOrders() {
        orderList = new ArrayList<>();

        if(orderList.size()==0){
            noOrderText.setVisibility(View.VISIBLE);
            noOrderImage.setVisibility(View.VISIBLE);
        }else {
            noOrderText.setVisibility(View.GONE);
            noOrderImage.setVisibility(View.GONE);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();


                for (DataSnapshot ds: snapshot.getChildren()){
                    String uid = ""+ds.getRef().getKey();

                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    reference1.orderByChild("orderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                    for(DataSnapshot ds: snapshot.getChildren()) {
                                        ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);

                                        //add to list
                                        orderList.add(modelOrderUser);

                                    }
                                        noOrderText.setVisibility(View.GONE);
                                        noOrderImage.setVisibility(View.GONE);


                                    }
                                    //setup Adapter
                                    adapterOrderUser = new AdapterOrderUser(getActivity(), orderList);
                                    //set Recycler
                                    OrderRecyclerView.setAdapter(adapterOrderUser);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}