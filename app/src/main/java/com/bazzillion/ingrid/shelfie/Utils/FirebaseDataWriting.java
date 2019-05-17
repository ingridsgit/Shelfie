package com.bazzillion.ingrid.shelfie.Utils;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.bazzillion.ingrid.shelfie.Database.Base;
import com.bazzillion.ingrid.shelfie.Database.Ingredient;
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
    static final String[] SKIN_TYPE_ARRAY = {
            "REGULAR SKIN",
            "DRY SKIN",
            "COMBINATION SKIN",
            "OILY SKIN",
            "REGULAR HAIR",
            "OILY HAIR",
    };

    static final String[] SPECIFICITY_ARRAY = {
            "ACNEIC SKIN",
            "MATURE SKIN",
            "SAGGING SKIN / CELLULITE",
            "ECZEMA",
            "PSORIASIS",
            "SCARS / STRETCH MARKS",
            "DANDRUFF"
    };
    static final String LOG = "FIREBASEDATAWRITING";

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
         List<String> primaryIngredients = new ArrayList<>();
        if ( !line[3].isEmpty()){
            Collections.addAll(primaryIngredients, line[3]);
        }



         String[] compulsoryAddOnsArray;
         if (!line[4].isEmpty()){
            compulsoryAddOnsArray = line[4].split(",");
        } else {compulsoryAddOnsArray = new String[0];}
         List<String> complusoryAddOns = new ArrayList<>();
         Collections.addAll(complusoryAddOns, compulsoryAddOnsArray);

         String[] optionalAddOnsArray;
         if (!line[5].isEmpty()){
             optionalAddOnsArray = line[5].split(", ");
         }  else {optionalAddOnsArray = new String[0];}
         List<String> optionalAddOns = new ArrayList<>();
         Collections.addAll(optionalAddOns, optionalAddOnsArray);
        return new Base(line[1], line[2], primaryIngredients, complusoryAddOns, optionalAddOns, line[6], line[7], line[0]);

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

     public static void changeListsToMaps(){
         FirebaseDatabase database = FirebaseDatabase.getInstance();
         final DatabaseReference dbReference = database.getReference().child("Ingredient");
         dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for (final DataSnapshot ingredientKey: dataSnapshot.getChildren()){
                     dbReference.child(ingredientKey.getKey()).child("specificity").addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             Object value = dataSnapshot.getValue();
                             if (value instanceof List){
                                 List<String> skinTypes = (List<String>) value;
                                 Map<String, Boolean> newMap = new HashMap<>();
                                 if (skinTypes != null){
                                     for (int i = 0; i < skinTypes.size(); i++){
                                         newMap.put(skinTypes.get(i), true);
                                     }
                                     if (newMap.containsKey("SCARS / STRETCH MARKS")){
                                         newMap.remove("SCARS / STRETCH MARKS");
                                         newMap.put("SCARS - STRETCH MARKS", true);
                                     }
                                     if (newMap.containsKey("SAGGING SKIN / CELLULITE")){
                                         newMap.remove("SAGGING SKIN / CELLULITE");
                                         newMap.put("SAGGING SKIN - CELLULITE", true);
                                     }
                                     dbReference.child(ingredientKey.getKey()).child("specificity").setValue(newMap);
                                 } else {
                                     Log.i("ATTENTIOOOOON", ingredientKey.getKey());
                                 }
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

     public static void splitSkinTypeSpecificity(){

         FirebaseDatabase database = FirebaseDatabase.getInstance();
         final DatabaseReference dbReference = database.getReference().child("Ingredient");
         dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 List<String> ingredients = new ArrayList<>();
            for (DataSnapshot ingredientKey: dataSnapshot.getChildren()) {

                ingredients.add(ingredientKey.getKey());
             }
                 for (final String ingredient: ingredients){
//                     dbReference.child(ingredient).child("forSkin").removeValue();
                 }

            for (final String ingredient: ingredients){
                dbReference.child(ingredient).child("forSkin").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> specificities = (List<String>) dataSnapshot.getValue();
                        List<String> newFirebaseSkinTypeValues = new ArrayList<>();
                        for (String specificity : specificities){
                            for (String skinType : SPECIFICITY_ARRAY){
                                if (specificity.equals(skinType)){
                                    newFirebaseSkinTypeValues.add(specificity);
                                }
                            }
                        }
                        dbReference.child(ingredient).child("specificity").setValue(newFirebaseSkinTypeValues);
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

     public static void addBodyPart(Context context){
         FirebaseDatabase database = FirebaseDatabase.getInstance();
         DatabaseReference dbReference = database.getReference().child("base");
         CSVReader csvReader = new CSVReader(new InputStreamReader(context.getResources().openRawResource(R.raw.base_essai)));
         String[] nextLine;
         try {
             while ((nextLine = csvReader.readNext()) != null) {
                 dbReference.child(nextLine[1]).child("bodyPart").setValue(nextLine[7]);
             }
             csvReader.close();
         } catch (IOException e){
             e.printStackTrace();
         }
     }

//     public static void addProductInBase(Context context){
//         FirebaseDatabase database = FirebaseDatabase.getInstance();
//         DatabaseReference dbReference = database.getReference().child("base");
//         CSVReader csvReader = new CSVReader(new InputStreamReader(context.getResources().openRawResource(R.raw.base_essai)));
//         String[] nextLine;
//         try {
//             while ((nextLine = csvReader.readNext()) != null) {
//                 dbReference.child(nextLine[1]).child("product").setValue(nextLine[0]);
//             }
//             csvReader.close();
//         } catch (IOException e){
//             e.printStackTrace();
//         }
//     }

     public static void getProductsFromCSV(Context context){
         FirebaseDatabase database = FirebaseDatabase.getInstance();
         DatabaseReference dbReference = database.getReference();
         CSVReader csvReader = new CSVReader(new InputStreamReader(context.getResources().openRawResource(R.raw.base_essai)));
         String[] nextLine;
         Map<String, String> myProducts = new HashMap<>();
         List<String> toothpaste = new ArrayList<>();
         List<String> faceMask = new ArrayList<>();
         List<String> makeUpRem = new ArrayList<>();
         List<String> scrub = new ArrayList<>();
         List<String> bodyCream = new ArrayList<>();
         List<String> showerGel = new ArrayList<>();
         List<String> shampoo = new ArrayList<>();
         List<String> conditioner = new ArrayList<>();
         List<String> hairMask = new ArrayList<>();
         int i = 0;
         try {
             while ((nextLine = csvReader.readNext()) != null) {
                 String product = nextLine[0];
                 String base = nextLine[1];
                 myProducts.put(product, base);
                 switch (product){
                     case "TOOTHPASTE":
                         toothpaste.add(base);
                         break;
                     case "MAKE UP REMOVER":
                         makeUpRem.add(base);
                         break;
                     case "FACE MASK":
                         faceMask.add(base);
                         break;
                     case "SCRUB":
                         scrub.add(base);
                         break;
                     case "BODY CREAM":
                         bodyCream.add(base);
                         break;
                     case "SHOWER GEL":
                         showerGel.add(base);
                         break;
                     case "SHAMPOO":
                         shampoo.add(base);
                         break;
                     case "CONDITIONER":
                         conditioner.add(base);
                         break;
                     case "HAIR MASK":
                         hairMask.add(base);
                         break;
                 }
             }

             csvReader.close();
         } catch (IOException e){
             e.printStackTrace();
         }
         String[] stringArray = context.getResources().getStringArray(R.array.product_array);

         dbReference.child("product").child(stringArray[0]).setValue(toothpaste);
         dbReference.child("product").child(stringArray[1]).setValue(faceMask);
         dbReference.child("product").child(stringArray[2]).setValue(makeUpRem);
         dbReference.child("product").child(stringArray[3]).setValue(scrub);
         dbReference.child("product").child(stringArray[4]).setValue(bodyCream);
         dbReference.child("product").child(stringArray[5]).setValue(showerGel);
         dbReference.child("product").child(stringArray[6]).setValue(shampoo);
         dbReference.child("product").child(stringArray[7]).setValue(conditioner);
         dbReference.child("product").child(stringArray[8]).setValue(hairMask);

     }


    public static void writeToDb(Context context){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReference = database.getReference();
//        DatabaseReference childReference = dbReference.child("type");
//        Map<String, List<String>> typeMap = getTypesFromCsv(context);
//        Set<Map.Entry<String, List<String>>> entrySet = typeMap.entrySet();
//        for (Map.Entry<String, List<String>> entry : entrySet ) {
//            childReference.child(entry.getKey()).setValue(entry.getValue());
//        }
//        addProductsToDb(context, dbReference);
        CSVReader csvReader = new CSVReader(new InputStreamReader(context.getResources().openRawResource(R.raw.base_essai)));
        String[] nextLine;
        int i = 0;
        try {
            while ((nextLine = csvReader.readNext()) != null) {
                Base base = extractBase(nextLine);
                dbReference.child("base").child(base.name).setValue(base);
                dbReference.child("base").child(base.name).child("name").removeValue();
                i++;
            }
            csvReader.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
