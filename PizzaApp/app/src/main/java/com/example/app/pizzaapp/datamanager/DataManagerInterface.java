package com.example.app.pizzaapp.datamanager;

import com.example.app.pizzaapp.model.Pizza;
import com.example.app.pizzaapp.model.Topping;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public interface DataManagerInterface {

        @GET("pizzas")
        Call<List<Pizza>> getPizzas();

        @GET("toppings")
        Call<Topping> getToppings(@Path("id") int id, @Query("api_key") String apiKey);
    }
