package com.bazzillion.ingrid.shelfie.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.bazzillion.ingrid.shelfie.Base;
import com.bazzillion.ingrid.shelfie.Ingredient;
import com.bazzillion.ingrid.shelfie.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirebaseDataWriting {

     static final String AQUEOUS_PHASE = "Aqueous phase";
    static final String CLAY = "Clay";
    static final String COSMETIC_INGREDIENT = "Cosmetic ingredient";
    static final String ESSENTIAL_OIL = "Essential oil";
    static final String FLOUR = "Flour";
    static final String FRUIT_PULP = "Fruit pulp";
    static final String PRESERVATIVE = "Preservative";
    static final String SOAP = "Soap";
    static final String TEXTURIZER = "Texturizer";
    static final String VEGETABLE_OIL = "Vegetable oil";
    static final String[] typeList = {AQUEOUS_PHASE,
            CLAY,
            COSMETIC_INGREDIENT,
            ESSENTIAL_OIL,
            FLOUR,
            FRUIT_PULP,
            PRESERVATIVE,
            SOAP,
            TEXTURIZER,
            VEGETABLE_OIL};

    private static Map<String, List<String>> getTypesFromCsv(Context context) {
        Map<String, List<String>> typeTable = new HashMap<>();
        try { for (int i = 0; i < typeList.length; i++) {
                typeTable.put(String.valueOf(typeList[i]), extractIngredientList(context, typeList[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
        return typeTable;
    }

     private static List<String> extractIngredientList(Context context, String type) throws IOException {
         CSVReader csvReader = new CSVReader(new InputStreamReader(context.getResources().openRawResource(R.raw.ingredients2)));
         String[] nextLine;
         List<String> ingredientList = new ArrayList<>();
         while ((nextLine = csvReader.readNext()) != null) {
             // nextLine[] is an array of values from the line
             if (nextLine[1].equalsIgnoreCase(type)) {
                 ingredientList.add(nextLine[0]);
             }
         }
         csvReader.close();
         return ingredientList;
     }

     private static void addProductsToDb(Context context, DatabaseReference dbReference){
//        String[] productArray = context.getResources().getStringArray(R.array.product_array);
//        for (String product : productArray){
//            dbReference.child("product").setValue(product);
//         }
         List<String> toothpasteBases = new ArrayList<>();
         toothpasteBases.add("Coconut oil toothpaste");
         toothpasteBases.add("Clay toothpaste");
         toothpasteBases.add("Lemon toothpaste");

         dbReference.child("product").child("Toothpaste").setValue(toothpasteBases);

         List<String> shampooBases = new ArrayList<>();
         shampooBases.add("Honey shampoo");
         shampooBases.add("Egg shampoo");
         shampooBases.add("Chickpea flour shampoo");
         shampooBases.add("Clay shampoo");
         shampooBases.add("Soap shampoo");
         shampooBases.add("Baking soda shampoo");

         dbReference.child("product").child("Shampoo").setValue(shampooBases);

     }

     private static Base extractBase(String[] line){

        String[] primaryIngredientsArray = line[3].split(",");
        List<String> primaryIngredients = new ArrayList<>();
         Collections.addAll(primaryIngredients, primaryIngredientsArray);

         String[] compulsoryAddOnsArray;
         if (!line[4].isEmpty()){
            compulsoryAddOnsArray = line[4].split(",");
        } else {compulsoryAddOnsArray = new String[0];}
         List<String> complusoryAddOns = new ArrayList<>();
         Collections.addAll(complusoryAddOns, compulsoryAddOnsArray);

         String[] optionalAddOnsArray;
         if (!line[5].isEmpty()){
             optionalAddOnsArray = line[5].split(",");
         }  else {optionalAddOnsArray = new String[0];}
         List<String> optionalAddOns = new ArrayList<>();
         Collections.addAll(optionalAddOns, optionalAddOnsArray);
        return new Base(line[1], line[2], primaryIngredients, complusoryAddOns, optionalAddOns, line[6]);

     }

     public static void changeIngredientsIds(){
         FirebaseDatabase database = FirebaseDatabase.getInstance();
         final DatabaseReference dbReference = database.getReference();
        for (int i = 0 ; i < 53 ; i++){
dbReference.child("Ingredient").child(String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Ingredient ingredient = dataSnapshot.getValue(Ingredient.class);
                    dbReference.child("Ingredient").child(ingredient.name).setValue(ingredient);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            dbReference.child("Ingredient").child(String.valueOf(i)).removeValue();
        }
     }

     public static void rectifierBanane(){
         FirebaseDatabase database = FirebaseDatabase.getInstance();
         final DatabaseReference dbReference = database.getReference();
         dbReference.child("Ingredient").child("\"Banana pulp \"").child(" peel").child("ingrid").setValue("essai");
//                 .addValueEventListener(new ValueEventListener() {
//             @Override
//             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                 Ingredient ingredient = dataSnapshot.getValue(Ingredient.class);
//                 dbReference.child("Ingredient").child("Banana pulp").setValue(ingredient);
//             }
//
//             @Override
//             public void onCancelled(@NonNull DatabaseError databaseError) {
//
//             }
//         });
     }


    public static void writeToDb(Context context){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReference = database.getReference();
        DatabaseReference childReference = dbReference.child("type");
        Map<String, List<String>> typeMap = getTypesFromCsv(context);
        Set<Map.Entry<String, List<String>>> entrySet = typeMap.entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet ) {
            childReference.child(entry.getKey()).setValue(entry.getValue());
        }
        addProductsToDb(context, dbReference);
        CSVReader csvReader = new CSVReader(new InputStreamReader(context.getResources().openRawResource(R.raw.base_essai)));
        String[] nextLine;
        int i = 0;
        try {
            while ((nextLine = csvReader.readNext()) != null) {
                Base base = extractBase(nextLine);
                dbReference.child("base").child(base.name).setValue(extractBase(nextLine));
                dbReference.child("base").child(base.name).child("name").removeValue();
                i++;
            }
            csvReader.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
