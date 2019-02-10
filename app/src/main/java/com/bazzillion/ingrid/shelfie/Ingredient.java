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
    public List<String> forProduct;
    public List<String> skinType;
    public List<String> specificity;

    public Ingredient(){}

    public Ingredient(String name, String type, String description, String properties, List<String> forProduct, List<String> skinType, List<String> specificity){
        this.name = name;
        this.type = type;
        this.description = description;
        this.properties = properties;
        this.forProduct = forProduct;
        this.skinType = skinType;
        this.specificity = specificity;
    }
}
