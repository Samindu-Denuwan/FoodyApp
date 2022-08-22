package com.jiat.foodyapp;

import android.annotation.SuppressLint;
import android.widget.Filter;

import com.jiat.foodyapp.adapter.ItemAdapterUpdate;
import com.jiat.foodyapp.adapter.ItemAdapterUser;
import com.jiat.foodyapp.model.Item;

import java.util.ArrayList;

public class FilterProductsUser extends Filter {

    private ItemAdapterUser itemAdapterUser;
    private ArrayList<Item> filterList;

    public FilterProductsUser(ItemAdapterUser itemAdapterUser, ArrayList<Item> filterList) {
        this.itemAdapterUser = itemAdapterUser;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //validate data for search
        if(constraint != null && constraint.length()>0){
            constraint = constraint.toString().toUpperCase();

            ArrayList<Item> filterModel = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                //check by title
                if(filterList.get(i).getItemName().toUpperCase().contains(constraint) ||
                filterList.get(i).getCheckedCategory().toUpperCase().contains(constraint)){

                    //add filtered data to list
                filterModel.add(filterList.get(i));
                }

            }
            results.count = filterModel.size();
            results.values = filterModel;

        }else{
            //search is empty
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        itemAdapterUser.items = (ArrayList<Item>) results.values;
        itemAdapterUser.notifyDataSetChanged();

    }
}
