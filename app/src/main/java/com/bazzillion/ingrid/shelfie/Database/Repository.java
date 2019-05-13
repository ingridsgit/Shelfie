package com.bazzillion.ingrid.shelfie.Database;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bazzillion.ingrid.shelfie.Adapters.BaseAdapter;
import com.bazzillion.ingrid.shelfie.Adapters.RecipeAdapter;
import com.bazzillion.ingrid.shelfie.AddOnFragment;
import com.bazzillion.ingrid.shelfie.Base;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    private static Repository repository;
    private AppDatabase appDatabase;
    private LiveData<List<Recipe>> myRecipes;
    private FirebaseDatabase database;
    private DatabaseReference basesDbReference;
    private ValueEventListener basesValueEventListener;
    private DatabaseReference singleBaseDbReference;
    private ValueEventListener singleBaseVEListener;

    private static final String FIREBASE_KEY_PRODUCT = "product";
    private static final String FIREBASE_KEY_BASE = "base";
    private static final String FIREBASE_KEY_TYPE = "type";
    private static final String FIREBASE_KEY_INGREDIENT = "Ingredient";

    public Repository(Context context){
        appDatabase = AppDatabase.getInstance(context);
    }

    public static Repository getInstance(Context context){
        if (repository == null){
            repository = new Repository(context);
        }
        return repository;
    }

    public void setUpViewModel(FragmentActivity activity, final RecipeAdapter recipeAdapter){
        MainViewModel mainViewModel = ViewModelProviders.of(activity).get(MainViewModel.class);
        myRecipes = mainViewModel.getMyRecipes();
        myRecipes.observe(activity, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                recipeAdapter.setRecipes(recipes);
            }
        });
    }

    public LiveData<Recipe> getRecipeById(final FragmentActivity activity, final int recipeId){
        SingleRecipeViewModelFactory singleRecipeViewModelFactory =
                new SingleRecipeViewModelFactory(appDatabase, recipeId);
        final SingleRecipeViewModel singleRecipeViewModel =
                ViewModelProviders.of(activity, singleRecipeViewModelFactory)
                        .get(SingleRecipeViewModel.class);
        return singleRecipeViewModel.getRecipe();
    }


    public void insertNewRecipe(final Recipe recipe){
        AppExecutors.getInstance().getDiskIo().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.recipeDao().insertNewRecipe(recipe);
            }
        });
    }
//
//    @Update(onConflict = OnConflictStrategy.REPLACE)
//    void updateRecipe(Recipe recipe); // no need for LiveData

    public void deleteRecipe(final Recipe recipe){
        AppExecutors.getInstance().getDiskIo().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.recipeDao().deleteRecipe(recipe);
            }
        });
    }

    public void retrieveBases(final BaseAdapter baseAdapter, String productType, final Context context){
        database = FirebaseDatabase.getInstance();
        basesDbReference = database.getReference().child(FIREBASE_KEY_PRODUCT).child(productType);
        basesValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> valueSet = (ArrayList<String>) dataSnapshot.getValue();
                baseAdapter.setBases(valueSet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

        };
        basesDbReference.addValueEventListener(basesValueEventListener);
    }

    public void removeBasesValueEventListener(){
        basesDbReference.removeEventListener(basesValueEventListener);
    }

    public void retrieveSingleBase(String baseName, final AddOnFragment addOnFragment){
        database = FirebaseDatabase.getInstance();
        singleBaseDbReference = database.getReference().child(FIREBASE_KEY_BASE).child(baseName);
        singleBaseVEListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Base selectedBase = dataSnapshot.getValue(Base.class);
                if (selectedBase != null){
                    addOnFragment.setSelectedBase(selectedBase);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(addOnFragment.getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        singleBaseDbReference.addValueEventListener(singleBaseVEListener);
    }

    public void removeSingleBaseValueEventListener(){
        singleBaseDbReference.removeEventListener(singleBaseVEListener);
    }

    public void queryFirebaseBeforeUpdate(final List<String> addOnTypes, final String product) {
        database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference();
        int i = 0;
        databaseReference.child(FIREBASE_KEY_TYPE).child(addOnTypes.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> ingredients = (List<String>) dataSnapshot.getValue();
                if (ingredients != null) {
                    databaseReference.child(FIREBASE_KEY_INGREDIENT).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //TODO : fix
//                            List<String> myList = new ArrayList<>();
//                            for (String ingredient : ingredients) {
//                                boolean isForProduct = dataSnapshot.child(ingredient).child("forProduct").child(product).getValue() != null;
//                                boolean isForUser;
//                                if (hairType != null) {
//                                    isForUser = dataSnapshot.child(ingredient).child("skinType").child(hairType).getValue() != null;
//                                } else {
//                                    isForUser = true;
//                                }
//
//                                if (isForProduct && isForUser) {
//                                    myList.add(ingredient);
//                                }
//                            }
//                            if (!myList.isEmpty()) {
//                                ingredientLists.add(myList);
//                                ingredientTypes.add(addOnTypes.get(i));
//
//                            }
//                            i++;
//                            if (i < addOnTypes.size()) {
//                                queryFirebaseBeforeUpdate(databaseReference, addOnTypes);
//                            } else {
//                                ingredientPickerAdapter.setIngredientLists(ingredientLists);
//                            }
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

}
