package com.jiat.foodyapp.adapter;

import static com.jiat.foodyapp.R.color.red;
import static com.jiat.foodyapp.R.color.white;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jiat.foodyapp.FilterProducts;
import com.jiat.foodyapp.ItemUpdateActivity;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.model.Item;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemAdapterUpdate extends RecyclerView.Adapter<ItemAdapterUpdate.viewHolderItem> implements Filterable {

    private static final String TAG = "owner";
    private Context context;
    public ArrayList<Item> items, filterList;
    private FirebaseStorage  storage;
    private FilterProducts filter;
    public String itemDocumentId ;
    private FirebaseFirestore  firestore;
    private SwitchCompat activeSwitch;




    public ItemAdapterUpdate(Context context, ArrayList<Item> items, FirebaseStorage storage) {
        this.context = context;
        this.items = items;
        this.storage = storage;
        this.filterList = items;

    }

    @NonNull
    @Override
    public viewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_item,parent,false);

        return new viewHolderItem(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull viewHolderItem holder, int position) {

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();



        //getData
        Item item = items.get(position);

        String NameItem = item.getItemName();
        String itemPrice = item.getPrice();
        String itemDisAvailable = item.getDiscountAvailable();
        String itemDiscount = item.getDiscount();
        String itemDisNote = item.getDiscountNote();
        String itemStat = item.getStatus();


        //set Data
        holder.itemPrice.setText(itemPrice);
        holder.itemName.setText(NameItem);


        if(itemStat.equals("active")){
            holder.statusImg.setImageResource(R.drawable.ic__active);
        }else{
            holder.statusImg.setImageResource(R.drawable.ic_inactive);
        }


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


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemDocumentId= item.getProductId();
                String name = item.getItemName();
               // Toast.makeText(context, "ID : "+itemDocumentId+" "+name, Toast.LENGTH_SHORT).show();
                detailsBottomSheet(item);


            }
        });







    }

    private void detailsBottomSheet(Item item) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.products_details_seller, null);
        bottomSheetDialog.setContentView(view);

        //init buttons
        ImageButton btnDelete = view.findViewById(R.id.deleteImgBtn);
        ImageButton btnEdit= view.findViewById(R.id.editImgBtn);

        ImageView imgProduct = view.findViewById(R.id.productImgView);
        /*ImageView imgShowStatus = view.findViewById(R.id.showStatus);*/

        TextView productName = view.findViewById(R.id.TitleTv);
        TextView productCategory = view.findViewById(R.id.CategoryTv);
        TextView productDesc = view.findViewById(R.id.DescTv);
        TextView productPrice = view.findViewById(R.id.PriceTv);
        TextView productDisc = view.findViewById(R.id.DiscPriceTv);
        TextView productDiscNote = view.findViewById(R.id.DiscNoteTv);
        activeSwitch = view.findViewById(R.id.switchStatus);


        //get data
        final String id = item.getProductId();
        String NameItem = item.getItemName();
        String itemDesc = item.getDetails();
        String itemPrice = item.getPrice();
        String itemDisAvailable = item.getDiscountAvailable();
        String itemDiscount = item.getDiscount();
        String itemDisNote = item.getDiscountNote();
        String itemStat = item.getStatus();
        String category = item.getCheckedCategory();

        //set data

        productName.setText(NameItem);
        productCategory.setText(category);
        productDesc.setText(itemDesc);
        productPrice.setText(itemPrice);




        if(itemStat.equals("active")){
            activeSwitch.setChecked(true);
        }else{
            activeSwitch.setChecked(false);
        }



        activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String itemStatus;
                if(isChecked){
                    //checked, Active

                    itemStatus = "active";

                    HashMap updateStatus = new HashMap<>();
                    updateStatus.put("status", itemStatus);



                    firestore.collection("Products").document(id).update(updateStatus).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(context, item.getItemName()+" Activated..", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                    bottomSheetDialog.dismiss();



                }else{
                    //unchecked, inactive
                    itemStatus = "inactive";
                    HashMap updateStatus = new HashMap<>();
                    updateStatus.put("status", itemStatus);



                    firestore.collection("Products").document(id).update(updateStatus).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(context, item.getItemName()+" Deactivated..", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    bottomSheetDialog.dismiss();


                }

            }

        });







        storage.getReference("Product_Images/"+item.getImage())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri)
                                .into(imgProduct);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        if(itemDisAvailable.equals("true")){
            productDisc.setVisibility(View.VISIBLE);
            productDiscNote.setVisibility(View.VISIBLE);
            productDisc.setText(itemDiscount);
            productDiscNote.setText(itemDisNote);
            productPrice.setTextSize(15);
            productPrice.setPaintFlags(productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else {
            productDisc.setVisibility(View.GONE);
            productDiscNote.setVisibility(View.GONE);
        }

        bottomSheetDialog.show();

        //edit click
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(context, ItemUpdateActivity.class);
                intent.putExtra("productId", id);
                context.startActivity(intent);

            }
        });

        //deleteClick
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show confirm delete dialog
                bottomSheetDialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you want to delete the product " +item.getItemName() +" ?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //delete
                               // deleteProduct(itemDocumentId);
                                /*firestore.collection("Products").document(itemDocumentId).delete();*/

                                deleteProduct(id);

                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //cancel
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });





    }

    private void deleteProduct(String id) {
        firestore.collection("Products").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, id+" successfully deleted!");
                        Toast.makeText(context, " Deleted Successfully..", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterProducts(this, filterList);
        }
        return filter;
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
