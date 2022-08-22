package com.jiat.foodyapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.foodyapp.CartCounter;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.cart.CartFragment;
import com.jiat.foodyapp.model.CartItem;
import com.jiat.foodyapp.newCart.AppDatabase;
import com.jiat.foodyapp.newCart.CartProduct;
import com.jiat.foodyapp.newCart.ProductDao;

import java.util.ArrayList;
import java.util.List;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
   // Context context;
    //ArrayList<CartItem> cartItems;
    List<CartProduct> cartProducts;
    private FirebaseStorage  storage;
    TextView subTotalTv;




    public CartAdapter( List<CartProduct> cartProducts, TextView subTotalTv) {
        this.cartProducts = cartProducts;
        this.subTotalTv = subTotalTv;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout, parent, false);
        return new MyViewHolder(view);
    }

    private int quantity;
    public Double FinalPriceItem;
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        storage = FirebaseStorage.getInstance();


       /* //get Data
        CartItem cartItem = cartItems.get(position);
        String id = cartItem.getId();
        String productId = cartItem.getpId();
        String title = cartItem.getName();
        String price = cartItem.getPrice();
        String Qty = cartItem.getQuantity();


        storage.getReference("Product_Images/"+cartItem.getImage())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri)
                                .into(holder.productImg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //setData


        holder.productTitle.setText(""+title);
        holder.productPrice.setText("LKR "+price);
        holder.productQty.setText(""+Qty);

        quantity = Integer.parseInt(Qty);

        holder.incrementBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = Integer.parseInt((String) holder.productQty.getText());
                quantity++;
                holder.productQty.setText(""+quantity);
                FinalPriceItem = Double.parseDouble(price) * Integer.parseInt((String) holder.productQty.getText());
                holder.productFinalPrice.setText("LKR "+FinalPriceItem);
            }
        });

        holder.decrementBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = Integer.parseInt((String) holder.productQty.getText());
                if(quantity>1) {
                    quantity--;
                    holder.productQty.setText("" + quantity);
                    FinalPriceItem = Double.parseDouble(price) * Integer.parseInt((String) holder.productQty.getText());
                    holder.productFinalPrice.setText("LKR "+FinalPriceItem);
                }
            }
        });

        //int qty = Integer.parseInt((String) holder.productQty.getText());
        FinalPriceItem = Double.parseDouble(price) * Integer.parseInt((String) holder.productQty.getText());
        holder.productFinalPrice.setText("LKR "+FinalPriceItem);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyDB easyDB = EasyDB.init(context, "ITEM_DB")
                        .setTableName("ITEM_TABLE")
                        .addColumn(new Column("item_id", new String[]{"text", "unique"}))
                        .addColumn(new Column("item_pid", new String[]{"text", "not null"}))
                        .addColumn(new Column("item_name", new String[]{"text", "not null"}))
                        .addColumn(new Column("item_price", new String[]{"text", "not null"}))
                        .addColumn(new Column("item_qty", new String[]{"text", "not null"}))
                        .addColumn(new Column("item_image", new String[]{"text", "not null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1, id);
                Toast.makeText(context, "Removed from Cart...", Toast.LENGTH_SHORT).show();


                //refresh list
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();


            }
        });*/


        CartProduct cartProduct = cartProducts.get(position);
        String productId = String.valueOf(cartProduct.getPrimaryId());
        String title = cartProduct.getPname();
        String price = String.valueOf(cartProduct.getPrice());
        String Qty = String.valueOf(cartProduct.getQnt());

        holder.productTitle.setText(""+title);
        holder.productPrice.setText("LKR "+price);
        holder.productQty.setText(""+Qty);



        FinalPriceItem = Double.parseDouble(price) * Integer.parseInt((String) holder.productQty.getText());
        holder.productFinalPrice.setText("LKR "+FinalPriceItem);

        storage.getReference("Product_Images/"+cartProduct.getPimage())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(holder.productImg.getContext()).load(uri)
                                .into(holder.productImg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(holder.productImg.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase db = Room.databaseBuilder(subTotalTv.getContext(), AppDatabase.class, "cart_db")
                        .allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();
                productDao.deleteById(cartProducts.get(position).getPrimaryId());
                cartProducts.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();
                updatePrice();
            }
        });

        holder.incrementBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qnt = cartProducts.get(position).getQnt();
                qnt++;
                cartProducts.get(position).setQnt(qnt);

                try{
                    AppDatabase db = Room.databaseBuilder(subTotalTv.getContext(), AppDatabase.class, "cart_db")
                            .allowMainThreadQueries().build();
                    ProductDao productDao = db.ProductDao();
                    productDao.updateQty(cartProduct);
                }catch(SQLiteConstraintException e){
                    e.getMessage();

                }
               /* AppDatabase db = Room.databaseBuilder(subTotalTv.getContext(), AppDatabase.class, "cart_db")
                        .allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();*/
               // productDao.(cartProducts.get(position).getPrimaryId());
                notifyDataSetChanged();
                updatePrice();

            }
        });

        holder.decrementBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qnt = cartProducts.get(position).getQnt();
                if(qnt>1) {
                    qnt--;
                    cartProducts.get(position).setQnt(qnt);
                    try{
                        AppDatabase db = Room.databaseBuilder(subTotalTv.getContext(), AppDatabase.class, "cart_db")
                                .allowMainThreadQueries().build();
                        ProductDao productDao = db.ProductDao();
                        productDao.updateQty(cartProduct);
                    }catch(SQLiteConstraintException e){
                        e.getMessage();

                    }
                    notifyDataSetChanged();
                    updatePrice();

                }
            }
        });

    }

    private void updatePrice() {

            int sum = 0, i;
            for (i = 0; i < cartProducts.size(); i++) {
                sum = sum + (cartProducts.get(i).getPrice() * cartProducts.get(i).getQnt());

                subTotalTv.setText("LKR " + sum);


            }

        if(cartProducts.size() == 0){
            subTotalTv.setText("LKR 0" );
        }
        }




    @Override
    public int getItemCount() {


        return cartProducts.size();
    }

    public class  MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView productImg;
        private TextView productTitle, productQty, productPrice, productFinalPrice;
        private ImageButton delete, incrementBTN, decrementBTN;
        private CheckBox selectCheckbox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            productImg = itemView.findViewById(R.id.imgItem);
            productTitle = itemView.findViewById(R.id.titleItem);
            productQty = itemView.findViewById(R.id.qtyCart);
            productPrice = itemView.findViewById(R.id.price);
            productFinalPrice = itemView.findViewById(R.id.finalPrice);
            delete = itemView.findViewById(R.id.deleteBtn);
            incrementBTN = itemView.findViewById(R.id.increment);
            decrementBTN = itemView.findViewById(R.id.decrement);
            selectCheckbox = itemView.findViewById(R.id.checkBox);
        }
    }
}
