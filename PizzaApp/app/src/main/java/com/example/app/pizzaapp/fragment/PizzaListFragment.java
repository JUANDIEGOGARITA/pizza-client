package com.example.app.pizzaapp.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.adapter.BaseRecyclerAdapter;
import com.example.app.pizzaapp.adapter.PizzaRecyclerAdapter;
import com.example.app.pizzaapp.datamanager.DataManager;
import com.example.app.pizzaapp.datamanager.ServiceCallback;
import com.example.app.pizzaapp.helper.TransitionHelper;
import com.example.app.pizzaapp.model.Pizza;
import com.example.app.pizzaapp.model.ToppingByPizza;
import com.example.app.pizzaapp.util.Navigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class PizzaListFragment extends TransitionHelper.BaseFragment {
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    PizzaRecyclerAdapter recyclerAdapter;
    List<Pizza> pizzas;

    public PizzaListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pizza_list, container, false);
        ButterKnife.bind(this, rootView);
        initRecyclerView();
        getThings();
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               getThings();
            }
        });

        return rootView;
    }

    private void initRecyclerView() {
        recyclerAdapter = new PizzaRecyclerAdapter();
        recyclerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<Pizza>() {
            @Override
            public void onItemClick(View view, Pizza item, boolean isLongClick) {
                if (isLongClick) {
                    MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.X);
                } else {
                    loadToppings(view, item);

                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);

        MainActivity.of(getActivity()).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.launchOverlay(MainActivity.of(getActivity()), v, getActivity().findViewById(R.id.base_fragment_container));
            }
        });
    }

    private void loadToppings(final View view, final Pizza item) {
        new DataManager(getActivity()).getToppingsByPizzaId(item.getId(), new ServiceCallback() {
            @Override
            public void onSuccess(Object response) {
                ArrayList<ToppingByPizza> toppingByPizzaList = (ArrayList<ToppingByPizza>) response;
                ArrayList<String> addyExtras = new ArrayList<String>();

                for (int i = 0; i < toppingByPizzaList.size(); i++) {
                    addyExtras.add(toppingByPizzaList.get(i).getName());
                }
                Navigator.launchDetail(MainActivity.of(getActivity()), view, item, addyExtras, recyclerView);
            }

            @Override
            public void onError(Object networkError) {

            }

            @Override
            public void onPreExecute() {

            }
        });
    }

    @Override
    public boolean onBeforeBack() {

        return super.onBeforeBack();
    }

    public List<Pizza> getThings() {
        new DataManager(getActivity()).getPizzas(new ServiceCallback() {
            @Override
            public void onSuccess(Object response) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                pizzas = (List<Pizza>) response;
                recyclerAdapter.updateList(pizzas);

            }

            @Override
            public void onError(Object networkError) {
                pizzas = null;
            }

            @Override
            public void onPreExecute() {

            }
        });
        return pizzas;
    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            getThings();
        } else {
            //TODO DISPLAY NO VIEW
        }
    }
}

