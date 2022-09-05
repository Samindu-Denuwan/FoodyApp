package com.jiat.foodyapp.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.jiat.foodyapp.AdapterCategories;
import com.jiat.foodyapp.ItemUserViewActivity;
import com.jiat.foodyapp.ModelCategories;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.ViewItemSellerActivity;
import com.jiat.foodyapp.adapter.ItemAdapterUser;
import com.jiat.foodyapp.model.Item;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements AdapterCategories.CategoryClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }

    private ArrayList<ModelCategories> categories_m;
    private AdapterCategories adapterCategories;

    public ArrayList<Item> items;
    private ItemAdapterUser itemAdapterUser;

    private static final String TAG = "owner";
    RecyclerView category_recyclerView, itemRecycler;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    public String selectCategory;
    private ImageSlider imageSlider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        category_recyclerView = view.findViewById(R.id.category_recycler);
        itemRecycler = view.findViewById(R.id.item_recycler);
        imageSlider = view.findViewById(R.id.image_slider);
        firestore = FirebaseFirestore.getInstance();
        loadSlider();
        loadItems();
        return view;
    }

    private void loadItems() {
        items = new ArrayList<>();
        itemAdapterUser = new ItemAdapterUser(getActivity(), items, storage);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2,RecyclerView.VERTICAL, false );
        itemRecycler.setLayoutManager(gridLayoutManager);
        itemRecycler.setAdapter(itemAdapterUser);

        firestore.collection("Products").orderBy("itemName").whereEqualTo("discountAvailable", "true").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

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

        firestore.collection("Products").orderBy("itemName").whereEqualTo("discountAvailable", "true")
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

    private void loadSlider() {
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        firestore.collection("Slider_Images").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        slideModels.add(new SlideModel(documentSnapshot.getString("url"), ScaleTypes.FIT));
                        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                    }
                }else{
                    Toast.makeText(getActivity(), "Can't Load Images...", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        loadCategories();
    }

    private void loadCategories() {


        categories_m = new ArrayList<>();
        adapterCategories = new AdapterCategories(getActivity(), categories_m, storage, this::selectedCategory);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        category_recyclerView.setLayoutManager(linearLayoutManager);
        category_recyclerView.setAdapter(adapterCategories);

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
        Toast.makeText(getActivity(), selectCategory, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), ItemUserViewActivity.class);
        intent.putExtra("item_name", selectCategory);
        startActivity(intent);


    }
}