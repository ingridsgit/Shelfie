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
import com.bazzillion.ingrid.shelfie.Database.Base;
import com.bazzillion.ingrid.shelfie.Database.Recipe;
import com.bazzillion.ingrid.shelfie.Database.Repository;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.bazzillion.ingrid.shelfie.Database.Repository.COMPULSORY_ADD_ON;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_BASE;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_COMPULSORY_ADD_ONS;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_IS_OPTIONAL;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_OPTIONAL_ADD_ONS;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_SELECTED_INGREDIENTS;
import static com.bazzillion.ingrid.shelfie.Database.Repository.OPTIONAL_ADD_ON;

public class AddOnFragment extends Fragment {

    private static final String KEY_BASE_NAME = "base_name";
    private static final String KEY_SELECTED_COMPULSORY = "selected_compulsory";
    private static final String KEY_SELECTED_OPTIONAL = "selected_optional";
    private static final String KEY_PRIMARY_INGREDIENTS = "primary_ingredients";

    private static final String KEY_ADD_ON_ADAPTER = "add_on_adapter";
    private static final String KEY_INGREDIENT_ADAPTER = "ingredient_adapter";
    private static final String SEPARATOR = "-,,,-";
    private static final String KEY_FRAGMENT_MODE = "key_fragment_mode";
    private static final int FRAGMENT_MODE_CREATE = 11;
    private static final int FRAGMENT_MODE_READ = 22;
    private static final int FRAGMENT_MODE_REWRITE = 33;
    private int fragmentMode;
    private String baseName;
    private Base selectedBase;
    private TextView descriptionView;
    private ArrayAdapter<String> ingredientArrayAdapter;
    private ArrayAdapter<String> addOnAdapter;
    private ListView ingredientListView;
    private ListView addOnListView;
    private Button pickIngredientButton;
    private Button pickAddOnButton;
    private Button saveButton;
    private TextView shelfLifeTextView;
    private List<String> selectedCompulsory = new ArrayList<>();
    private List<String> selectedOptional = new ArrayList<>();
    private List<String> myPrimaryIngredients = new ArrayList<>();
    private int recipeId;
    private LiveData<Recipe> currentRecipe;
    private EditText editText;

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
        args.putInt(Repository.KEY_RECIPE_ID, recipeId);
        AddOnFragment fragment = new AddOnFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                if (getArguments().containsKey(Repository.KEY_RECIPE_ID)){ // activityMode == Update
                    fragmentMode = FRAGMENT_MODE_READ;
                    recipeId = getArguments().getInt(Repository.KEY_RECIPE_ID);
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
                    // this method calls the @setBase() method
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
                baseName = savedInstanceState.getString(KEY_BASE_NAME);
                myPrimaryIngredients = savedInstanceState.getStringArrayList(KEY_PRIMARY_INGREDIENTS);
//                ingredientListView.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_INGREDIENT_ADAPTER));
            if (fragmentMode == FRAGMENT_MODE_READ || fragmentMode == FRAGMENT_MODE_REWRITE){
                recipeId = savedInstanceState.getInt(Repository.KEY_RECIPE_ID);
                currentRecipe = Repository.getInstance(getContext()).getRecipeById(getActivity(), recipeId);
            }


        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_on, container, false);
        bindViews(view);
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
        if (selectedBase.compulsoryAddOns == null){
            pickIngredientButton.setVisibility(View.GONE);
        } else {
            pickIngredientButton.setVisibility(View.VISIBLE);
        }
        pickAddOnButton.setVisibility(View.VISIBLE);
        saveButton.setText(R.string.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Recipe recipeToBeUpdated = currentRecipe.getValue();
                editText = new EditText(getContext());
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
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
                };
              getSelectedIngredients(recipeToBeUpdated.getName(), onClickListener);
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
        if (selectedBase.compulsoryAddOns == null){
            pickIngredientButton.setVisibility(View.GONE);
        } else {
            pickIngredientButton.setVisibility(View.VISIBLE);
        }
        pickAddOnButton.setVisibility(View.VISIBLE);
        saveButton.setText(R.string.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText = new EditText(getContext());
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String recipeName = editText.getText().toString().trim();
                        saveNewRecipe(recipeName);
                        Toast.makeText(getContext(), getResources().getString(R.string.saved, recipeName), Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                };
                getSelectedIngredients(baseName, onClickListener);
            }
        });
    }

    private void getSelectedIngredients(String editTextPrefill, DialogInterface.OnClickListener onClickListener){
        if (selectedBase != null){
            // check that the user has selected the compulsory ingredients
            if (ingredientArrayAdapter.isEmpty()){
                Toast.makeText(getContext(), R.string.please_select, Toast.LENGTH_LONG).show();
            } else {
                // get the list of all compulsory ingredients, either pre selected or selected by the user

                myPrimaryIngredients = new ArrayList<>();
                if (selectedBase.primaryIngredients != null){
                    myPrimaryIngredients.addAll(selectedBase.primaryIngredients);
                }
                if (selectedCompulsory != null){
                    myPrimaryIngredients.addAll(selectedCompulsory);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                editText.setText(editTextPrefill);
                builder.setView(editText)
                        .setTitle(R.string.type_recipe_name)
                        .setCancelable(true)
                        .setPositiveButton(R.string.save, onClickListener).create().show();

            }

        } else {
            Toast.makeText(getContext(), R.string.no_product, Toast.LENGTH_LONG).show();
        }
    }

    public void setSelectedBase(Base selectedBase) {
        this.selectedBase = selectedBase;
        updateUi();
    }

    private void updateUi(){
        ingredientArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        ingredientListView.setAdapter(ingredientArrayAdapter);
        switch (fragmentMode){
            case FRAGMENT_MODE_CREATE:
                setCreateUI();
                if (selectedBase.primaryIngredients != null){
                    ingredientArrayAdapter.addAll(selectedBase.primaryIngredients);
                }
                if (selectedCompulsory != null){
                    ingredientArrayAdapter.addAll(selectedCompulsory);
                }
                break;
            case FRAGMENT_MODE_READ:
                setReadUI();
                ingredientArrayAdapter.addAll(selectedCompulsory);
                break;
            case FRAGMENT_MODE_REWRITE:
                setRewriteUi();
                ingredientArrayAdapter.addAll(selectedCompulsory);
                break;
        }
        descriptionView.setText(selectedBase.description);
        shelfLifeTextView.setText(getString(R.string.shelf_life, selectedBase.shelfLife));
        pickIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPickIngredientFragment(COMPULSORY_ADD_ON);
            }
        });
        addOnAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        addOnListView.setAdapter(addOnAdapter);
        addOnAdapter.addAll(selectedOptional);
        pickAddOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPickIngredientFragment(OPTIONAL_ADD_ON);
            }
        });
    }

    private void startPickIngredientFragment(int isOptional){
        if (getResources().getBoolean(R.bool.isTablet)){
            PickIngredientFragment pickIngredientFragment;
            if (isOptional == COMPULSORY_ADD_ON && !selectedCompulsory.isEmpty()){
                pickIngredientFragment = PickIngredientFragment.newInstance(selectedBase, isOptional, selectedCompulsory);
            } else if (isOptional == OPTIONAL_ADD_ON && !selectedOptional.isEmpty()){
                pickIngredientFragment = PickIngredientFragment.newInstance(selectedBase, isOptional, selectedOptional);
            } else {
                pickIngredientFragment = PickIngredientFragment.newInstance(selectedBase, isOptional, null);
            }

            pickIngredientFragment.setTargetFragment(this, isOptional);
            // TODO : envoyer les ingredients preselectionnes
            getFragmentManager().beginTransaction().replace(R.id.pick_ingredient_fragment,pickIngredientFragment ).commit();
        } else {
            Intent intent = new Intent(getActivity(), PickIngredientActivity.class);
            intent.putExtra(KEY_BASE, selectedBase);
            intent.putExtra(KEY_IS_OPTIONAL, isOptional);
            if (isOptional == COMPULSORY_ADD_ON && !selectedCompulsory.isEmpty()){
                intent.putExtra(KEY_COMPULSORY_ADD_ONS, (ArrayList<String>) selectedCompulsory);
            } else if (isOptional == OPTIONAL_ADD_ON && !selectedOptional.isEmpty()){
                intent.putExtra(KEY_OPTIONAL_ADD_ONS,(ArrayList<String>) selectedOptional);
            }
            startActivityForResult(intent, isOptional);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            HashMap<Integer, String> hashMap = (HashMap<Integer, String>) data.getSerializableExtra(KEY_SELECTED_INGREDIENTS);
            if (requestCode == COMPULSORY_ADD_ON){
                selectedCompulsory.clear();
                selectedCompulsory.addAll(hashMap.values());
                ingredientArrayAdapter.addAll(selectedCompulsory);

            } else if (requestCode == OPTIONAL_ADD_ON){
                selectedOptional.clear();
                selectedOptional.addAll(hashMap.values());
                addOnAdapter.clear();
                addOnAdapter.addAll(selectedOptional);

            }
        }
    }

    private void saveNewRecipe(String recipeName){
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
            outState.putStringArrayList(KEY_PRIMARY_INGREDIENTS, (ArrayList<String>) myPrimaryIngredients);
        if (fragmentMode == FRAGMENT_MODE_READ || fragmentMode == FRAGMENT_MODE_REWRITE){
            outState.putInt(Repository.KEY_RECIPE_ID, recipeId);
        }
    }

}
