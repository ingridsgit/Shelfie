package com.bazzillion.ingrid.shelfie;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.annotations.Nullable;

import java.util.List;

@IgnoreExtraProperties
public class Base {
    @Exclude
    public String name;
    public String description;
    public List<String> primaryIngredients;
    public List<String> compulsoryAddOns;
    public List<String> optionalAddOns;
    public String shelfLife;

    public Base(){

    }

    public Base(String name, String description,
                List<String> primaryIngredients, @Nullable List<String> compulsoryAddOns, @Nullable List<String> optionalAddOns, String shelfLife){
        this.name = name;
        this.description = description;
        this.primaryIngredients = primaryIngredients;
        this.compulsoryAddOns = compulsoryAddOns;
        this.optionalAddOns = optionalAddOns;
        this.shelfLife = shelfLife;
    }


}
