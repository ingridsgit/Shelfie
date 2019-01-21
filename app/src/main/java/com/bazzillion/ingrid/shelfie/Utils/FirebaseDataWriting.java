package com.bazzillion.ingrid.shelfie.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bazzillion.ingrid.shelfie.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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


    public static void writeToDb(Context context){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReference = database.getReference().child("type");
        Map<String, List<String>> typeMap = getTypesFromCsv(context);
        Set<Map.Entry<String, List<String>>> entrySet = typeMap.entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet ) {
            dbReference.child(entry.getKey()).setValue(entry.getValue());
        }
    }
}
