package com.bazzillion.ingrid.shelfie;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder> {

    private ArrayList<String> bases = new ArrayList<>();
    private DatabaseReference dbReference;
    private static final String FIREBASE_KEY_BASE = "base";
    private static final String FIREBASE_KEY_DESCRIPTION = "description";

    public BaseAdapter(DatabaseReference dbReference){
        this.dbReference = dbReference;
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
        dbReference.child(FIREBASE_KEY_BASE).child(baseName).child(FIREBASE_KEY_DESCRIPTION)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.descriptionView.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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

    public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameView;
        TextView descriptionView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.base_name_text_view);
            descriptionView = itemView.findViewById(R.id.base_description_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
