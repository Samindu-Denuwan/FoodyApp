package com.jiat.foodyapp.favourite;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.adapter.AdapterFavourite;
import com.jiat.foodyapp.model.FavModel;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class FavouriteFragment extends Fragment {


    public ArrayList<FavModel> favModels;

    private AdapterFavourite adapterFavourite;
    private RecyclerView favouriteRecycler;
    FirebaseStorage storage;
    private TextView noFavText;
    private ImageView noFavImage;


    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Favourites");
        storage = FirebaseStorage.getInstance();
        favouriteRecycler = view.findViewById(R.id.favRecycler);

        noFavImage = view.findViewById(R.id.noFavImg);
        noFavText = view.findViewById(R.id.noFavTv);

        noFavText.setVisibility(View.GONE);
        noFavImage.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadFav();
        if(favModels.size() == 0){
            noFavText.setVisibility(View.VISIBLE);
            noFavImage.setVisibility(View.VISIBLE);
        }else{
            noFavText.setVisibility(View.GONE);
            noFavImage.setVisibility(View.GONE);
        }
    }

    private void loadFav() {


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2,RecyclerView.VERTICAL, false );
        favouriteRecycler.setLayoutManager(gridLayoutManager);
        favModels = new ArrayList<>();
        EasyDB easyDB = EasyDB.init(getActivity(), "FAV_DB")
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
        while (res.moveToNext()){
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String disAvail = res.getString(5);
            String discount = res.getString(6);
            String disNote = res.getString(7);
            String img = res.getString(8);
            String stat = res.getString(9);



            FavModel favModel = new FavModel(""+id,
                    ""+pId,""+name,
                    ""+price,""+disAvail,
                    ""+discount,""+disNote,""+img,""+stat);

            favModels.add(favModel);




        }
        //set up Adapter
        adapterFavourite = new AdapterFavourite(getActivity(),favModels , storage);

        //set Recycler
        favouriteRecycler.setAdapter(adapterFavourite);
    }
}