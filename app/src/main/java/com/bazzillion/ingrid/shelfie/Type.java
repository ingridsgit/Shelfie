package com.bazzillion.ingrid.shelfie;

import java.util.ArrayList;
import java.util.List;

public class Type {

    private String name;
    private List<Ingredient> ingredients;

    public Type(String name, List<Ingredient> ingredients){
        this.name = name;
        this.ingredients = ingredients;
    }

}
