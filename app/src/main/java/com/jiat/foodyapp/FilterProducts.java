package com.jiat.foodyapp;

import android.widget.Filter;

import com.jiat.foodyapp.adapter.ItemAdapterUpdate;
import com.jiat.foodyapp.model.Item;

import java.util.ArrayList;
import java.util.Locale;

public class FilterProducts extends Filter {

    private ItemAdapterUpdate  itemAdapterUpdate;
    private ArrayList<Item> filterList;

    public FilterProducts(ItemAdapterUpdate itemAdapterUpdate, ArrayList<Item> filterList) {
        this.itemAdapterUpdate = itemAdapterUpdate;
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

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        itemAdapterUpdate.items = (ArrayList<Item>) results.values;
        itemAdapterUpdate.notifyDataSetChanged();

    }
}
