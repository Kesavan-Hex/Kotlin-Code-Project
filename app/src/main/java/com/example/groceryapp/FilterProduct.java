package com.example.groceryapp;

import android.widget.Filter;

import com.example.groceryapp.adapters.AdapterProductSeller;
import com.example.groceryapp.models.ModelProduct;

import java.util.ArrayList;

public class FilterProduct extends Filter {


    private AdapterProductSeller adapter;
    private ArrayList<ModelProduct> filterList;


    public FilterProduct(AdapterProductSeller adapter, ArrayList<ModelProduct> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {

        FilterResults results = new FilterResults();
        if(charSequence != null && charSequence.length() > 0){
            charSequence=charSequence.toString().toUpperCase();
            ArrayList<ModelProduct> filteredModels = new ArrayList<>();
            for (int i=0;i<filterList.size();i++){
                if(filterList.get(i).getProductTitle().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getProductCategory().toUpperCase().contains(charSequence)){
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;

        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            adapter.productList= (ArrayList<ModelProduct>) filterResults.values;
            //refresh adapter
            adapter.notifyDataSetChanged();
    }
}
