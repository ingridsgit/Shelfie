package com.bazzillion.ingrid.shelfie;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngredientPickerAdapter extends RecyclerView.Adapter<IngredientPickerAdapter.PickerViewHolder> {

    private List<List<String>> ingredientLists;
    private Context context;
    private List<String> ingredientTypes;
    private IngredientClickHandler ingredientClickHandler;
    private Map<Integer, String> selectedIngredients = new HashMap<>();

    public IngredientPickerAdapter(Context context, List<String> ingredientTypes, IngredientClickHandler ingredientClickHandler){
        this.context = context;
        this.ingredientTypes = ingredientTypes;
        this.ingredientClickHandler = ingredientClickHandler;
    }

    @NonNull
    @Override
    public PickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.ingredient_picker_list_item, parent, false);
        return new PickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PickerViewHolder holder, int position) {
        holder.textView.setText(context.getResources().getString(R.string.select, ingredientTypes.get(position)).toUpperCase());
        holder.radioGroup.setSaveFromParentEnabled(true);
        holder.radioGroup.setSaveEnabled(true);

        List<String> ingredients = ingredientLists.get(position);
        for (final String ingredient : ingredients){
            final RadioButton radioButton = new RadioButton(context);
            radioButton.setText(ingredient);
            radioButton.setSaveEnabled(true);
            radioButton.setSaveFromParentEnabled(true);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ingredientClickHandler.onIngredientClick(ingredient);
                    selectedIngredients.put(holder.getAdapterPosition(), ingredient);
                }
            });
            holder.radioGroup.addView(radioButton);
            String checkedButton = selectedIngredients.get(holder.getAdapterPosition());
            if (radioButton.getText().equals(checkedButton)){
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
        }
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.radioGroup.clearCheck();
                selectedIngredients.remove(String.valueOf(holder.getAdapterPosition()));
            }
        });



    }

    @Override
    public int getItemCount() {
        if (ingredientLists == null){
            return 0;
        } else {
            return ingredientLists.size();
        }
    }

    public void setIngredientLists(List<List<String>> ingredientLists){
        this.ingredientLists = ingredientLists;
        notifyDataSetChanged();
    }

    public Map<Integer, String> getSelectedIngredients(){
        return selectedIngredients;
    }

    public void setSelectedIngredients(Map<Integer, String> selectedIngredients){
        this.selectedIngredients = selectedIngredients;
    }

    public interface IngredientClickHandler{
        void onIngredientClick(String ingredient);
    }

    public class PickerViewHolder extends RecyclerView.ViewHolder {

        private final RadioGroup radioGroup;
        private final TextView textView;
        private final Button button;

        PickerViewHolder(View itemView) {
            super(itemView);
            radioGroup = itemView.findViewById(R.id.picker_radio_group);
            textView = itemView.findViewById(R.id.select_text_view);
            button = itemView.findViewById(R.id.clear_button);
            itemView.setSaveEnabled(true);
            itemView.setSaveFromParentEnabled(true);
    }

    }
}
