package com.example.app.pizzaapp.datamanager;

import android.content.Context;

import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.model.GetToppingByPizzaResult;
import com.example.app.pizzaapp.model.Pizza;
import com.example.app.pizzaapp.model.PostPizza;
import com.example.app.pizzaapp.model.PostTopping;
import com.example.app.pizzaapp.model.PostToppingByPizza;
import com.example.app.pizzaapp.model.PostToppingByPizzaResult;
import com.example.app.pizzaapp.model.Topping;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by juandiegoGL on 4/7/17.
 */

public class DataManager {

    String mBaseUrl;

    public DataManager(Context context) {
        mBaseUrl = context.getString(R.string.base_url);
    }

    public void getPizzas(final ServiceCallback listener) {
        DataManagerInterface apiService =
                BaseManager.getClient(mBaseUrl).create(DataManagerInterface.class);

        Call<List<Pizza>> call = apiService.getPizzas();
        call.enqueue(new Callback<List<Pizza>>() {
            @Override
            public void onResponse(Call<List<Pizza>> call, Response<List<Pizza>> response) {

                listener.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<List<Pizza>> call, Throwable t) {
                listener.onError(t);
            }
        });
    }

    public void getToppings(final ServiceCallback listener) {
        DataManagerInterface apiService =
                BaseManager.getClient(mBaseUrl).create(DataManagerInterface.class);

        Call<List<Topping>> call = apiService.getToppings();
        call.enqueue(new Callback<List<Topping>>() {
            @Override
            public void onResponse(Call<List<Topping>> call, Response<List<Topping>> response) {
                listener.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<List<Topping>> call, Throwable t) {
                listener.onError(t);
            }
        });
    }

    public void getToppingsByPizzaId(int pizzaId, final ServiceCallback listener) {
        DataManagerInterface apiService =
                BaseManager.getClient(mBaseUrl).create(DataManagerInterface.class);
        Call<List<GetToppingByPizzaResult>> call = apiService.getToppingsByPizzaId(pizzaId);
        call.enqueue(new Callback<List<GetToppingByPizzaResult>>() {
            @Override
            public void onResponse(Call<List<GetToppingByPizzaResult>> call, Response<List<GetToppingByPizzaResult>> response) {
                listener.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<List<GetToppingByPizzaResult>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void postTopping(PostTopping topping, final ServiceCallback listener) {
        DataManagerInterface apiService =
                BaseManager.getClient(mBaseUrl).create(DataManagerInterface.class);
        Call<Topping> call = apiService.postTopping(topping);
        call.enqueue(new Callback<Topping>() {
            @Override
            public void onResponse(Call<Topping> call, Response<Topping> response) {
                listener.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<Topping> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void postPizza(PostPizza pizza, final ServiceCallback listener) {
        DataManagerInterface apiService =
                BaseManager.getClient(mBaseUrl).create(DataManagerInterface.class);
        Call<Pizza> call = apiService.postPizza(pizza);
        call.enqueue(new Callback<Pizza>() {
            @Override
            public void onResponse(Call<Pizza> call, Response<Pizza> response) {
                listener.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<Pizza> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void postToppingByPizza(int pizzaId, PostToppingByPizza topping, final ServiceCallback listener) {
        DataManagerInterface apiService =
                BaseManager.getClient(mBaseUrl).create(DataManagerInterface.class);
        Call<PostToppingByPizzaResult> call = apiService.postToppingByPizza(pizzaId, topping);
        call.enqueue(new Callback<PostToppingByPizzaResult>() {
            @Override
            public void onResponse(Call<PostToppingByPizzaResult> call, Response<PostToppingByPizzaResult> response) {
                listener.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<PostToppingByPizzaResult> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }
}