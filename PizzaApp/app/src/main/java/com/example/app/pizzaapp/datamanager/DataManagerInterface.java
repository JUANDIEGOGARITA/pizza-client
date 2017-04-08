package com.example.app.pizzaapp.datamanager;

import com.example.app.pizzaapp.model.Pizza;
import com.example.app.pizzaapp.model.Topping;
import com.example.app.pizzaapp.model.ToppingByPizza;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public interface DataManagerInterface {

    @GET("pizzas")
    Call<List<Pizza>> getPizzas();

    @GET("toppings")
    Call<List<Topping>> getToppings();

    @GET("pizzas/{id}/toppings")
    Call<List<ToppingByPizza>> getToppingsByPizzaId(@Path("id") int id);



}
