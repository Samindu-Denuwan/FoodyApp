package com.jiat.foodyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.foodyapp.adapter.ItemAdapter;
import com.jiat.foodyapp.model.Item;

import java.util.ArrayList;

public class ViewItemSellerActivity extends AppCompatActivity implements AdapterCategories.CategoryClickListener {

    private ArrayList<ModelCategories> categories_m;
    private AdapterCategories adapterCategories;

    private ArrayList<Item> items;
    private ItemAdapter itemAdapter;

    private static final String TAG = "owner";
    RecyclerView cat_recyclerView, item_recyclerView;

    FirebaseFirestore firestore;
    FirebaseStorage storage;
    private String selectCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_seller);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        item_recyclerView = findViewById(R.id.item_recycler);
        loadCategories();

    }

    private void loadItems() {



        items = new ArrayList<>();
        itemAdapter = new ItemAdapter(ViewItemSellerActivity.this, items, storage);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2,RecyclerView.VERTICAL, false );
        item_recyclerView.setLayoutManager(gridLayoutManager);
        item_recyclerView.setAdapter(itemAdapter);

        firestore.collection("Products").orderBy("itemName").whereEqualTo("checkedCategory",  selectCategory).whereEqualTo("status", "active").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                items.clear();
                for(QueryDocumentSnapshot snapshot : task.getResult()){
                    Item item = snapshot.toObject(Item.class);
                    items.add(item);
                }
                itemAdapter.notifyDataSetChanged();
            }
        });

        firestore.collection("Products").orderBy("itemName").whereEqualTo("checkedCategory",  selectCategory).whereEqualTo("status", "active")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        items.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()){
                            Item item = snapshot.toObject(Item.class);
                            items.add(item);
                        }
                        itemAdapter.notifyDataSetChanged();
                    }

                });
    }

    private void loadCategories() {

        cat_recyclerView = findViewById(R.id.category_recycler);

        categories_m = new ArrayList<>();
        adapterCategories = new AdapterCategories(ViewItemSellerActivity.this, categories_m, storage, this::selectedCategory);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewItemSellerActivity.this, RecyclerView.HORIZONTAL, false);
        cat_recyclerView.setLayoutManager(linearLayoutManager);
        cat_recyclerView.setAdapter(adapterCategories);

        firestore.collection("Categories").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                categories_m.clear();
                for(QueryDocumentSnapshot snapshot : task.getResult()){
                    ModelCategories modelCategories = snapshot.toObject(ModelCategories.class);
                    categories_m.add(modelCategories);
                }
                adapterCategories.notifyDataSetChanged();
            }
        });

        firestore.collection("Categories").orderBy("name")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        categories_m.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()){
                            ModelCategories modelCategories = snapshot.toObject(ModelCategories.class);
                            categories_m.add(modelCategories);
                        }
                        adapterCategories.notifyDataSetChanged();
                    }

                });
    }


    @Override
    public void selectedCategory(ModelCategories modelCategories) {
        selectCategory = modelCategories.getName();
        Toast.makeText(this, selectCategory, Toast.LENGTH_SHORT).show();

        loadItems();

    }
}