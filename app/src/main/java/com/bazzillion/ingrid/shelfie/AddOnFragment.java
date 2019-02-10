package com.bazzillion.ingrid.shelfie;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AddOnFragment extends Fragment {

    private static final String KEY_BASE_NAME = "base_name";
    protected static final String KEY_BASE = "base";
    protected static final String KEY_IS_OPTIONAL = "isOptional";
    protected static final int COMPULSORY_ADD_ON = 123;
    protected static final int OPTIONAL_ADD_ON = 234;
    private static final String KEY_DESCRITION = "description";
    private static final String KEY_PRIMARY_INGREDIENTS = "primary_ingredients";
    private static final String KEY_COMPULSORY_ADD_ONS = "compulsory_add_ons";
    private static final String KEY_OPTIONAL_ADD_ONS = "optional_add_ons";
    private static final String KEY_SHELF_LIFE = "shels_life";
    private static final String FIREBASE_KEY_BASE = "base";
    private static final String FIREBASE_KEY_INGREDIENT = "Ingredient";
    private String baseName;
    private Base selectedBase;
    public String description;
    public List<String> primaryIngredients;
    public List<String> compulsoryAddOns;
    public List<String> optionalAddOns;
    public String shelfLife;
    private TextView descriptionView;
    private ArrayAdapter<String> ingredientArrayAdapter;
    private ListView ingredientListView;
    private ListView addOnListView;
    private Button pickIngredientButton;
    private Button pickAddOnButton;
    private Button saveButton;
    private TextView shelfLifeTextView;
    private FirebaseDatabase firebaseDatabase;

    public AddOnFragment(){

    }

    public static AddOnFragment newInstance(String baseName) {
        Bundle args = new Bundle();
        args.putString(KEY_BASE_NAME, baseName);
        AddOnFragment fragment = new AddOnFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                baseName = getArguments().getString(KEY_BASE_NAME);
                firebaseDatabase.getReference().child(FIREBASE_KEY_BASE).child(baseName)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                selectedBase = dataSnapshot.getValue(Base.class);
                                if (selectedBase != null){
                                    selectedBase.name = dataSnapshot.getKey();
//                                    description = selectedBase.description;
//                                    primaryIngredients = selectedBase.primaryIngredients;
//                                    compulsoryAddOns = selectedBase.compulsoryAddOns;
//                                    optionalAddOns = selectedBase.optionalAddOns;
//                                    shelfLife = selectedBase.shelfLife;
                                    updateUi();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        } else {
            selectedBase = savedInstanceState.getParcelable(KEY_BASE);
//            baseName = savedInstanceState.getString(KEY_BASE_NAME);
//            description = savedInstanceState.getString(KEY_DESCRITION);
//            primaryIngredients = savedInstanceState.getStringArrayList(KEY_PRIMARY_INGREDIENTS);
//            compulsoryAddOns = savedInstanceState.getStringArrayList(KEY_COMPULSORY_ADD_ONS);
//            optionalAddOns = savedInstanceState.getStringArrayList(KEY_OPTIONAL_ADD_ONS);
//            shelfLife = savedInstanceState.getString(KEY_SHELF_LIFE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_on, container, false);
        descriptionView = view.findViewById(R.id.base_description_text_view);
        ingredientListView = view.findViewById(R.id.base_ingredient_list_view);
        pickIngredientButton = view.findViewById(R.id.pick_base_ingredient_button);
        addOnListView = view.findViewById(R.id.add_on_list_view);
        pickAddOnButton = view.findViewById(R.id.pick_add_on_button);
        saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        shelfLifeTextView = view.findViewById(R.id.shelf_life_text_view);
        if (savedInstanceState != null && selectedBase != null){
            updateUi();
        }
        return view;
    }

    private void updateUi(){
        descriptionView.setText(selectedBase.description);
        shelfLifeTextView.setText(getString(R.string.shelf_life, selectedBase.shelfLife));
        if (selectedBase.primaryIngredients != null && getContext() != null){
            ingredientArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
            ingredientArrayAdapter.addAll(selectedBase.primaryIngredients);
            pickIngredientButton.setVisibility(View.GONE);
        } else {
            pickIngredientButton.setVisibility(View.VISIBLE);
            pickIngredientButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPickIngredientFragment(selectedBase, COMPULSORY_ADD_ON);
                }
            });
        }
        ingredientListView.setAdapter(ingredientArrayAdapter);
        ArrayAdapter<String> addOnAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        pickAddOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPickIngredientFragment(selectedBase, OPTIONAL_ADD_ON);
            }
        });
    }

    private void startPickIngredientFragment(Base selectedBase, int isOptional){
        if (getResources().getBoolean(R.bool.isTablet)){
            getFragmentManager().beginTransaction().replace(R.id.pick_ingredient_fragment, PickIngredientFragment.newInstance(selectedBase, isOptional)).commit();
        } else {
            Intent intent = new Intent(getActivity(), PickIngredientActivity.class);
            intent.putExtra(KEY_BASE, selectedBase);
            intent.putExtra(KEY_IS_OPTIONAL, isOptional);
            startActivity(intent);
        }

    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (baseName != null){
            outState.putString(KEY_BASE_NAME, baseName);
        }
        outState.putParcelable(KEY_BASE, selectedBase);
//            outState.putString(KEY_DESCRITION, description);
//            outState.putStringArrayList(KEY_PRIMARY_INGREDIENTS, (ArrayList<String>) primaryIngredients);
//            outState.putStringArrayList(KEY_COMPULSORY_ADD_ONS, (ArrayList<String>) compulsoryAddOns);
//            outState.putStringArrayList(KEY_OPTIONAL_ADD_ONS, (ArrayList<String>) optionalAddOns);
//            outState.putString(KEY_SHELF_LIFE, shelfLife);
    }




}
