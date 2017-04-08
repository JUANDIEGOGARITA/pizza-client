package com.example.app.pizzaapp.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
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
import com.example.app.pizzaapp.util.Navigator;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PizzaListFragment extends TransitionHelper.BaseFragment {
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    PizzaRecyclerAdapter recyclerAdapter;
    List<Pizza> pizzas;

    public PizzaListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pizza_list, container, false);
        ButterKnife.bind(this, rootView);
        initRecyclerView();
        getThings();
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
                    Navigator.launchDetail(MainActivity.of(getActivity()), view, item, recyclerView);
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

    @Override
    public boolean onBeforeBack() {
        MainActivity activity = MainActivity.of(getActivity());
        if (!activity.animateHomeIcon(MaterialMenuDrawable.IconState.BURGER)) {
            activity.drawerLayout.openDrawer(Gravity.START);
        }
        return super.onBeforeBack();
    }

    public  List<Pizza> getThings() {
         new DataManager().getPizzas(new ServiceCallback() {
            @Override
            public void onSuccess(Object response) {
                pizzas =  (List<Pizza>) response;
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
    //    recyclerAdapter.notifyDataSetChanged();
        return pizzas;
    }

}

