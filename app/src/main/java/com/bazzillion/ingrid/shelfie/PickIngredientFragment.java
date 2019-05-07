package com.bazzillion.ingrid.shelfie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private static final String FIREBASE_KEY_TYPE = "type";
    private static final String FIREBASE_KEY_INGREDIENT = "Ingredient";
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
    private DatabaseReference databaseReference;
    private int i;
    private Button confirmButton;

//    private OnFragmentInteractionListener mListener;

    public PickIngredientFragment() {
        // Required empty public constructor
    }

    public static PickIngredientFragment newInstance(Base selectedBase, int isOptional) {
        PickIngredientFragment fragment = new PickIngredientFragment();
        Bundle args = new Bundle();
        args.putParcelable(AddOnFragment.KEY_BASE, selectedBase);
        args.putInt(AddOnFragment.KEY_IS_OPTIONAL, isOptional);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getArguments() != null) {
            selectedBase = getArguments().getParcelable(AddOnFragment.KEY_BASE);
            isOptional = getArguments().getInt(AddOnFragment.KEY_IS_OPTIONAL);

        } else {
            selectedBase = savedInstanceState.getParcelable(AddOnFragment.KEY_BASE);
            isOptional = savedInstanceState.getInt(AddOnFragment.KEY_IS_OPTIONAL);
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

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        updateRecyclerView();
        if (savedInstanceState == null && selectedBase != null) {
            List<String> addOnTypes;
            switch (isOptional) {
                case AddOnFragment.COMPULSORY_ADD_ON:
                    addOnTypes = selectedBase.compulsoryAddOns;
                    i = 0;
                    queryFirebaseBeforeUpdate(databaseReference, addOnTypes);
                    break;
                case AddOnFragment.OPTIONAL_ADD_ON:
                default:
                    addOnTypes = selectedBase.optionalAddOns;
                    i = 0;
                    queryFirebaseBeforeUpdate(databaseReference, addOnTypes);
                    break;
            }
        } else {
            ingredientPickerAdapter.setSelectedIngredients((Map<Integer, String>)savedInstanceState.getSerializable(KEY_SELECTION));
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
                Intent data = new Intent();
                data.putExtra(AddOnFragment.KEY_SELECTED_INGREDIENTS, (HashMap<Integer, String>) ingredientPickerAdapter.getSelectedIngredients());
                getActivity().setResult(RESULT_OK, data);
                getActivity().finish();
            }
        });


        return view;
    }

    private void updateRecyclerView() {
        ingredientPickerAdapter = new IngredientPickerAdapter(getContext(), ingredientTypes, this);
        ingredientPickerAdapter.setIngredientLists(ingredientLists);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setSaveEnabled(true);
        recyclerView.setSaveFromParentEnabled(true);
        recyclerView.setAdapter(ingredientPickerAdapter);
    }


    private void queryFirebaseBeforeUpdate(final DatabaseReference databaseReference, final List<String> addOnTypes) {
        databaseReference.child(FIREBASE_KEY_TYPE).child(addOnTypes.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> ingredients = (List<String>) dataSnapshot.getValue();
                if (ingredients != null) {
                    databaseReference.child(FIREBASE_KEY_INGREDIENT).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<String> myList = new ArrayList<>();
                            for (String ingredient : ingredients) {
                                boolean isForProduct = dataSnapshot.child(ingredient).child("forProduct").child(selectedBase.product).getValue() != null;
                                boolean isForUser;
                                if (hairType != null) {
                                    isForUser = dataSnapshot.child(ingredient).child("skinType").child(hairType).getValue() != null;
                                } else {
                                    isForUser = true;
                                }

                                if (isForProduct && isForUser) {
                                    myList.add(ingredient);
                                }
                            }
                            if (!myList.isEmpty()) {
                                ingredientLists.add(myList);
                                ingredientTypes.add(addOnTypes.get(i));

                            }
                            i++;
                            if (i < addOnTypes.size()) {
                                queryFirebaseBeforeUpdate(databaseReference, addOnTypes);
                            } else {
                                ingredientPickerAdapter.setIngredientLists(ingredientLists);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
    public void onIngredientClick(String ingredient) {

        databaseReference.child(FIREBASE_KEY_INGREDIENT).child(ingredient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                nameTextView.setText(name);
                description = dataSnapshot.child("description").getValue().toString();
                descriptionTextView.setText(description);
                properties = dataSnapshot.child("properties").getValue().toString();
                propertiesTextView.setText(properties);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AddOnFragment.KEY_BASE, selectedBase);
        outState.putInt(AddOnFragment.KEY_IS_OPTIONAL, isOptional);
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
        outState.putSerializable(KEY_SELECTION, (HashMap<Integer, String>) ingredientPickerAdapter.getSelectedIngredients());


    }

}
