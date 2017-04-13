package com.example.app.pizzaapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by juandiegoGL on 4/10/17.
 */

public class PostToppingByPizza {

    @SerializedName("topping_id")
    int mToppingId;

    public PostToppingByPizza(int mToppingId) {
        this.mToppingId = mToppingId;
    }

    public int getmToppingId() {
        return mToppingId;
    }

    @Override
    public String toString() {
        return "PostToppingByPizza{" +
                "mToppingId=" + mToppingId +
                '}';
    }
}