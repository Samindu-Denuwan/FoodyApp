package com.jiat.foodyapp.adapter;

import android.content.Context;
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
import com.jiat.foodyapp.ModelCategories;
import com.jiat.foodyapp.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.HolderProductCategories> {

    private Context context;
    private ArrayList<ModelCategories> categories;
    private FirebaseStorage storage;





    public CategoryAdapter(Context context, ArrayList<ModelCategories> categories, FirebaseStorage storage) {
        this.context = context;
        this.categories = categories;
        this.storage = storage;

    }


    @NonNull
    @Override
    public HolderProductCategories onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //layout inflate
        View view = LayoutInflater.from(context).inflate(R.layout.food_item_category_layout, parent, false);
        return new HolderProductCategories(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductCategories holder, int position) {
        //get Data
        ModelCategories modelCategories = categories.get(position);
        holder.categoryName.setText(modelCategories.getName());

        storage.getReference("Categories/"+modelCategories.getImage())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context).load(uri)
                                .into(holder.categoryImage);
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
        return categories.size();
    }


    class HolderProductCategories extends RecyclerView.ViewHolder{

        private ImageView categoryImage;
        private TextView categoryName;


        public HolderProductCategories(@NonNull View itemView) {
            super(itemView);

            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);


        }
    }
}
