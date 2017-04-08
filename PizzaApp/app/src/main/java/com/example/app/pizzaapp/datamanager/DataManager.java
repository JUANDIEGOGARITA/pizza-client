package com.example.app.pizzaapp.datamanager;

import android.content.Context;
import android.util.Log;

import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.model.Pizza;
import com.example.app.pizzaapp.model.Topping;
import com.example.app.pizzaapp.model.ToppingByPizza;

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
                listener.onSuccess(response.body());
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
                listener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Topping>> call, Throwable t) {
                listener.onError(t);
            }
        });
    }

    public void getToppingsByPizzaId(int pizzaId, final ServiceCallback listener){
        DataManagerInterface apiService =
                BaseManager.getClient(mBaseUrl).create(DataManagerInterface.class);
        Call<List<ToppingByPizza>> call = apiService.getToppingsByPizzaId(pizzaId);
        call.enqueue(new Callback<List<ToppingByPizza>>() {
            @Override
            public void onResponse(Call<List<ToppingByPizza>> call, Response<List<ToppingByPizza>> response) {
                listener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<ToppingByPizza>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }
}
