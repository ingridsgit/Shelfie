package com.bazzillion.ingrid.shelfie;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Ingredient {
    public String name;
    public String type;
    public String description;
    public String properties;
    public List<Boolean> forProduct;
    public List<Boolean> skinType;
    public List<Boolean> specificity;

    public Ingredient(){}

    public Ingredient(String name, String type, String description, String properties, List<Boolean> forProduct, List<Boolean> skinType, List<Boolean> specificity){
        this.name = name;
        this.type = type;
        this.description = description;
        this.properties = properties;
        this.forProduct = forProduct;
        this.skinType = skinType;
        this.specificity = specificity;
    }
}
