package com.jiat.foodyapp.adapter;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.newCart.AppDatabase;
import com.jiat.foodyapp.newCart.CartProduct;
import com.jiat.foodyapp.newCart.ProductDao;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.MyViewHolder> {
   // Context context;
    //ArrayList<CartItem> cartItems;
    List<CartProduct> cartProducts;
    private FirebaseStorage  storage;
    TextView subTotalTv;




    public CheckoutAdapter(List<CartProduct> cartProducts, TextView subTotalTv) {
        this.cartProducts = cartProducts;
        this.subTotalTv = subTotalTv;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_cart_layout, parent, false);
        return new MyViewHolder(view);
    }

    private int quantity;
    public Double FinalPriceItem;
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        storage = FirebaseStorage.getInstance();




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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            productImg = itemView.findViewById(R.id.imgItem);
            productTitle = itemView.findViewById(R.id.titleItem);
            productQty = itemView.findViewById(R.id.Cartqty);
            productPrice = itemView.findViewById(R.id.CartPrice);
            productFinalPrice = itemView.findViewById(R.id.finalPrice);

        }
    }
}
