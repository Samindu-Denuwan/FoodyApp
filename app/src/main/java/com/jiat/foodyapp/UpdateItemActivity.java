package com.jiat.foodyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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
import com.jiat.foodyapp.adapter.ItemAdapterUpdate;
import com.jiat.foodyapp.model.Item;

import java.util.ArrayList;


public class UpdateItemActivity extends AppCompatActivity {

    public ArrayList<Item> items;
    private ItemAdapterUpdate itemAdapterUpdate1;
    private static final String TAG = "owner";
    private RecyclerView updateRView;

    private EditText searchItems;
    FirebaseFirestore firestore;
    FirebaseStorage storage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);


        searchItems = findViewById(R.id.searchEt);
        updateRView = findViewById(R.id.update_recycler);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        loadItems();

        searchItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    itemAdapterUpdate1.getFilter().filter(s);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void loadItems() {
        items = new ArrayList<>();
        itemAdapterUpdate1 = new ItemAdapterUpdate(this, items, storage);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2,RecyclerView.VERTICAL, false );
        updateRView.setLayoutManager(gridLayoutManager);
        updateRView.setAdapter(itemAdapterUpdate1);

        firestore.collection("Products").orderBy("itemName").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                items.clear();
                for(QueryDocumentSnapshot snapshot : task.getResult()){
                    Item item = snapshot.toObject(Item.class);
                    //item.setId(snapshot.getId());
                    items.add(item);
                }
                itemAdapterUpdate1.notifyDataSetChanged();
            }
        });

        firestore.collection("Products").orderBy("itemName")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        items.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()){
                            Item item = snapshot.toObject(Item.class);
                            items.add(item);
                        }
                        itemAdapterUpdate1.notifyDataSetChanged();
                    }

                });
    }
}