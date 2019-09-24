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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bazzillion.ingrid.shelfie.Adapters.MySimpleAdapter;
import com.bazzillion.ingrid.shelfie.Database.Base;
import com.bazzillion.ingrid.shelfie.Database.Recipe;
import com.bazzillion.ingrid.shelfie.Database.Repository;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.bazzillion.ingrid.shelfie.Database.Repository.COMPULSORY_ADD_ON;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_BASE;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_COMPULSORY_ADD_ONS;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_IS_OPTIONAL;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_OPTIONAL_ADD_ONS;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_RECIPE_MODE;
import static com.bazzillion.ingrid.shelfie.Database.Repository.KEY_SELECTED_INGREDIENTS;
import static com.bazzillion.ingrid.shelfie.Database.Repository.OPTIONAL_ADD_ON;
import static com.bazzillion.ingrid.shelfie.Database.Repository.RecipeMode;



public class AddOnFragment extends Fragment {


    private RecipeMode recipeMode;

    private static final String KEY_BASE_NAME = "base_name";
    private static final String KEY_SELECTED_COMPULSORY = "selected_compulsory";
    private static final String KEY_SELECTED_OPTIONAL = "selected_optional";
    private static final String KEY_COMPULSORY_INGREDIENTS = "compulsory_ingredients";
    private static final String KEY_ADD_ON_ADAPTER = "add_on_adapter";
    private static final String KEY_INGREDIENT_ADAPTER = "ingredient_adapter";
    private static final String SEPARATOR = "-,,,-";

    private TextView descriptionView;
    private MySimpleAdapter ingredientAdapter;
    private MySimpleAdapter addOnAdapter;
    private RecyclerView ingredientRecyclerView;
    private RecyclerView addOnRecyclerView;
    private Button pickIngredientButton;
    private Button pickAddOnButton;
    private Button saveButton;
    private TextView shelfLifeTextView;
    private EditText editText;
    private TextView addOnTextView;
    private String baseName;
    private Base selectedBase;
    private int recipeId;
    private LiveData<Recipe> currentRecipe;
    private List<String> selectedCompulsory = new ArrayList<>();
    private List<String> selectedOptional = new ArrayList<>();

    public AddOnFragment(){

    }

    public static AddOnFragment newInstance(String baseName, int recipeId, RecipeMode recipeMode) {
        Bundle args = new Bundle();
        args.putString(KEY_BASE_NAME, baseName);
        args.putInt(Repository.KEY_RECIPE_ID, recipeId);
        args.putSerializable(KEY_RECIPE_MODE, recipeMode);
        AddOnFragment fragment = new AddOnFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getArguments() != null) {
            recipeMode = (RecipeMode) getArguments().getSerializable(KEY_RECIPE_MODE);
                if (recipeMode == RecipeMode.Read){
                    recipeId = getArguments().getInt(Repository.KEY_RECIPE_ID);
                    currentRecipe = Repository.getInstance(getContext()).getRecipeById(getActivity(), recipeId);
                    currentRecipe.observe(this, new Observer<Recipe>() {
                        @Override
                        public void onChanged(Recipe recipe) {
                            currentRecipe.removeObserver(this);
                            selectedCompulsory = convertStringToList(recipe.getCompulsoryAddOns());
                            selectedOptional = convertStringToList(recipe.getOptionalAddOns());
                        }
                    });
                }
                // retrieve the Base from FirebaseDatabase with its name
                baseName = getArguments().getString(KEY_BASE_NAME);
                if (baseName != null){
                    // this method calls the @setBase() method
                    Repository.getInstance(getContext()).retrieveSingleBase(baseName, this);
                } else {
                    Toast.makeText(getContext(), R.string.try_again, Toast.LENGTH_LONG).show();
                }
        } else {
                recipeMode = (RecipeMode) savedInstanceState.getSerializable(KEY_RECIPE_MODE);
                selectedBase = savedInstanceState.getParcelable(KEY_BASE);
                selectedCompulsory = savedInstanceState.getStringArrayList(KEY_SELECTED_COMPULSORY);
                selectedOptional = savedInstanceState.getStringArrayList(KEY_SELECTED_OPTIONAL);
                baseName = savedInstanceState.getString(KEY_BASE_NAME);
//                ingredientRecyclerView.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_INGREDIENT_ADAPTER));
            if (recipeMode == RecipeMode.Read || recipeMode == RecipeMode.Rewrite){
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
//            ingredientRecyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(KEY_INGREDIENT_ADAPTER));
//            addOnRecyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(KEY_ADD_ON_ADAPTER));
            updateUi();
        }
        return view;
    }

