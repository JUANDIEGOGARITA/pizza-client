package com.example.app.pizzaapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by juandiegoGL on 4/9/17.
 */

public class PostToppingByPizzaResult {

    @SerializedName("object")
    GetToppingByPizzaResult mTopping;
    @SerializedName("errors")
    Error mError;


    public GetToppingByPizzaResult getmTopping() {
        return mTopping;
    }

    public Error getmError() {
        return mError;
    }

    @Override
    public String toString() {
        return "PostToppingByPizzaResult{" +
                "mTopping=" + mTopping +
                ", mError=" + mError +
                '}';
    }

    public PostToppingByPizzaResult(GetToppingByPizzaResult mTopping, Error mError) {
        this.mTopping = mTopping;
        this.mError = mError;
    }
}