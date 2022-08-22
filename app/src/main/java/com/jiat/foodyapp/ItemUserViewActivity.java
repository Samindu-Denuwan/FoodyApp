package com.jiat.foodyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

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
import com.jiat.foodyapp.adapter.ItemAdapterUser;
import com.jiat.foodyapp.model.Item;

import java.util.ArrayList;

public class ItemUserViewActivity extends AppCompatActivity {

    private String categoryName;
    private ArrayList<Item> items;
    private ItemAdapterUser itemAdapterUser;
    private static final String TAG = "owner";
    RecyclerView  item_recyclerView;
    TextView categoryTv;

    FirebaseFirestore firestore;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_user_view);

        categoryName = getIntent().getStringExtra("item_name");
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        item_recyclerView = findViewById(R.id.item_recycler);
        categoryTv = findViewById(R.id.nameCategory);
        loadItems();
    }

    private void loadItems() {

        items = new ArrayList<>();
        itemAdapterUser = new ItemAdapterUser(this, items, storage);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2,RecyclerView.VERTICAL, false );
        item_recyclerView.setLayoutManager(gridLayoutManager);
        item_recyclerView.setAdapter(itemAdapterUser);

        categoryTv.setText("- "+categoryName+" -");

        firestore.collection("Products").orderBy("itemName").whereEqualTo("checkedCategory", categoryName ).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                items.clear();
                for(QueryDocumentSnapshot snapshot : task.getResult()){
                    Item item = snapshot.toObject(Item.class);
                    items.add(item);
                }
                itemAdapterUser.notifyDataSetChanged();
            }
        });

        firestore.collection("Products").orderBy("itemName").whereEqualTo("checkedCategory",  categoryName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        items.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()){
                            Item item = snapshot.toObject(Item.class);
                            items.add(item);
                        }
                        itemAdapterUser.notifyDataSetChanged();
                    }

                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}