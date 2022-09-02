package com.jiat.foodyapp.seller.filterOrder;

import android.annotation.SuppressLint;
import android.widget.Filter;
import android.widget.TextView;

import com.jiat.foodyapp.seller.adapter.AdapterOrderSeller;
import com.jiat.foodyapp.seller.adapter.AdapterSellerEarning;
import com.jiat.foodyapp.seller.model.ModelOrderSeller;

import java.util.ArrayList;

public class FilterEarnSeller extends Filter {

    private AdapterSellerEarning adapterSellerEarning;
    private ArrayList<ModelOrderSeller> filterList;
    TextView totalTv;

    public FilterEarnSeller(AdapterSellerEarning adapterSellerEarning, ArrayList<ModelOrderSeller> filterList, TextView totalTv) {
        this.adapterSellerEarning = adapterSellerEarning;
        this.filterList = filterList;
        this.totalTv = totalTv;

    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //validate data for search
        if(constraint != null && constraint.length()>0){
            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelOrderSeller> filterModel = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                //check by title
                if(filterList.get(i).getOrderStatus().toUpperCase().contains(constraint)){
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
        adapterSellerEarning.orderSellerArrayList = (ArrayList<ModelOrderSeller>) results.values;
        adapterSellerEarning.notifyDataSetChanged();

    }
}
