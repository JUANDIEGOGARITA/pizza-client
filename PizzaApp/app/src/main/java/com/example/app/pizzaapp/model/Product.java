package com.example.app.pizzaapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public abstract class Product {

    @SerializedName("id")
    String mId;

    @SerializedName("name")
    String mName;

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    @Override
    public String toString() {
        return "Product{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                '}';
    }
}
