package com.bazzillion.ingrid.shelfie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bazzillion.ingrid.shelfie.Adapters.IngredientPickerAdapter;
import com.bazzillion.ingrid.shelfie.Database.Base;
import com.bazzillion.ingrid.shelfie.Database.Repository;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class PickIngredientFragment extends Fragment implements IngredientPickerAdapter.IngredientClickHandler {

    private static final String MOUTH = "mouth";
    private static final String SKIN = "skin";
    private static final String HAIR = "hair";
    private static final String KEY_MY_LIST = "myList";
    private static final String KEY_TYPES = "types";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PROPERTIES = "properties";
    private static final String KEY_SELECTION = "selection";
    private static final String KEY_PRESELECTED = "preselected";
    private static final String FIREBASE_KEY_INGREDIENT = "Ingredient";
    private static final String FIREBASE_KEY_PRODUCT = "product";
    private static final String FIREBASE_KEY_BASE = "base";
    private static final String FIREBASE_KEY_TYPE = "type";
    private static final String FIREBASE_KEY_FOR_PRODUCT = "forProduct";
    private static final String FIREBASE_KEY_SKIN_TYPE = "skinType";
    private Base selectedBase;
    private int isOptional;
    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView propertiesTextView;
    private String hairType;
    private String name;
    private String description;
    private String properties;
    private List<List<String>> ingredientLists = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private IngredientPickerAdapter ingredientPickerAdapter;
    private List<String> ingredientTypes = new ArrayList<>();
    private ValueEventListener typeEventListener;
    private int i;
    private Button confirmButton;
    private ArrayList<String> preselectedIngredients = new ArrayList<>();

//    private OnFragmentInteractionListener mListener;

    public PickIngredientFragment() {
        // Required empty public constructor
    }

    public static PickIngredientFragment newInstance(Base selectedBase, int isOptional, @Nullable List<String> preselectedIngredients) {
        PickIngredientFragment fragment = new PickIngredientFragment();
        Bundle args = new Bundle();
        args.putParcelable(Repository.KEY_BASE, selectedBase);
        args.putInt(Repository.KEY_IS_OPTIONAL, isOptional);
        if (preselectedIngredients != null){
            args.putStringArrayList(KEY_PRESELECTED, (ArrayList<String>) preselectedIngredients);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getArguments() != null) {
            selectedBase = getArguments().getParcelable(Repository.KEY_BASE);
            isOptional = getArguments().getInt(Repository.KEY_IS_OPTIONAL);
            if (getArguments().containsKey(KEY_PRESELECTED)){
                preselectedIngredients = getArguments().getStringArrayList(KEY_PRESELECTED);
            }

        } else {
            selectedBase = savedInstanceState.getParcelable(Repository.KEY_BASE);
            isOptional = savedInstanceState.getInt(Repository.KEY_IS_OPTIONAL);
            ingredientLists = (ArrayList<List<String>>) savedInstanceState.getSerializable(KEY_MY_LIST);
            ingredientTypes = savedInstanceState.getStringArrayList(KEY_TYPES);
        }

        hairType = getPreferenceByBodyPart(selectedBase.bodyPart);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_ingredient, container, false);

        recyclerView = view.findViewById(R.id.picker_recycler_view);
        descriptionTextView = view.findViewById(R.id.ingredient_description_tv);
        propertiesTextView = view.findViewById(R.id.properties_tv);
        nameTextView = view.findViewById(R.id.ingredient_name_tv);
        confirmButton = view.findViewById(R.id.confirm_button);


        updateRecyclerView();
        if (savedInstanceState == null && selectedBase != null) {
            List<String> addOnTypes;
            switch (isOptional) {
                case Repository.COMPULSORY_ADD_ON:
                    addOnTypes = selectedBase.compulsoryAddOns;
                    break;
                case Repository.OPTIONAL_ADD_ON:
                default:
                    addOnTypes = selectedBase.optionalAddOns;
                    break;
            }
            i = 0;
            Repository.getInstance(getContext()).getMatchingIngredients(i, addOnTypes, this, selectedBase.product, hairType);
        } else {
            ingredientPickerAdapter.setSelectedIngredients((List<String>)savedInstanceState.getSerializable(KEY_SELECTION));
            name = savedInstanceState.getString(KEY_NAME);
            description = savedInstanceState.getString(KEY_DESCRIPTION);
            properties = savedInstanceState.getString(KEY_PROPERTIES);
            if (name != null && description != null && properties != null) {
                nameTextView.setText(name);
                propertiesTextView.setText(properties);
                descriptionTextView.setText(description);
            }

        }
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedIngredients = (ArrayList<String>) ingredientPickerAdapter.getSelectedIngredients();
                Intent intent = new Intent();
                intent.putExtra(Repository.KEY_SELECTED_INGREDIENTS, selectedIngredients);

                if (getResources().getBoolean(R.bool.isTablet)){
                    getTargetFragment().onActivityResult(isOptional, RESULT_OK, intent);
                    getFragmentManager().beginTransaction().detach(PickIngredientFragment.this).commit();
                } else {
                    getActivity().setResult(RESULT_OK, intent);
                    getActivity().finish();
                }
            }
        });


        return view;
    }

    private void updateRecyclerView() {
        ingredientPickerAdapter = new IngredientPickerAdapter(getContext(), ingredientTypes, this);
        ingredientPickerAdapter.setIngredientLists(ingredientLists);
        ingredientPickerAdapter.setSelectedIngredients(preselectedIngredients);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setSaveEnabled(true);
        recyclerView.setSaveFromParentEnabled(true);
        recyclerView.setAdapter(ingredientPickerAdapter);
    }



    public void setMatchingIngredients(List<String> myList, List<String> addOnTypes){
        if (!myList.isEmpty()) {
            ingredientLists.add(myList);
            ingredientTypes.add(addOnTypes.get(i));

        }
        i++;
        if (i < addOnTypes.size()) {
            Repository.getInstance(getContext()).getMatchingIngredients(i, addOnTypes, this, selectedBase.product, hairType);
        } else {
            ingredientPickerAdapter.setIngredientLists(ingredientLists);
        }
    }


    private String getPreferenceByBodyPart(String bodyPart) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        switch (bodyPart) {
            case MOUTH:
                return null;
            case SKIN:
                return sharedPreferences.getString(getContext().getResources().getString(R.string.key_skin_type), "REGULAR SKIN");
            case HAIR:
            default:
                return sharedPreferences.getString(getContext().getResources().getString(R.string.key_hair_type), "REGULAR HAIR");
        }
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
////        databaseReference.removeEventListener(typeEventListener);
//
////        mListener = null;
//    }

    @Override
    public void onIngredientClick(String ingredientName) {
        Repository.getInstance(getContext()).getIngredientByName(this, ingredientName);
    }

    public void populateIngredientDetails(String name, String properties, String description){
        this.name = name;
        nameTextView.setText(this.name);
        this.description = description;
        descriptionTextView.setText(this.description);
        this.properties = properties;
        propertiesTextView.setText(this.properties);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Repository.KEY_BASE, selectedBase);
        outState.putInt(Repository.KEY_IS_OPTIONAL, isOptional);
        outState.putStringArrayList(KEY_TYPES, (ArrayList<String>) ingredientTypes);
        outState.putSerializable(KEY_MY_LIST, (ArrayList<List<String>>) ingredientLists);
        if (name != null) {
            outState.putString(KEY_NAME, name);
        }
        if (description != null) {
            outState.putString(KEY_DESCRIPTION, description);
        }
        if (properties != null) {
            outState.putString(KEY_PROPERTIES, properties);
        }
        outState.putSerializable(KEY_SELECTION, (ArrayList<String>) ingredientPickerAdapter.getSelectedIngredients());


    }

}
