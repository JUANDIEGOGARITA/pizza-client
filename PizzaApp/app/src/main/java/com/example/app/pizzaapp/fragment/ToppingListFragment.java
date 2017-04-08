
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
import com.example.app.pizzaapp.adapter.ToppingRecyclerAdapter;
import com.example.app.pizzaapp.datamanager.DataManager;
import com.example.app.pizzaapp.datamanager.ServiceCallback;
import com.example.app.pizzaapp.helper.TransitionHelper;
import com.example.app.pizzaapp.model.Pizza;
import com.example.app.pizzaapp.model.Topping;
import com.example.app.pizzaapp.model.ToppingByPizza;
import com.example.app.pizzaapp.util.Navigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class ToppingListFragment extends TransitionHelper.BaseFragment {
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    ToppingRecyclerAdapter recyclerAdapter;
    List<Pizza> pizzas;

    public ToppingListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topping_list, container, false);
        ButterKnife.bind(this, rootView);
        initRecyclerView();
        loadToppings();
        MainActivity activity = MainActivity.of(getActivity());
        activity.homeButton.setVisibility(View.GONE);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadToppings();
            }
        });

        return rootView;
    }

    private void initRecyclerView() {
        recyclerAdapter = new ToppingRecyclerAdapter();
        recyclerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<String>() {
            @Override
            public void onItemClick(View view, String item, boolean isLongClick) {
                if (isLongClick) {
                    MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.X);
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

    private void loadToppings() {
        new DataManager(getActivity()).getToppings(new ServiceCallback() {
            @Override
            public void onSuccess(Object response) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                ArrayList<Topping> toppingArrayList = (ArrayList<Topping>) response;
                ArrayList<String> addyExtras = new ArrayList<String>();

                for (int i = 0; i < toppingArrayList.size(); i++) {
                    addyExtras.add(toppingArrayList.get(i).getName());
                }
                recyclerAdapter.updateList(addyExtras);
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


    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            loadToppings();
        } else {
            //TODO DISPLAY NO VIEW
        }
    }
}