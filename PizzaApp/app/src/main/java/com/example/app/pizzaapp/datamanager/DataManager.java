package com.example.app.pizzaapp.datamanager;

import android.util.Log;

import com.example.app.pizzaapp.model.Pizza;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by juandiegoGL on 4/7/17.
 */

public class DataManager {

    public DataManager() {
    }

    public void getPizzas(final ServiceCallback listener) {
        DataManagerInterface apiService =
                BaseManager.getClient().create(DataManagerInterface.class);

        Call<List<Pizza>> call = apiService.getPizzas();
        call.enqueue(new Callback<List<Pizza>>() {
            @Override
            public void onResponse(Call<List<Pizza>> call, Response<List<Pizza>> response) {
                List<Pizza> pizzas = response.body();
                listener.onSuccess(response.body());
                Log.d("JD DataManager", pizzas.toString());
            }

            @Override
            public void onFailure(Call<List<Pizza>> call, Throwable t) {
                listener.onError(t);
                Log.d("JD DataManager", t.getMessage());
            }
        });
    }
}
