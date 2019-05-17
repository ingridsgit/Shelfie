package com.bazzillion.ingrid.shelfie.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bazzillion.ingrid.shelfie.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder> {

    private ArrayList<String> bases = new ArrayList<>();
    private DatabaseReference dbReference;
    private static final String FIREBASE_KEY_BASE = "base";
    private static final String FIREBASE_KEY_DESCRIPTION = "description";
    private BaseClickHandler baseClickHandler;
    private ProgressBar progressBar;


    public BaseAdapter(BaseClickHandler baseClickHandler){
        this.baseClickHandler = baseClickHandler;
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflatedView = layoutInflater.inflate(R.layout.base_list_item, parent, false);
        return new BaseViewHolder(inflatedView);
    }


    @Override
    public void onBindViewHolder(@NonNull final BaseViewHolder holder, int position) {
        String baseName = bases.get(position);
        holder.nameView.setText(baseName);
//        holder.progressBar.setVisibility(View.VISIBLE);
    }


    public void setBases(ArrayList<String> bases){
        this.bases = bases;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if (bases == null){
            return 0;
        } else {
            return bases.size();
        }
    }


    public interface BaseClickHandler{
        void onBaseClick(String base);
    }



    public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameView;
        ProgressBar progressBar;


        public BaseViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.base_name_text_view);
//            progressBar = itemView.findViewById(R.id.base_progress_bar);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String clickedBase = bases.get(adapterPosition);
            baseClickHandler.onBaseClick(clickedBase);
        }

    }

}