    private void bindViews(View view){
        descriptionView = view.findViewById(R.id.base_description_text_view);
        ingredientRecyclerView = view.findViewById(R.id.base_ingredient_recycler_view);
        pickIngredientButton = view.findViewById(R.id.pick_base_ingredient_button);
        addOnRecyclerView = view.findViewById(R.id.add_on_recycler_view);
        pickAddOnButton = view.findViewById(R.id.pick_add_on_button);
        saveButton = view.findViewById(R.id.save_button);
        shelfLifeTextView = view.findViewById(R.id.shelf_life_text_view);
        addOnTextView = view.findViewById(R.id.add_on_text_view);

    }

    private void updateUi(){
        ingredientAdapter = new MySimpleAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        ingredientRecyclerView.setLayoutManager(layoutManager);
        ingredientRecyclerView.setAdapter(ingredientAdapter);
        switch (recipeMode){
            case Create:
                setCreateUI();
                break;
            case Read:
                setReadUI();
                break;
            case Rewrite:
                setRewriteUi();
                break;
        }
        if (selectedBase != null && selectedBase.primaryIngredients != null){
            ingredientAdapter.setIngredientsNames(selectedBase.primaryIngredients);
        }
        if (selectedCompulsory != null){
            ingredientAdapter.setIngredientsNames(selectedCompulsory);
        }
        descriptionView.setText(selectedBase.description);
        shelfLifeTextView.setText(getString(R.string.shelf_life, selectedBase.shelfLife));
        pickIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPickIngredientFragment(COMPULSORY_ADD_ON);
            }
        });

        addOnAdapter = new MySimpleAdapter();
        RecyclerView.LayoutManager addOnLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        ingredientRecyclerView.setLayoutManager(addOnLayoutManager);
        addOnRecyclerView.setAdapter(addOnAdapter);
        addOnAdapter.setIngredientsNames(selectedOptional);
        pickAddOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPickIngredientFragment(OPTIONAL_ADD_ON);
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
                recipeMode = RecipeMode.Rewrite;
                setRewriteUi();
            }
        });
    }

    private void setRewriteUi(){
        setPickButtonsVisibility();
        saveButton.setText(R.string.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialogBox(currentRecipe.getValue().getName());
            }
        });
    }

    private void setCreateUI(){
        setPickButtonsVisibility();
        saveButton.setText(R.string.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialogBox(baseName);
            }
        });
    }

    private void setPickButtonsVisibility(){
        if (selectedBase.compulsoryAddOns == null){
            pickIngredientButton.setVisibility(View.GONE);
        } else {
            pickIngredientButton.setVisibility(View.VISIBLE);
        }
        if (selectedBase.optionalAddOns == null){
            pickAddOnButton.setVisibility(View.GONE);
            addOnTextView.setVisibility(View.GONE);
        } else {
            pickAddOnButton.setVisibility(View.VISIBLE);
            addOnTextView.setVisibility(View.VISIBLE);
        }
    }

    private void showSaveDialogBox(String editTextPrefill){

        final String primaryIngredientsString = convertListToString(selectedBase.primaryIngredients);
        final String selectedCompulsoryString = convertListToString(selectedCompulsory);
        final String selectedOptionalString = convertListToString(selectedOptional);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        editText = new EditText(getContext());
        editText.setText(editTextPrefill);

        builder.setView(editText)
                .setTitle(R.string.type_recipe_name)
                .setCancelable(true);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String newRecipeName = editText.getText().toString().trim();
                        switch (recipeMode){
                            case Create: saveNewRecipe(newRecipeName, primaryIngredientsString, selectedCompulsoryString, selectedOptionalString);
                                break;
                            case Rewrite: updateRecipe(newRecipeName, primaryIngredientsString, selectedCompulsoryString, selectedOptionalString);
                                break;
                        }
                    }
                }).create().show();

    }


    public void setSelectedBase(Base selectedBase) {
        this.selectedBase = selectedBase;
        updateUi();
    }

    private void startPickIngredientFragment(int isOptional){
        if (getResources().getBoolean(R.bool.isTablet)){

            // for a tablet
            PickIngredientFragment pickIngredientFragment;
            if (isOptional == COMPULSORY_ADD_ON && !selectedCompulsory.isEmpty()){
                pickIngredientFragment = PickIngredientFragment.newInstance(selectedBase, isOptional, selectedCompulsory);
            } else if (isOptional == OPTIONAL_ADD_ON && !selectedOptional.isEmpty()){
                pickIngredientFragment = PickIngredientFragment.newInstance(selectedBase, isOptional, selectedOptional);
            } else {
                pickIngredientFragment = PickIngredientFragment.newInstance(selectedBase, isOptional, null);
            }

            pickIngredientFragment.setTargetFragment(this, isOptional);
            getFragmentManager().beginTransaction().replace(R.id.pick_ingredient_fragment,pickIngredientFragment ).commit();
        } else {

            // for a phone
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
            ArrayList<String> selectedIngredients = (ArrayList<String>) data.getSerializableExtra(KEY_SELECTED_INGREDIENTS);
            if (requestCode == COMPULSORY_ADD_ON){
                selectedCompulsory.clear();
                selectedCompulsory.addAll(selectedIngredients);
                List<String> ingredientsToDisplay = new ArrayList<>();
                if (selectedBase.primaryIngredients != null){
                    ingredientsToDisplay.addAll(selectedBase.primaryIngredients);
                }
                ingredientsToDisplay.addAll(selectedCompulsory);
                ingredientAdapter.setIngredientsNames(ingredientsToDisplay);

            } else if (requestCode == OPTIONAL_ADD_ON){
                selectedOptional.clear();
                selectedOptional.addAll(selectedIngredients);
                addOnAdapter.setIngredientsNames(selectedOptional);

            }
        }
    }

    private void saveNewRecipe(String newRecipeName, String primaryIngredientsString,
                               String selectedCompulsoryString, String selectedOptionalString){
        if (selectedBase.compulsoryAddOns != null && selectedCompulsoryString.isEmpty()){
            Toast.makeText(getContext(), getResources().getString(R.string.please_select), Toast.LENGTH_LONG).show();
        } else {
            Recipe recipe = new Recipe(newRecipeName,
                    baseName,
                    primaryIngredientsString,
                    selectedCompulsoryString,
                    selectedOptionalString);
            Repository.getInstance(getContext()).insertNewRecipe(recipe);
            Toast.makeText(getContext(), getResources().getString(R.string.saved, newRecipeName), Toast.LENGTH_LONG).show();
            getActivity().finish();
        }


    }

    private void updateRecipe(String newRecipeName, String primaryIngredientsString,
                              String selectedCompulsoryString, String selectedOptionalString){
        if (selectedBase.compulsoryAddOns != null && selectedCompulsoryString.isEmpty()){
            Toast.makeText(getContext(), getResources().getString(R.string.please_select), Toast.LENGTH_LONG).show();
        } else {
            currentRecipe.getValue().setName(newRecipeName);
            currentRecipe.getValue().setCompulsoryBaseIngredient(primaryIngredientsString);
            currentRecipe.getValue().setCompulsoryAddOns(selectedCompulsoryString);
            currentRecipe.getValue().setOptionalAddOns(selectedOptionalString);
            Repository.getInstance(getContext()).updateRecipe(currentRecipe.getValue());
            Toast.makeText(getContext(), getResources().getString(R.string.updated, newRecipeName), Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }

    private String convertListToString(List<String> list){
        if (list != null){
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0 ; i <list.size() ; i++) {
                stringBuilder.append(list.get(i));
                // Do not append comma at the end of last element
                if( i < list.size() -1){
                    stringBuilder.append(SEPARATOR);
                }
            }
            return stringBuilder.toString();
        } else {
            return null;
        }
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
            outState.putSerializable(KEY_RECIPE_MODE, recipeMode);
            outState.putParcelable(KEY_BASE, selectedBase);
            outState.putStringArrayList(KEY_SELECTED_COMPULSORY, (ArrayList<String>) selectedCompulsory);
            outState.putStringArrayList(KEY_SELECTED_OPTIONAL, (ArrayList<String>) selectedOptional);
//            outState.putParcelable(KEY_INGREDIENT_ADAPTER, ingredientRecyclerView.getLayoutManager().onSaveInstanceState());
//            outState.putParcelable(KEY_ADD_ON_ADAPTER, addOnRecyclerView.getLayoutManager().onSaveInstanceState());
        if (recipeMode == RecipeMode.Read || recipeMode == RecipeMode.Rewrite){
            outState.putInt(Repository.KEY_RECIPE_ID, recipeId);
        }
    }

}

