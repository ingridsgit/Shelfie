package com.bazzillion.ingrid.shelfie.Database;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipe")
public class Recipe{

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    @ColumnInfo(name = "base_name")
    private String baseName;
    @ColumnInfo(name = "compulsory_base_ingr")
    private String compulsoryBaseIngredient;
    @ColumnInfo(name = "primary_ingredients")
    private String compulsoryAddOns;
    @ColumnInfo(name = "add_ons")
    private String optionalAddOns;

    @Ignore
    public Recipe(String name, String baseName, @Nullable String compulsoryBaseIngredient, @Nullable String compulsoryAddOns, @Nullable String optionalAddOns) {
        this.name = name;
        this.baseName = baseName;
        this.compulsoryBaseIngredient = compulsoryBaseIngredient;
        this.compulsoryAddOns = compulsoryAddOns;
        this.optionalAddOns = optionalAddOns;
    }

    public Recipe(int id, String name, String baseName, @Nullable String compulsoryBaseIngredient, @Nullable String compulsoryAddOns, @Nullable String optionalAddOns) {
        this.id = id;
        this.name = name;
        this.baseName = baseName;
        this.compulsoryBaseIngredient = compulsoryBaseIngredient;
        this.compulsoryAddOns = compulsoryAddOns;
        this.optionalAddOns = optionalAddOns;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseName() {
        return baseName;
    }

    public String getCompulsoryBaseIngredient() {
        return compulsoryBaseIngredient;
    }

    public void setCompulsoryBaseIngredient(String compulsoryBaseIngredient) {
        this.compulsoryBaseIngredient = compulsoryBaseIngredient;
    }

    public String getCompulsoryAddOns() {
        return compulsoryAddOns;
    }

    public void setCompulsoryAddOns(String compulsoryAddOns) {
        this.compulsoryAddOns = compulsoryAddOns;
    }

    public String getOptionalAddOns() {
        return optionalAddOns;
    }

    public void setOptionalAddOns(String optionalAddOns) {
        this.optionalAddOns = optionalAddOns;
    }

}
