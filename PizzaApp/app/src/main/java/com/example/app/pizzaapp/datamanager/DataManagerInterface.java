package com.example.app.pizzaapp.datamanager;

import com.example.app.pizzaapp.model.GetToppingByPizzaResult;
import com.example.app.pizzaapp.model.Pizza;
import com.example.app.pizzaapp.model.PostTopping;
import com.example.app.pizzaapp.model.PostToppingByPizza;
import com.example.app.pizzaapp.model.PostToppingByPizzaResult;
import com.example.app.pizzaapp.model.Topping;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
    Call<List<GetToppingByPizzaResult>> getToppingsByPizzaId(@Path("id") int id);

    @POST("toppings")
    Call<Topping> postTopping(@Body PostTopping toppingName);

    @POST("pizza")
    Call<Pizza> postPizza(@Body Pizza topping);

    @POST("pizzas/{id}/toppings")
    Call<PostToppingByPizzaResult> postToppingByPizza(@Path("id") int id, @Body PostToppingByPizza topping);
}