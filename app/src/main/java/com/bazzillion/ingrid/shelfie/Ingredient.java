package com.bazzillion.ingrid.shelfie;

public class Ingredient {
    private String name;
    private String type;
    private String description;
    private String properties;
    private String[] forProduct;
    private String[] forSkin;

    public Ingredient(){}

    public Ingredient(String name, String type, String description, String properties, String[] forProduct, String[] forSkin){
        this.name = name;
        this.type = type;
        this.description = description;
        this.properties = properties;
        this.forProduct = forProduct;
        this.forSkin = forSkin;
    }
}
