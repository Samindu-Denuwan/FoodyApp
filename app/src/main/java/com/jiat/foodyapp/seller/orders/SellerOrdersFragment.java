package com.jiat.foodyapp.seller.orders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.Chart;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.EnumsAlign;
import com.anychart.anychart.LegendLayout;
import com.anychart.anychart.Pie;
import com.anychart.anychart.ValueDataEntry;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.seller.adapter.AdapterOrderSeller;
import com.jiat.foodyapp.seller.adapter.AdapterSellerEarning;
import com.jiat.foodyapp.seller.model.ModelOrderSeller;

import java.util.ArrayList;
import java.util.List;


public class SellerOrdersFragment extends Fragment {

    private TextView FilterTv;
    private ImageButton BtnFilter;
    private RecyclerView orderSellerRecycler;
    private ArrayList<ModelOrderSeller>orderSellerArrayList;
    private AdapterOrderSeller adapterOrderSeller;
    private FirebaseAuth  firebaseAuth;
    private ImageButton chartPie;
    private int inProgressCount, readyToDeliverCount,completeCount, onTheWayCount, deliveredCount,cancelledCount,riderCancelledCount;

    public SellerOrdersFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_orders, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Orders");
        FilterTv = view.findViewById(R.id.filterOrderTv);
        BtnFilter = view.findViewById(R.id.filterBtn);
        orderSellerRecycler = view.findViewById(R.id.order_recycler_seller);
        chartPie = view.findViewById(R.id.pieChart);

        firebaseAuth = FirebaseAuth.getInstance();
        loadAllOrders();
        ordersCountiP();
        orderRD();
        orderCP();
        countOW();
        countDL();
        countCL();
        countRCL();


        BtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] options = {"All", "In Progress", "Completed", "Cancelled", "Ready to Deliver","On the way", "Delivered"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Filter Orders: ")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                if (which ==0){
                                    //All Click
                                    FilterTv.setText("Showing All Orders");
                                    adapterOrderSeller.getFilter().filter("");
                                }else{
                                    String optionClicked = options[which];
                                    FilterTv.setText("Showing "+optionClicked+" Orders");
                                    adapterOrderSeller.getFilter().filter(optionClicked);
                                }
                            }
                        })
                        .show();
            }
        });

        chartPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pieChartBottomSheet();
            }
        });


        return view;

    }

    private void countRCL() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").orderByChild("orderStatus").equalTo("Rider Cancelled")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String count = "" + snapshot.getChildrenCount();
                            riderCancelledCount = Integer.parseInt(count);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void countCL() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").orderByChild("orderStatus").equalTo("Cancelled")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String count = "" + snapshot.getChildrenCount();
                            cancelledCount = Integer.parseInt(count);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void countDL() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").orderByChild("orderStatus").equalTo("Delivered")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String count = "" + snapshot.getChildrenCount();
                            deliveredCount = Integer.parseInt(count);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void countOW() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").orderByChild("orderStatus").equalTo("On the way")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String count = "" + snapshot.getChildrenCount();
                            onTheWayCount = Integer.parseInt(count);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void orderCP() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").orderByChild("orderStatus").equalTo("Completed")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String count = "" + snapshot.getChildrenCount();
                            completeCount = Integer.parseInt(count);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void orderRD() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").orderByChild("orderStatus").equalTo("Ready to Deliver")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String count = "" + snapshot.getChildrenCount();
                            readyToDeliverCount = Integer.parseInt(count);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void ordersCountiP() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").orderByChild("orderStatus").equalTo("In Progress")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String inProgress = "" + snapshot.getChildrenCount();
                            inProgressCount = Integer.parseInt(inProgress);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private AnyChartView pie_chart;

    private void pieChartBottomSheet() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.piechart_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        pie_chart = view.findViewById(R.id.pie);
        setUpChart();

        bottomSheetDialog.show();
    }

    private void setUpChart() {

        String[] status = {"In Progress","Completed", "Ready to Deliver", "On the way", "Delivered", "Cancelled", "Rider Cancelled"};
        int[] orders = {inProgressCount,readyToDeliverCount,completeCount,onTheWayCount,deliveredCount,cancelledCount,riderCancelledCount};

        Pie pie = AnyChart.pie();
        List<DataEntry>dataEntries = new ArrayList<>();

        for (int i = 0; i < status.length; i++) {
            dataEntries.add(new ValueDataEntry(status[i], orders[i]));
        }
        pie.data(dataEntries);



        pie.getLegend()
                .setItemsLayout(LegendLayout.VERTICAL)
                .setAlign(EnumsAlign.LEFT);
        pie_chart.setChart(pie);
     }

    private void loadAllOrders() {
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
                        orderSellerRecycler.setAdapter(adapterOrderSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}