package com.jiat.foodyapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.foodyapp.FilterProductsUser;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.model.FavModel;
import com.jiat.foodyapp.model.Item;
import com.jiat.foodyapp.newCart.AppDatabase;
import com.jiat.foodyapp.newCart.CartProduct;
import com.jiat.foodyapp.newCart.ProductDao;

import java.util.ArrayList;
import java.util.Random;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterFavourite extends RecyclerView.Adapter<AdapterFavourite.viewHolderItem> {

    private static final String TAG = "owner";
    private Context context;
    public ArrayList<FavModel> favModels, filterList;
    private FirebaseStorage  storage;
    private FilterProductsUser filter;
    public String itemDocumentId ;
    private FirebaseFirestore  firestore;

    public int favId =1;




    public AdapterFavourite(Context context,  ArrayList<FavModel> favModels, FirebaseStorage storage) {
        this.context = context;
        this.favModels = favModels;
        this.storage = storage;


    }

    @NonNull
    @Override
    public viewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_item_user,parent,false);

        return new viewHolderItem(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull viewHolderItem holder, @SuppressLint("RecyclerView") int position) {

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();



        //getData
        FavModel favModel = favModels.get(position);
        String id = favModel.getFavId();
        String NameItem = favModel.getItemName();
        String itemPrice = favModel.getPrice();
        String itemDisAvailable = favModel.getDiscountAvailable();
        String itemDiscount = favModel.getDiscount();
        String itemDisNote = favModel.getDiscountNote();
        String itemStat = favModel.getStatus();


        //set Data
        holder.itemPrice.setText("LKR "+itemPrice);
        holder.itemName.setText(NameItem);


        if(!itemDisAvailable.equals("false")){
            holder.itemDiscount.setVisibility(View.VISIBLE);
            holder.itemDisNote.setVisibility(View.VISIBLE);
            holder.itemDiscount.setText("LKR "+itemDiscount);
            holder.itemDisNote.setText(itemDisNote);
            holder.itemPrice.setTextSize(15);
            holder.itemPrice.setPaintFlags(holder.itemPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else {
            holder.itemPrice.setTextSize(23);
            holder.itemDiscount.setVisibility(View.GONE);
            holder.itemDisNote.setVisibility(View.GONE);
            holder.itemPrice.setPaintFlags(0);

        }

        if(itemStat.equals("active")){
           holder.imgNotAvailable.setVisibility(View.GONE);
        }else{
            holder.imgNotAvailable.setVisibility(View.VISIBLE);
        }



        storage.getReference("Product_Images/"+favModel.getImage())
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



        holder.favCheckbox.setChecked(true);

        holder.favCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){

                }else{
                    EasyDB easyDB = EasyDB.init(context, "FAV_DB")
                            .setTableName("FAV_TABLE")
                            .addColumn(new Column("fav_id", new String[]{"text", "unique"}))
                            .addColumn(new Column("item_pid", new String[]{"text", "not null"}))
                            .addColumn(new Column("item_name", new String[]{"text", "not null"}))
                            .addColumn(new Column("item_price", new String[]{"text", "not null"}))
                            .addColumn(new Column("discount_available", new String[]{"text", "not null"}))
                            .addColumn(new Column("item_discount", new String[]{"text", "not null"}))
                            .addColumn(new Column("item_disNote", new String[]{"text", "not null"}))
                            .addColumn(new Column("item_image", new String[]{"text", "not null"}))
                            .addColumn(new Column("item_status", new String[]{"text", "not null"}))
                            .doneTableColumn();

                    easyDB.deleteRow(1, id);

                    favModels.remove(position);
                    notifyItemChanged(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, " Remove from the Favourite...", Toast.LENGTH_SHORT).show();

                }
            }
        });





    }



    @Override
    public int getItemCount() {
        return favModels.size();
    }




    class viewHolderItem extends RecyclerView.ViewHolder{

        private ImageView itemImage, imgNotAvailable;
        private TextView itemName,  itemPrice, itemDiscount, itemDisNote;
        private CheckBox favCheckbox;



        public viewHolderItem(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.foodPrice);

            itemDisNote = itemView.findViewById(R.id.disNote);
            itemDiscount = itemView.findViewById(R.id.disPrice);
            favCheckbox = itemView.findViewById(R.id.checkBoxFav);
            imgNotAvailable = itemView.findViewById(R.id.notAvailableImg);


        }
    }
}
