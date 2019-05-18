package com.bazzillion.ingrid.shelfie.Database;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Base implements Parcelable {
    @Exclude
    public String name;
    public String description;
    public List<String> primaryIngredients;
    public List<String> compulsoryAddOns;
    public List<String> optionalAddOns;
    public String shelfLife;
    public String bodyPart;
    public String product;

    public Base(){

    }

    public Base(String name, String description, @Nullable List<String> primaryIngredients, @Nullable List<String> compulsoryAddOns,
                @Nullable List<String> optionalAddOns, String shelfLife, String bodyPart, String product){
        this.name = name;
        this.description = description;
        this.primaryIngredients = primaryIngredients;
        this.compulsoryAddOns = compulsoryAddOns;
        this.optionalAddOns = optionalAddOns;
        this.shelfLife = shelfLife;
        this.bodyPart = bodyPart;
        this.product = product;
    }

    public Base(Parcel in){
        name = in.readString();
        description = in.readString();
        primaryIngredients = new ArrayList<>();
        in.readStringList(primaryIngredients);
        compulsoryAddOns = new ArrayList<>();
        in.readStringList(compulsoryAddOns);
        optionalAddOns = new ArrayList<>();
        in.readStringList(optionalAddOns);
        shelfLife = in.readString();
        bodyPart = in.readString();
        product = in.readString();
    }

    public static final Creator<Base> CREATOR = new Creator<Base>() {
        @Override
        public Base createFromParcel(Parcel in) {
            return new Base(in);
        }

        @Override
        public Base[] newArray(int size) {
            return new Base[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeStringList(primaryIngredients);
        dest.writeStringList(compulsoryAddOns);
        dest.writeStringList(optionalAddOns);
        dest.writeString(shelfLife);
        dest.writeString(bodyPart);
        dest.writeString(product);
    }

    public void setName(String name){
        this.name = name;
    }
}
