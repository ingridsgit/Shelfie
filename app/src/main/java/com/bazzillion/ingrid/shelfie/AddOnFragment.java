package com.bazzillion.ingrid.shelfie;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bazzillion.ingrid.shelfie.Database.AppDatabase;
import com.bazzillion.ingrid.shelfie.Database.Recipe;
import com.bazzillion.ingrid.shelfie.Database.Repository;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AddOnFragment extends Fragment {

    private static final String KEY_BASE_NAME = "base_name";
    protected static final String KEY_BASE = "base";
    protected static final String KEY_IS_OPTIONAL = "isOptional";
    protected static final String KEY_SELECTED_INGREDIENTS = "selection";
    protected static final int COMPULSORY_ADD_ON = 123;
    protected static final int OPTIONAL_ADD_ON = 234;
    private static final String KEY_SELECTED_COMPULSORY = "selected_compulsory";
    private static final String KEY_SELECTED_OPTIONAL = "selected_optional";
    private static final String KEY_INGREDIENT_ADAPTER = "ingredient_adapter";
    private static final String KEY_ADD_ON_ADAPTER = "add_on_adapter";
    private static final String KEY_CURRENT_RECIPE = "current_recipe";
    private static final String KEY_DESCRITION = "description";
    private static final String KEY_PRIMARY_INGREDIENTS = "primary_ingredients";
    private static final String KEY_COMPULSORY_ADD_ONS = "compulsory_add_ons";
    private static final String KEY_OPTIONAL_ADD_ONS = "optional_add_ons";
    private static final String KEY_SHELF_LIFE = "shelf_life";
    private static final String FIREBASE_KEY_INGREDIENT = "Ingredient";
    private static final String SEPARATOR = "-,,,-";
    private static final int FRAGMENT_MODE_CREATE = 11;
    private static final int FRAGMENT_MODE_READ = 22;
    private static final int FRAGMENT_MODE_REWRITE = 33;
    private static final String KEY_FRAGMENT_MODE = "key_fragment_mode";
    private int fragmentMode;
    private String baseName;
    private Base selectedBase;
    public String description;
    public List<String> primaryIngredients;
    public List<String> compulsoryAddOns;
    public List<String> optionalAddOns;
    public String shelfLife;
    private TextView descriptionView;
    private ArrayAdapter<String> ingredientArrayAdapter;
    private ArrayAdapter<String> addOnAdapter;
    private ListView ingredientListView;
    private ListView addOnListView;
    private Button pickIngredientButton;
    private Button pickAddOnButton;
    private Button saveButton;
    private TextView shelfLifeTextView;
    private FirebaseDatabase firebaseDatabase;
    private List<String> selectedCompulsory = new ArrayList<>();
    private List<String> selectedOptional = new ArrayList<>();
    private AppDatabase appDatabase;
    private int recipeId;
    private LiveData<Recipe> currentRecipe;

    public AddOnFragment(){

    }

    // for Create
    public static AddOnFragment newInstance(String baseName) {
        Bundle args = new Bundle();
        args.putString(KEY_BASE_NAME, baseName);
        AddOnFragment fragment = new AddOnFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // for Read and Update
    public static AddOnFragment newInstance(String baseName, int recipeId) {
        Bundle args = new Bundle();
        args.putString(KEY_BASE_NAME, baseName);
        args.putInt(NewRecipeActivity.KEY_RECIPE_ID, recipeId);
        AddOnFragment fragment = new AddOnFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                if (getArguments().containsKey(NewRecipeActivity.KEY_RECIPE_ID)){ // activityMode == Update
                    fragmentMode = FRAGMENT_MODE_READ;
                    recipeId = getArguments().getInt(NewRecipeActivity.KEY_RECIPE_ID);
                    currentRecipe = Repository.getInstance(getContext()).getRecipeById(getActivity(), recipeId);
                    currentRecipe.observe(this, new Observer<Recipe>() {
                        @Override
                        public void onChanged(Recipe recipe) {
                            currentRecipe.removeObserver(this);
                            selectedCompulsory = convertStringToList(recipe.getPrimaryIngredients());
                            selectedOptional = convertStringToList(recipe.getAddOns());
                        }
                    });
                } else { // activityMode == Create
                    fragmentMode = FRAGMENT_MODE_CREATE;
                }

                // retrieve the Base from FirebaseDatabase with its name

                baseName = getArguments().getString(KEY_BASE_NAME);
                if (baseName != null){
                    Repository.getInstance(getContext()).retrieveSingleBase(baseName, this);
                } else {
                    Toast.makeText(getContext(), R.string.try_again, Toast.LENGTH_LONG).show();
                }


            }
        } else {
                fragmentMode = savedInstanceState.getInt(KEY_FRAGMENT_MODE);
                selectedBase = savedInstanceState.getParcelable(KEY_BASE);
                selectedCompulsory = savedInstanceState.getStringArrayList(KEY_SELECTED_COMPULSORY);
                selectedOptional = savedInstanceState.getStringArrayList(KEY_SELECTED_OPTIONAL);
//                ingredientListView.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_INGREDIENT_ADAPTER));
//            if (fragmentMode == FRAGMENT_MODE_READ || fragmentMode == FRAGMENT_MODE_REWRITE){
//                recipeId = savedInstanceState.getInt(NewRecipeActivity.KEY_RECIPE_ID);
//                currentRecipe = savedInstanceState.getParcelable(KEY_CURRENT_RECIPE);
//            }


        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_on, container, false);
        bindViews(view);
        switch (fragmentMode){
            case FRAGMENT_MODE_CREATE:
                setCreateUI();
                break;
            case FRAGMENT_MODE_READ:
                setReadUI();
                break;
            case FRAGMENT_MODE_REWRITE:
                setRewriteUi();
                break;
        }
        if (savedInstanceState != null && selectedBase != null){
            ingredientListView.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_INGREDIENT_ADAPTER));
            addOnListView.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_ADD_ON_ADAPTER));
            updateUi();
        }
        return view;
    }

    private void bindViews(View view){
        descriptionView = view.findViewById(R.id.base_description_text_view);
        ingredientListView = view.findViewById(R.id.base_ingredient_list_view);
        pickIngredientButton = view.findViewById(R.id.pick_base_ingredient_button);
        addOnListView = view.findViewById(R.id.add_on_list_view);
        pickAddOnButton = view.findViewById(R.id.pick_add_on_button);
        saveButton = view.findViewById(R.id.save_button);
        shelfLifeTextView = view.findViewById(R.id.shelf_life_text_view);
    }

    private void setRewriteUi(){
        pickIngredientButton.setVisibility(View.VISIBLE);
        pickAddOnButton.setVisibility(View.VISIBLE);
        saveButton.setText(R.string.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBase != null){
                    // check that the user has selected the compulsory ingredients
                    if (ingredientArrayAdapter.isEmpty()){
                        Toast.makeText(getContext(), R.string.please_select, Toast.LENGTH_LONG).show();
                    } else {
                        // get the list of all compulsory ingredients, either pre selected or selected by the user

                        final List<String> myPrimaryIngredients = new ArrayList<>();
                        if (selectedBase.primaryIngredients != null){
                            myPrimaryIngredients.addAll(selectedBase.primaryIngredients);
                        }
                        if (selectedCompulsory != null){
                            myPrimaryIngredients.addAll(selectedCompulsory);
                        }



                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        final EditText editText = new EditText(getContext());
                        final Recipe recipeToBeUpdated = currentRecipe.getValue();
                        editText.setText(recipeToBeUpdated.getName());
                        builder.setView(editText)
                                .setTitle(R.string.type_recipe_name)
                                .setCancelable(true)
                                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        String recipeName = editText.getText().toString().trim();
                                        recipeToBeUpdated.setName(recipeName);
                                        recipeToBeUpdated.setPrimaryIngredients(convertListToString(myPrimaryIngredients));
                                        String myAddOns = null;
                                        if (selectedOptional != null){
                                            myAddOns = convertListToString(selectedOptional);
                                        }
                                        recipeToBeUpdated.setAddOns(myAddOns);
                                        Repository.getInstance(getContext()).updateRecipe(currentRecipe.getValue());

                                        Toast.makeText(getContext(), getResources().getString(R.string.updated, recipeName) , Toast.LENGTH_LONG).show();
                                        getActivity().finish();
                                    }
                                }).create().show();

                    }

                } else {
                    Toast.makeText(getContext(), R.string.no_product, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setReadUI(){
        pickIngredientButton.setVisibility(View.GONE);
        pickAddOnButton.setVisibility(View.GONE);
        saveButton.setText(R.string.edit);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentMode = FRAGMENT_MODE_REWRITE;
                setRewriteUi();
            }
        });
    }

    private void setCreateUI(){
        pickIngredientButton.setVisibility(View.VISIBLE);
        pickAddOnButton.setVisibility(View.VISIBLE);
        saveButton.setText(R.string.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBase != null){
                    // check that the user has selected the compulsory ingredients
                    if (ingredientArrayAdapter.isEmpty()){
                        Toast.makeText(getContext(), R.string.please_select, Toast.LENGTH_LONG).show();
                    } else {
                        // get the list of all compulsory ingredients, either pre selected or selected by the user

                        final List<String> myPrimaryIngredients = new ArrayList<>();
                        if (selectedBase.primaryIngredients != null){
                            myPrimaryIngredients.addAll(selectedBase.primaryIngredients);
                        }
                        if (selectedCompulsory != null){
                            myPrimaryIngredients.addAll(selectedCompulsory);
                        }



                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        final EditText editText = new EditText(getContext());
                        builder.setView(editText)
                                .setTitle(R.string.type_recipe_name)
                                .setCancelable(true)
                                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String recipeName = editText.getText().toString().trim();
                                        saveNewRecipe(recipeName, myPrimaryIngredients);
                                        Toast.makeText(getContext(), getResources().getString(R.string.saved, recipeName) , Toast.LENGTH_LONG).show();
                                        getActivity().finish();
                                    }
                                }).create().show();

                    }

                } else {
                    Toast.makeText(getContext(), R.string.no_product, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void setSelectedBase(Base selectedBase) {
        this.selectedBase = selectedBase;
        updateUi();
    }

    private void updateUi(){
        descriptionView.setText(selectedBase.description);
        shelfLifeTextView.setText(getString(R.string.shelf_life, selectedBase.shelfLife));
        ingredientArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        if (getArguments() != null && getArguments().containsKey(NewRecipeActivity.KEY_RECIPE_ID)){ // activityMode == Update
            ingredientArrayAdapter.addAll(selectedCompulsory);
        } else if (selectedBase.primaryIngredients != null){ // activityMode == Create && there are primary ingredients
            ingredientArrayAdapter.addAll(selectedBase.primaryIngredients);
            if (!ingredientArrayAdapter.isEmpty()){
                pickIngredientButton.setVisibility(View.GONE);
            }

        } else {
            ingredientArrayAdapter.addAll(selectedCompulsory);
            pickIngredientButton.setVisibility(View.VISIBLE);
            pickIngredientButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPickIngredientFragment(selectedBase, COMPULSORY_ADD_ON);
                }
            });
        }
        ingredientListView.setAdapter(ingredientArrayAdapter);
        addOnAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        addOnListView.setAdapter(addOnAdapter);
        addOnAdapter.addAll(selectedOptional);
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
            startActivityForResult(intent, isOptional);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COMPULSORY_ADD_ON){
            if (resultCode == RESULT_OK){
                HashMap<Integer, String> hashMap = (HashMap<Integer, String>) data.getSerializableExtra(KEY_SELECTED_INGREDIENTS);
                selectedCompulsory.clear();
                selectedCompulsory.addAll(hashMap.values());
                ingredientArrayAdapter.clear();
                ingredientArrayAdapter.addAll(selectedCompulsory);
            }
        } else if (requestCode == OPTIONAL_ADD_ON){
            if (resultCode == RESULT_OK){
                HashMap<Integer, String> hashMap = (HashMap<Integer, String>) data.getSerializableExtra(KEY_SELECTED_INGREDIENTS);
                selectedOptional.clear();
                selectedOptional.addAll(hashMap.values());
                addOnAdapter.clear();
                addOnAdapter.addAll(selectedOptional);
            }
        }
    }

    private String convertListToString(List<String> list){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0 ; i <list.size() ; i++) {
            stringBuilder.append(list.get(i));
            // Do not append comma at the end of last element
            if( i < list.size() -1){
                stringBuilder.append(SEPARATOR);
            }
        }
        return stringBuilder.toString();
    }

    private List<String> convertStringToList(String string){
        String[] stringArray = StringUtils.split(string, SEPARATOR);
        return new ArrayList<>(Arrays.asList(stringArray));
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
            if (baseName != null){
                outState.putString(KEY_BASE_NAME, baseName);
            }
            outState.putInt(KEY_FRAGMENT_MODE, fragmentMode);
            outState.putParcelable(KEY_BASE, selectedBase);
            outState.putStringArrayList(KEY_SELECTED_COMPULSORY, (ArrayList<String>) selectedCompulsory);
            outState.putStringArrayList(KEY_SELECTED_OPTIONAL, (ArrayList<String>) selectedOptional);
        outState.putParcelable(KEY_INGREDIENT_ADAPTER, ingredientListView.onSaveInstanceState());
        outState.putParcelable(KEY_ADD_ON_ADAPTER, addOnListView.onSaveInstanceState());
//        if (fragmentMode == FRAGMENT_MODE_READ || fragmentMode == FRAGMENT_MODE_REWRITE){
//            outState.putInt(NewRecipeActivity.KEY_RECIPE_ID, recipeId);
//            outState.putParcelable(KEY_CURRENT_RECIPE, currentRecipe);
//        }

    }

    private void saveNewRecipe(String recipeName, List<String> myPrimaryIngredients){
        String myAddOns = null;
        if (selectedOptional != null){
            myAddOns = convertListToString(selectedOptional);
        }
        Recipe recipe = new Recipe(recipeName,
                baseName,
                convertListToString(myPrimaryIngredients),
                myAddOns);
        Repository.getInstance(getContext()).insertNewRecipe(recipe);
    }




}
