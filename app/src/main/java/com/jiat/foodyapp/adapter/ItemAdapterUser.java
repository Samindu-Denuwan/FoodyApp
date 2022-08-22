package com.jiat.foodyapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
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
import androidx.annotation.PluralsRes;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.HashMap;
import java.util.Random;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ItemAdapterUser extends RecyclerView.Adapter<ItemAdapterUser.viewHolderItem> implements Filterable {

    private static final String TAG = "owner";
    private Context context;
    public ArrayList<Item> items, filterList;
    private FirebaseStorage  storage;
    private FilterProductsUser filter;
    public String itemDocumentId ;
    private FirebaseFirestore  firestore;

    public int favId =1;
    public String proID;
    public String FAV_ID;




    public ItemAdapterUser(Context context, ArrayList<Item> items, FirebaseStorage storage) {
        this.context = context;
        this.items = items;
        this.storage = storage;
        this.filterList = items;


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
        Item item = items.get(position);

        proID = item.getProductId();
        String NameItem = item.getItemName();
        String itemPrice = item.getPrice();
        String itemDisAvailable = item.getDiscountAvailable();
        String itemDiscount = item.getDiscount();
        String itemDisNote = item.getDiscountNote();
        String itemStat = item.getStatus();


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
               /* String name = item.getItemName();
                Toast.makeText(context, "ID : "+itemDocumentId+" "+name, Toast.LENGTH_SHORT).show();*/
                detailsBottomSheet(item);


            }
        });



        holder.favCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.favCheckbox.isChecked()){
                    Random random = new Random();
                    favId = random.nextInt(200000);


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


                    Boolean b = easyDB.addData("fav_id", favId)
                            .addData("item_pid", item.getProductId())
                            .addData("item_name",NameItem)
                            .addData("item_price", itemPrice)
                            .addData("discount_available", itemDisAvailable)
                            .addData("item_discount", itemDiscount)
                            .addData("item_disNote", itemDisNote)
                            .addData("item_image", item.getImage())
                            .addData("item_status", itemStat)
                            .doneDataAdding();
                    Toast.makeText(context, "Add to Favourite List...", Toast.LENGTH_SHORT).show();

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



                    easyDB.deleteRow(1, FAV_ID);

                    Toast.makeText(context, " Remove from the Favourite...", Toast.LENGTH_SHORT).show();

                }
            }
        });


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


        //get all records from db
        Cursor res = easyDB.getAllData();
        while (res.moveToNext()) {
            FAV_ID = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String disAvail = res.getString(5);
            String discount = res.getString(6);
            String disNote = res.getString(7);
            String img = res.getString(8);
            String stat = res.getString(9);

            if (proID.equals(pId)) {
                    holder.favCheckbox.setChecked(true);
            }
        }





    }


    public int itemId =1;
    private int quantity  = 0;
    private void detailsBottomSheet(Item item) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.food_detail_user, null);
        bottomSheetDialog.setContentView(view);

        //init buttons
        ImageButton decrement = view.findViewById(R.id.decrementBtn);
        ImageButton increment= view.findViewById(R.id.incrementBtn);
        TextView qty = view.findViewById(R.id.qtyTv);

        ImageView imgProduct = view.findViewById(R.id.foodImg);
        ImageView notAvailable = view.findViewById(R.id.notAvailableImg);

        TextView productName = view.findViewById(R.id.item_name);
        TextView productCategory = view.findViewById(R.id.category);
        TextView productDesc = view.findViewById(R.id.itemDetails);
        TextView productPrice = view.findViewById(R.id.foodPrice);
        TextView productDisc = view.findViewById(R.id.disPrice);
        TextView productDiscNote = view.findViewById(R.id.disNote);
        CheckBox favourite = view.findViewById(R.id.checkBoxFav);
        TextView star = view.findViewById(R.id.starMark);
        Button addToCart = view.findViewById(R.id.btnAddToCart);
        View qtyLayout = view.findViewById(R.id.layoutQty);

        //get data
        String newPrice;

        String id = item.getProductId();
        String NameItem = item.getItemName();
        String itemDesc = item.getDetails();
        String itemPrice = item.getPrice();
        String itemDisAvailable = item.getDiscountAvailable();
        String itemDiscount = item.getDiscount();
        String itemDisNote = item.getDiscountNote();
        String itemStat = item.getStatus();
        String category = item.getCheckedCategory();
        quantity = 1;
        String ItemImage = item.getImage();

        if(itemDisAvailable.equals("true")){
            newPrice = itemDiscount;
        }else{
            newPrice = itemPrice;
        }

        //set data

        productName.setText(NameItem);
        productCategory.setText(category);
        productDesc.setText(itemDesc);
        productPrice.setText("LKR "+itemPrice);
        qty.setText(""+quantity);




        if(itemStat.equals("active")){
           notAvailable.setVisibility(View.INVISIBLE);

        }else{
            notAvailable.setVisibility(View.VISIBLE);
            addToCart.setVisibility(View.INVISIBLE);
            addToCart.setVisibility(View.INVISIBLE);
            qtyLayout.setVisibility(View.INVISIBLE);
        }


        storage.getReference("Product_Images/"+item.getImage())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri).circleCrop()
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
            productDisc.setText("LKR "+itemDiscount);
            productDiscNote.setText(itemDisNote);
            productPrice.setTextSize(20);
            productPrice.setPaintFlags(productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else {
            productDisc.setVisibility(View.INVISIBLE);
            productDiscNote.setVisibility(View.INVISIBLE);
        }
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                qty.setText(""+quantity);
            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantity>1) {
                    quantity--;
                    qty.setText(""+quantity);
                }
            }
        });

        //add to db sqlite

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String Qty = qty.getText().toString().trim();
               // addtoCart(id, NameItem, price, Qty, ItemImage);
                //Toast.makeText(context, "Added "+id+""+NameItem+""+price+""+Qty , Toast.LENGTH_SHORT).show();


                Random random = new Random();
                itemId = random.nextInt(200000);

                AppDatabase db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "cart_db")
                        .allowMainThreadQueries().fallbackToDestructiveMigration().build();
                ProductDao productDao = db.ProductDao();
                Boolean check = productDao.is_exist(id);
                if(check== false){
                   /* int pid = Integer.parseInt(id);*/
                    int pid = itemId;
                    String productID = id;
                    String pname = NameItem;
                    int price = Integer.parseInt(newPrice);
                    int qnt = Integer.parseInt(Qty);
                    String pimage = ItemImage;

                    productDao.insertrecord(new CartProduct(pid,productID, pname, price, qnt, pimage));

                    Toast.makeText(context, "Added to Cart...", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(context, "Product Already in Cart...", Toast.LENGTH_SHORT).show();
                }
                bottomSheetDialog.dismiss();
                //Toast.makeText(context, "Added "+id+""+NameItem+""+price+"" , Toast.LENGTH_SHORT).show();
            }
        });


        bottomSheetDialog.show();


    }





    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterProductsUser(this, filterList);
        }

        return filter;
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
