package com.bazzillion.ingrid.shelfie.Database;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipe")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    @ColumnInfo(name = "base_name")
    private String baseName;
    @ColumnInfo(name = "primary_ingredients")
    private String primaryIngredients;
    @ColumnInfo(name = "add_ons")
    private String addOns;

    @Ignore
    public Recipe(String name, String baseName, String primaryIngredients, @Nullable String addOns) {
        this.name = name;
        this.baseName = baseName;
        this.primaryIngredients = primaryIngredients;
        this.addOns = addOns;
    }

    public Recipe(int id, String name, String baseName, String primaryIngredients, @Nullable String addOns) {
        this.id = id;
        this.name = name;
        this.baseName = baseName;
        this.primaryIngredients = primaryIngredients;
        this.addOns = addOns;
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

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getPrimaryIngredients() {
        return primaryIngredients;
    }

    public void setPrimaryIngredients(String primaryIngredients) {
        this.primaryIngredients = primaryIngredients;
    }

    public String getAddOns() {
        return addOns;
    }

    public void setAddOns(String addOns) {
        this.addOns = addOns;
    }

}
