package com.bazzillion.ingrid.shelfie.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class MySimpleAdapter extends RecyclerView.Adapter<MySimpleAdapter.SimpleViewHolder> {

    List<String> ingredientsNames;

    public MySimpleAdapter(){
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflatedView = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SimpleViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        holder.ingredientNameView.setText(ingredientsNames.get(position));
    }

    @Override
    public int getItemCount() {
        if (ingredientsNames == null){
            return 0;
        } else {
            return ingredientsNames.size();
        }
    }

    public void setIngredientsNames(List<String> ingredientsNames) {
        this.ingredientsNames = ingredientsNames;
        notifyDataSetChanged();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        TextView ingredientNameView;

        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientNameView = itemView.findViewById(android.R.id.text1);
        }
    }

}
