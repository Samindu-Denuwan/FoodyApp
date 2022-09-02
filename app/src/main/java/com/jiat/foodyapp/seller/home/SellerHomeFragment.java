package com.jiat.foodyapp.seller.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.seller.adapter.AdapterOrderSeller;
import com.jiat.foodyapp.seller.model.ModelOrderSeller;
import com.jiat.foodyapp.seller.orders.SellerOrdersFragment;
import com.jiat.foodyapp.seller.ui.EarningsFragment;
import com.jiat.foodyapp.seller.ui.ReviewSellerFragment;
import com.jiat.foodyapp.seller.ui.RiderOrdersDashboardFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class SellerHomeFragment extends Fragment {

    private SwitchCompat onlineSwitch;
    private CardView allOrdersView, earningsView, ridersView, reviewView;
    private TextView allOrdersTv,totalEarnTv,ridersTv, reviewTv ;

    private AdapterOrderSeller adapterOrderSeller;
    private ArrayList<ModelOrderSeller> orderSellerArrayList;
    private RecyclerView recyclerViewOrders;
    private FirebaseAuth firebaseAuth;
    Fragment fragment = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_home, container, false);
        onlineSwitch = view.findViewById(R.id.switchStatus);
        allOrdersView = view.findViewById(R.id.cardViewOrders);
        allOrdersTv = view.findViewById(R.id.allOrderTv);
        earningsView = view.findViewById(R.id.totalEarning);
        totalEarnTv = view.findViewById(R.id.EarningTv);
        ridersView = view.findViewById(R.id.ridersCardView);
        ridersTv = view.findViewById(R.id.RidersTv);
        reviewView = view.findViewById(R.id.reviewCardView);
        reviewTv = view.findViewById(R.id.ReviewTv);
        recyclerViewOrders = view.findViewById(R.id.order_recycler);
        firebaseAuth = FirebaseAuth.getInstance();

        loadStatus();
        loadAllOrders();
        loadRiders();
        loadReviews();
        loadNewOrders();


        onlineSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onlineSwitch.isChecked()){
                    setActive();
                }else{
                    setInactive();
                }
            }
        });

        allOrdersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment =  new SellerOrdersFragment();
                loadFragment(fragment);


            }
        });

        earningsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment =  new EarningsFragment();
                loadFragment(fragment);

            }
        });

        ridersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment =  new RiderOrdersDashboardFragment();
                loadFragment(fragment);
            }
        });

        reviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment =  new ReviewSellerFragment();
                loadFragment(fragment);

            }
        });







        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerSeller, fragment);
        fragmentTransaction.commit();

    }

    private void loadNewOrders() {

        orderSellerArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderSellerArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderSeller modelOrderSeller = ds.getValue(ModelOrderSeller.class);
                            orderSellerArrayList.add(modelOrderSeller);
                        }
                        adapterOrderSeller = new AdapterOrderSeller(getActivity(), orderSellerArrayList);
                        adapterOrderSeller.getFilter().filter("In Progress");
                        recyclerViewOrders.setAdapter(adapterOrderSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void setInactive() {
        if (getActivity() == null) {
            return;
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("shopOpen", "" + "false");

        //update to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //updated
                        Toast.makeText(getActivity(), "You are Change to Shop Close Mode", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to update
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setActive() {
        if (getActivity() == null) {
            return;
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("shopOpen", "" + "true");

        //update to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //updated
                        Toast.makeText(getActivity(), "You are Change to Shop Open Mode", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to update
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadStatus() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String open = ""+ds.child("shopOpen").getValue();
                    if(open.equals("true")){
                        onlineSwitch.setChecked(true);
                    }else{
                        onlineSwitch.setChecked(false);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadReviews() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Ratings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String count = ""+ snapshot.getChildrenCount();
                    reviewTv.setText(count);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadRiders() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("accountType").equalTo("Rider").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String count = ""+ snapshot.getChildrenCount();
                    ridersTv.setText(count);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllOrders() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String count = ""+ snapshot.getChildrenCount();
                    allOrdersTv.setText(count);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}