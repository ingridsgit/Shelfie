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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bazzillion.ingrid.shelfie.Adapters.IngredientPickerAdapter;
import com.bazzillion.ingrid.shelfie.Database.Base;
import com.bazzillion.ingrid.shelfie.Database.Repository;
import com.bazzillion.ingrid.shelfie.Utils.CrossAppFunctions;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private static final String KEY_MATCHING_SPECS = "matching_specs";
    private Base selectedBase;
    private int isOptional;
    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView propertiesTextView;
    private TextView matchingSpecTextView;
    private String matchingSpecs;
    private String hairOrSkinType;
    private String[] userSpecificities;
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
    private ProgressBar progressBar;

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
        CrossAppFunctions.checkNetworkState(getContext());
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

        hairOrSkinType = getPreferenceByBodyPart(selectedBase.bodyPart);
        userSpecificities = getSpecificitiesByBodyPart(selectedBase.bodyPart);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_ingredient, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.picker_recycler_view);
        descriptionTextView = view.findViewById(R.id.ingredient_description_tv);
        propertiesTextView = view.findViewById(R.id.properties_tv);
        nameTextView = view.findViewById(R.id.ingredient_name_tv);
        confirmButton = view.findViewById(R.id.confirm_button);
        matchingSpecTextView = view.findViewById(R.id.matching_specs_tv);


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
            Repository.getInstance(getContext()).getMatchingIngredients(i, addOnTypes, this, selectedBase.product, hairOrSkinType);
        } else {
            ingredientPickerAdapter.setSelectedIngredients((List<String>)savedInstanceState.getSerializable(KEY_SELECTION));
            name = savedInstanceState.getString(KEY_NAME);
            description = savedInstanceState.getString(KEY_DESCRIPTION);
            properties = savedInstanceState.getString(KEY_PROPERTIES);
            matchingSpecs = savedInstanceState.getString(KEY_MATCHING_SPECS);
            if (name != null && description != null && properties != null) {
                nameTextView.setText(name);
                propertiesTextView.setText(properties);
                descriptionTextView.setText(description);
            }
            if (matchingSpecs != null){
                matchingSpecTextView.setText(matchingSpecs);
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
            progressBar.setVisibility(View.GONE);

        }
        i++;
        if (i < addOnTypes.size()) {
            Repository.getInstance(getContext()).getMatchingIngredients(i, addOnTypes, this, selectedBase.product, hairOrSkinType);
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

    private String[] getSpecificitiesByBodyPart(String bodyPart) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        switch (bodyPart) {
            case MOUTH:
                return null;
            case SKIN:
                Set<String> skinSet = sharedPreferences.getStringSet(getContext().getResources().getString(R.string.key_skin_specificity), null);
                return skinSet.toArray(new String[0]);
            case HAIR:
            default:
                boolean dandruff = sharedPreferences.getBoolean(getContext().getResources().getString(R.string.key_dandruff), false);
                return dandruff ? new String[]{"Dandruff"} : null;
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
        // this method calls @populateIngredientDetails below
        Repository.getInstance(getContext()).getIngredientByName(this, ingredientName);
    }

    public void populateIngredientDetails(String name, String properties, String description, @Nullable Map<String, Boolean> specificities){
        this.name = name;
        nameTextView.setText(this.name);
        this.description = description;
        descriptionTextView.setText(this.description);
        this.properties = properties;
        propertiesTextView.setText(this.properties);
        StringBuilder matchingSpecBuilder = new StringBuilder();
        if (userSpecificities != null) {
            for (String specificity : userSpecificities) {
                if (specificities != null && specificities.containsKey(specificity)) {
                    String comma = matchingSpecBuilder.length() == 0 ? "Good for " : ", ";
                    matchingSpecBuilder.append(comma).append(specificity);
                    //TODO: when basefragment restarts after bug, PICK button is visible even though it shouldnt
                    // + remove excels sheets from github
                }
            }
        }
        matchingSpecs = matchingSpecBuilder.toString();
        if (matchingSpecs != null){
            matchingSpecTextView.setVisibility(View.VISIBLE);
            matchingSpecTextView.setText(matchingSpecs);
        } else {
            matchingSpecTextView.setVisibility(View.GONE);
        }
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
        if (matchingSpecs != null){
            outState.putString(KEY_MATCHING_SPECS, matchingSpecs);
        }
        outState.putSerializable(KEY_SELECTION, (ArrayList<String>) ingredientPickerAdapter.getSelectedIngredients());


    }

}
