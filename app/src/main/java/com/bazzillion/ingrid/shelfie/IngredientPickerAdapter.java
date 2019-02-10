package com.bazzillion.ingrid.shelfie;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class IngredientPickerAdapter extends RecyclerView.Adapter<IngredientPickerAdapter.PickerViewHolder> {

    private List<List<String>> ingredientLists;
    private Context context;
    private List<String> ingredientTypes;

    public IngredientPickerAdapter(Context context, List<String> ingredientTypes){
        this.context = context;
        this.ingredientTypes = ingredientTypes;
    }

    @NonNull
    @Override
    public PickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.ingredient_picker_list_item, parent, false);
        return new PickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickerViewHolder holder, int position) {
        holder.textView.setText(context.getResources().getString(R.string.select, ingredientTypes.get(position)).toUpperCase());

        List<String> ingredients = ingredientLists.get(position);
        for (String ingredient : ingredients){
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(ingredient);
            holder.radioGroup.addView(radioButton);
        }

        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

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

    public class PickerViewHolder extends RecyclerView.ViewHolder{

        RadioGroup radioGroup;
        TextView textView;

    public PickerViewHolder(View itemView) {
        super(itemView);
        radioGroup = itemView.findViewById(R.id.picker_radio_group);
        textView = itemView.findViewById(R.id.select_text_view);
    }
}
}
