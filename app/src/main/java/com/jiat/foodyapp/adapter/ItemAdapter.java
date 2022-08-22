package com.jiat.foodyapp.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.model.Item;

import java.util.ArrayList;

public class ItemAdapter  extends RecyclerView.Adapter<ItemAdapter.viewHolderItem> {

    private Context context;
    private ArrayList<Item> items;
    private FirebaseStorage  storage;



    public ItemAdapter(Context context, ArrayList<Item> items, FirebaseStorage storage) {
        this.context = context;
        this.items = items;
        this.storage = storage;

    }

    @NonNull
    @Override
    public viewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_item,parent,false);
        return new viewHolderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolderItem holder, int position) {

        //getData
        Item item = items.get(position);
        holder.statusImg.setVisibility(View.GONE);

        String NameItem = item.getItemName();
        String itemPrice = item.getPrice();
        String itemDisAvailable = item.getDiscountAvailable();
        String itemDiscount = item.getDiscount();
        String itemDisNote = item.getDiscountNote();
        String itemStat = item.getStatus();

        //set Data
        holder.itemPrice.setText(itemPrice);
        holder.itemName.setText(NameItem);

        if(!itemDisAvailable.equals("false")){
            holder.itemDiscount.setVisibility(View.VISIBLE);
            holder.itemDisNote.setVisibility(View.VISIBLE);
            holder.itemDiscount.setText(itemDiscount);
            holder.itemDisNote.setText(itemDisNote);
            holder.itemPrice.setTextSize(15);
            holder.itemPrice.setPaintFlags(holder.itemPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else {
            holder.itemPrice.setTextSize(23);
            holder.itemDiscount.setVisibility(View.GONE);
            holder.itemDisNote.setVisibility(View.GONE);
            holder.itemPrice.setPaintFlags(0);
        }

        storage.getReference("Product_Images/"+item.getImage())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri)
                                .into(holder.itemImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });




    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class viewHolderItem extends RecyclerView.ViewHolder{

        private ImageView itemImage;
        private TextView itemName,  itemPrice, itemDiscount, itemDisNote;
        private ImageView statusImg;

        public viewHolderItem(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.foodPrice);

            itemDisNote = itemView.findViewById(R.id.disNote);
            itemDiscount = itemView.findViewById(R.id.disPrice);
            statusImg = itemView.findViewById(R.id.statusIcon);
        }
    }
}
