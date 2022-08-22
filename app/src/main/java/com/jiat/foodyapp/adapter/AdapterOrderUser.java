package com.jiat.foodyapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiat.foodyapp.OrderDetailsUserActivity;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.model.ModelOrderUser;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.ViewHolder>{

    private Context context;
    private ArrayList<ModelOrderUser>modelOrderUsers;

    public AdapterOrderUser(Context context, ArrayList<ModelOrderUser> modelOrderUsers) {
        this.context = context;
        this.modelOrderUsers = modelOrderUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //get data
         ModelOrderUser modelOrderUser = modelOrderUsers.get(position);
            String orderID = modelOrderUser.getOrderId();
            String orderBY = modelOrderUser.getOrderBy();
            String orderCOST = modelOrderUser.getOrderCost();
            String orderSTATUS = modelOrderUser.getOrderStatus();
            String orderTIME= modelOrderUser.getOrderTime();
            String orderTO = modelOrderUser.getOrderTo();

            //set Data
        holder.amount.setText("Amount : LKR "+orderCOST);
        holder.status.setText(orderSTATUS);
        holder.orderId.setText("Order Id: "+orderID);

        //change order status text color
        if(orderSTATUS.equals("In Progress")){
            holder.status.setTextColor(context.getResources().getColor(R.color.Blue));
        }else if(orderSTATUS.equals("Completed")){
            holder.status.setTextColor(context.getResources().getColor(R.color.Green));
        }else if(orderSTATUS.equals("Cancelled")){
            holder.status.setTextColor(context.getResources().getColor(R.color.red));
        }

        //convert timestamp
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTIME));
        String formatedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.date.setText(formatedDate);

        loadShopInfo(modelOrderUser, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailsUserActivity.class);
                intent.putExtra("orderTo", orderTO);
                intent.putExtra("orderId", orderID);
                context.startActivity(intent);
            }
        });


    }

    private void loadShopInfo(ModelOrderUser modelOrderUser, final ViewHolder holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(modelOrderUser.getOrderTo())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String ShopName = ""+snapshot.child("name").getValue();
                        holder.shopName.setText(ShopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return modelOrderUsers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView orderId, date, shopName, amount, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.orderTv);
            date = itemView.findViewById(R.id.dateTv);
            shopName = itemView.findViewById(R.id.shopTv);
            amount = itemView.findViewById(R.id.amountTv);
            status = itemView.findViewById(R.id.statusTv);
        }
    }
}
