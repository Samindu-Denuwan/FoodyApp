package com.jiat.foodyapp.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.adapter.ItemAdapterUpdate;
import com.jiat.foodyapp.adapter.ItemAdapterUser;
import com.jiat.foodyapp.model.Item;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }


    public ArrayList<Item> items;
    private ItemAdapterUser itemAdapterUser;
    private static final String TAG = "owner";
    private RecyclerView itemRecyclerView;

    private EditText searchItems;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchItems = view.findViewById(R.id.searchEt);
        itemRecyclerView = view.findViewById(R.id.item_recycler);
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
                    itemAdapterUser.getFilter().filter(s);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void loadItems() {
        items = new ArrayList<>();
        itemAdapterUser = new ItemAdapterUser(getActivity(), items, storage);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2,RecyclerView.VERTICAL, false );
        itemRecyclerView.setLayoutManager(gridLayoutManager);
        itemRecyclerView.setAdapter(itemAdapterUser);

        firestore.collection("Products").orderBy("itemName").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                items.clear();
                for(QueryDocumentSnapshot snapshot : task.getResult()){
                    Item item = snapshot.toObject(Item.class);
                    //item.setId(snapshot.getId());
                    items.add(item);
                }
                itemAdapterUser.notifyDataSetChanged();
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
                        itemAdapterUser.notifyDataSetChanged();
                    }

                });
    }
    }
