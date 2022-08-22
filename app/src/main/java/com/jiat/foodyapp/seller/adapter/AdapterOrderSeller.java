package com.jiat.foodyapp.seller.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.seller.filterOrder.FilterOrderSeller;
import com.jiat.foodyapp.seller.model.ModelOrderSeller;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderSeller extends RecyclerView.Adapter<AdapterOrderSeller.ViewHolderItem> implements Filterable {

    private Context  context;
    public ArrayList<ModelOrderSeller> orderSellerArrayList, filterList;
    private FilterOrderSeller filter;

    public AdapterOrderSeller(Context context, ArrayList<ModelOrderSeller> orderSellerArrayList) {
        this.context = context;
        this.orderSellerArrayList = orderSellerArrayList;
        this.filterList = orderSellerArrayList;
    }

    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_seller_order_details, parent, false);
        return new ViewHolderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItem holder, int position) {

        ModelOrderSeller modelOrderSeller = orderSellerArrayList.get(position);
        String orderId= modelOrderSeller.getOrderId();
        String orderBy= modelOrderSeller.getOrderBy();
        String orderCost= modelOrderSeller.getOrderCost();
        String orderStatus= modelOrderSeller.getOrderStatus();
        String orderTime= modelOrderSeller.getOrderTime();
        String orderTo= modelOrderSeller.getOrderTo();

        //load user info
        loadUserInfo(modelOrderSeller, holder);
        
        holder.orderAmount.setText("Amount: LKR"+ orderCost);
        holder.orderStatus.setText(orderStatus);
        holder.orderId.setText("Order ID: "+orderId);

        //change order status text color
        if(orderStatus.equals("In Progress")){
            holder.orderStatus.setTextColor(context.getResources().getColor(R.color.Blue));
        }else if(orderStatus.equals("Completed")){
            holder.orderStatus.setTextColor(context.getResources().getColor(R.color.Green));
        }else if(orderStatus.equals("Cancelled")){
            holder.orderStatus.setTextColor(context.getResources().getColor(R.color.red));
        }

        //convert timestamp
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.orderDate.setText(formatedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }

    private void loadUserInfo(ModelOrderSeller modelOrderSeller, ViewHolderItem holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(modelOrderSeller.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = ""+snapshot.child("email").getValue();
                        holder.orderEmail.setText(email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderSellerArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterOrderSeller(this,filterList);
        }
        return filter;
    }

    class ViewHolderItem extends RecyclerView.ViewHolder{

        private TextView orderId, orderDate, orderEmail, orderAmount, orderStatus;
        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.orderTv);
            orderDate = itemView.findViewById(R.id.dateTv);
            orderEmail = itemView.findViewById(R.id.EmailTv);
            orderAmount = itemView.findViewById(R.id.amountTv);
            orderStatus = itemView.findViewById(R.id.statusTv);

        }
    }
}
