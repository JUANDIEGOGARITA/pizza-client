package com.example.app.pizzaapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by juandiegoGL on 4/10/17.
 */

public class PostTopping {

    @SerializedName("topping")
    Topping mTopping;

    public PostTopping(Topping topping) {
        this.mTopping = topping;
    }

}
