package com.example.app.pizzaapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.adapter.BaseRecyclerAdapter;
import com.example.app.pizzaapp.adapter.PizzaRecyclerAdapter;
import com.example.app.pizzaapp.datamanager.DataManager;
import com.example.app.pizzaapp.datamanager.ServiceCallback;
import com.example.app.pizzaapp.util.TransitionUtil;
import com.example.app.pizzaapp.model.Pizza;
import com.example.app.pizzaapp.util.AppContants;
import com.example.app.pizzaapp.util.Navigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class PizzaListFragment extends TransitionUtil.BaseFragment implements SearchView.OnQueryTextListener {
    @Bind(R.id.recycler)
    RecyclerView recyclerView;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.main_view)
    RelativeLayout main_view;

    @Bind(R.id.empty_view)
    RelativeLayout empty_view;

    @Bind(R.id.error_message)
    TextView error_message;

    PizzaRecyclerAdapter recyclerAdapter;

    List<Pizza> pizzaList;
    String itemDescription;

    public PizzaListFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPizzas();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pizza_list, container, false);
        ButterKnife.bind(this, rootView);
        pizzaList = new ArrayList<>();
        itemDescription = getActivity().getIntent().getStringExtra("item_text");
        initRecyclerView();
        ((MainActivity) getActivity()).getToolbarTitle().setText(itemDescription);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPizzas();
            }
        });
        initSearchView();
        initBodyText();
        return rootView;
    }

    private void initBodyText() {
        recyclerView.setAlpha(0);
        recyclerView.setTranslationY(100);
        MainActivity.of(getActivity()).getFabButton().setAlpha(0.f);
        MainActivity.of(getActivity()).getFabButton().setTranslationY(100);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                recyclerView.animate()
                        .alpha(1)
                        .setStartDelay(Navigator.ANIM_DURATION / 3)
                        .setDuration(Navigator.ANIM_DURATION * 5)
                        .setInterpolator(new DecelerateInterpolator(9))
                        .translationY(0)
                        .start();
                MainActivity.of(getActivity()).getFabButton().animate()
                        .alpha(1)
                        .setStartDelay(Navigator.ANIM_DURATION / 3)
                        .setDuration(Navigator.ANIM_DURATION * 5)
                        .setInterpolator(new DecelerateInterpolator(9))
                        .translationY(0)
                        .start();
            }
        }, 400);
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

        MainActivity.of(getActivity()).getFabButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.launchAddPizzaFragment(MainActivity.of(getActivity()), v, getActivity().findViewById(R.id.base_fragment_container), getPizzaNameList());
            }
        });
    }

    private ArrayList<String> getPizzaNameList() {
        ArrayList<String> pizzaNameList = new ArrayList<>();
        if (!pizzaList.isEmpty()) {
            for (Pizza pizza : pizzaList) {
                pizzaNameList.add(pizza.getName());
            }
        }
        return pizzaNameList;
    }

    @Override
    public void onBeforeViewShows(View contentView) {
        ViewCompat.setTransitionName(((MainActivity) getActivity()).getToolbarTitle(), "title_element");
        ViewCompat.setTransitionName(main_view, "option_wrapper");

        TransitionUtil.excludeEnterTarget(getActivity(), R.id.toolbar_container, true);
        TransitionUtil.excludeEnterTarget(getActivity(), R.id.full_screen, true);
    }

    @Override
    public boolean onBeforeBack() {
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
        TransitionUtil.fadeThenFinish(recyclerView, getActivity());
        TransitionUtil.fadeThenFinish(MainActivity.of(getActivity()).getFabButton(), getActivity());

        return false;
    }

    @Override
    public void onBeforeEnter(View contentView) {
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
    }


    public void loadPizzas() {
        if (isInternetAvailable()) {
            mRefreshLayout.setRefreshing(true);
            new DataManager(getActivity()).getPizzas(new ServiceCallback() {
                @Override
                public void onSuccess(Object status, Object response) {
                    if (mRefreshLayout.isRefreshing()) {
                        mRefreshLayout.setRefreshing(false);
                    }
                    empty_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    if (Integer.parseInt(status.toString()) == AppContants.OK_HTTP_RESPONSE) {
                        pizzaList = (List<Pizza>) response;
                        recyclerAdapter.updateList(pizzaList);
                    } else {
                        showEmptyOrErrorView(status.toString() + " Internal Server Error");
                    }
                }

                @Override
                public void onError(Object networkError) {
                    showEmptyOrErrorView(networkError.toString());
                }

                @Override
                public void onPreExecute() {

                }
            });
        } else {
            networkChangedState(false);
        }
    }


    public void showEmptyOrErrorView(String message) {
        recyclerView.setVisibility(View.GONE);
        empty_view.setVisibility(View.VISIBLE);
        error_message.setText(message);

    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            MainActivity.of(getActivity()).getFabButton().setEnabled(true);
            loadPizzas();
        } else {
            showEmptyOrErrorView("No internet connection available");
            MainActivity.of(getActivity()).getFabButton().setEnabled(false);
            if (!MainActivity.of(getActivity()).getSnackbar().isShown()) {
                MainActivity.of(getActivity()).getSnackbar().show();
            }
        }
    }

    private void initSearchView() {
        final SearchView searchView = MainActivity.of(getActivity()).getSearchView();
        searchView.setVisibility(View.VISIBLE);
        searchView.setOnQueryTextListener(this);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.of(getActivity()).getToolbarTitle().setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                MainActivity.of(getActivity()).getToolbarTitle().setVisibility(View.VISIBLE);
                recyclerAdapter.updateList(pizzaList);
                return false;
            }
        });
    }

    @Override
    public boolean onQueryTextChange(String query) {
        recyclerAdapter.updateList(filter(query));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Pizza> filter(String query) {
        query = query.toLowerCase();
        final List<Pizza> filteredModelList = new ArrayList<>();
        for (Pizza model : pizzaList) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}