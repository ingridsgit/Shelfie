package com.bazzillion.ingrid.shelfie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PickIngredientFragment extends Fragment {

    private static final String MOUTH = "mouth";
    private static final String SKIN = "skin";
    private static final String HAIR = "hair";
    private static final String KEY_MY_LIST = "myList";
    private static final String KEY_TYPES = "types";
    private Base selectedBase;
    private int isOptional;
    private TextView textView;
    private String hairType;
    private List<List<String>> ingredientLists = new ArrayList<>();
    private RecyclerView recyclerView;
    private IngredientPickerAdapter ingredientPickerAdapter;
    private List<String> ingredientTypes = new ArrayList<>();
    private ValueEventListener typeEventListener;
    private  DatabaseReference databaseReference;
    private int i;

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AddOnFragment.KEY_BASE, selectedBase);
        outState.putInt(AddOnFragment.KEY_IS_OPTIONAL, isOptional);
        outState.putStringArrayList(KEY_TYPES, (ArrayList<String>) ingredientTypes);
        outState.putSerializable(KEY_MY_LIST, (ArrayList<List<String>>) ingredientLists);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_ingredient, container, false);
        recyclerView = view.findViewById(R.id.picker_recycler_view);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        updateUi();
        switch (isOptional) {
            case AddOnFragment.COMPULSORY_ADD_ON:
                if (savedInstanceState == null){
                    List<String> addOnTypes = selectedBase.compulsoryAddOns;
                    i = 0;
                    queryFirebaseBeforeUpdate(databaseReference, addOnTypes);
            }
                break;
            case AddOnFragment.OPTIONAL_ADD_ON:
            default:
                if (savedInstanceState == null){
                    Toast.makeText(getContext(), selectedBase.optionalAddOns.toString(), Toast.LENGTH_SHORT).show();
                    List<String> addOnTypes = selectedBase.optionalAddOns;
                    i = 0;
                    queryFirebaseBeforeUpdate(databaseReference, addOnTypes);
                                    }
                break;
        }
        return view;
    }

    private void updateUi(){
        ingredientPickerAdapter = new IngredientPickerAdapter(getContext(), ingredientTypes);
        ingredientPickerAdapter.setIngredientLists(ingredientLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(ingredientPickerAdapter);
    }

    private void queryFirebaseBeforeUpdate(final DatabaseReference databaseReference, final List<String> addOnTypes){
        databaseReference.child("type").child(addOnTypes.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> ingredients = (List<String>) dataSnapshot.getValue();
                if (ingredients != null){
                    databaseReference.child("Ingredient").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<String> myList = new ArrayList<>();
                            for (String ingredient : ingredients) {
                                boolean isForProduct = dataSnapshot.child(ingredient).child("forProduct").child(selectedBase.product).getValue() != null;
                                boolean isForUser;
                                if (hairType != null){
                                    isForUser = dataSnapshot.child(ingredient).child("skinType").child(hairType).getValue() != null;
                                } else {
                                    isForUser = true;
                                }

                                if (isForProduct && isForUser) {
                                    myList.add(ingredient);
                                }
                            }
                            if (!myList.isEmpty()){
                                ingredientLists.add(myList);
                                ingredientTypes.add(addOnTypes.get(i));

                            }
                            i++;
                            if (i < addOnTypes.size()){
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

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
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
    @Override
    public void onDetach() {
        super.onDetach();
//        databaseReference.removeEventListener(typeEventListener);

//        mListener = null;
    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
