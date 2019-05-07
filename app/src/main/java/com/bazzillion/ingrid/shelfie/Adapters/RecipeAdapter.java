package com.bazzillion.ingrid.shelfie.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bazzillion.ingrid.shelfie.Database.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private LiveData<List<Recipe>> myRecipes;

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflatedView = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new RecipeViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = myRecipes.getValue().get(position);
        holder.nameView.setText(recipe.getName());

    }

    @Override
    public int getItemCount() {
        if (myRecipes == null || myRecipes.getValue() == null){
            return 0;
        } else {
            return myRecipes.getValue().size();
        }
    }

    public void setRecipes(LiveData<List<Recipe>> myRecipes){
        this.myRecipes = myRecipes;
        notifyDataSetChanged();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return myRecipes;
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(android.R.id.text1);
        }
    }
}
