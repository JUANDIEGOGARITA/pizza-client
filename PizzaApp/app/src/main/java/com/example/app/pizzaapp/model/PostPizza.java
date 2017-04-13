package com.example.app.pizzaapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by juandiegoGL on 4/10/17.
 */

public class PostPizza {

    @SerializedName("pizza")
    Pizza mPizza;

    public PostPizza(Pizza pizza) {
        this.mPizza = pizza;
    }
}