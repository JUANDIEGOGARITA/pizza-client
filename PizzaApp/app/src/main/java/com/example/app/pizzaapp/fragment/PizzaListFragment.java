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
import com.example.app.pizzaapp.model.Pizza;
import com.example.app.pizzaapp.util.AppContants;
import com.example.app.pizzaapp.util.Initializer;
import com.example.app.pizzaapp.util.Navigator;
import com.example.app.pizzaapp.util.TransitionUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class PizzaListFragment extends TransitionUtil.BaseFragment implements SearchView.OnQueryTextListener, Initializer {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.refresh_view)
    SwipeRefreshLayout mRefreshView;

    @Bind(R.id.main_view)
    RelativeLayout mMainView;

    @Bind(R.id.empty_view)
    RelativeLayout mEmptyView;

    @Bind(R.id.error_view)
    RelativeLayout mErrorView;

    @Bind(R.id.error_message)
    TextView mErrorMessage;

    PizzaRecyclerAdapter mRecyclerAdapter;

    List<Pizza> mPizzaList;


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
        init();
        return rootView;
    }

    private void initBodyText() {
        mRecyclerView.setAlpha(0);
        mRecyclerView.setTranslationY(100);
        MainActivity.of(getActivity()).getFabButton().setAlpha(0.f);
        MainActivity.of(getActivity()).getFabButton().setTranslationY(100);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mRecyclerView.animate()
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
        mRecyclerAdapter = new PizzaRecyclerAdapter();
        mRecyclerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<Pizza>() {
            @Override
            public void onItemClick(View view, Pizza item, boolean isLongClick) {
                if (isLongClick) {
                    MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.X);
                } else {
                    Navigator.launchDetailProductFragment(MainActivity.of(getActivity()), view, item, mRecyclerView);
                }
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRecyclerAdapter);

        MainActivity.of(getActivity()).getFabButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.launchAddProductFragment(MainActivity.of(getActivity()),
                        getActivity().findViewById(R.id.base_fragment_container), getPizzaNameList(), R.layout.fragment_add_pizza);
            }
        });
    }

    private ArrayList<String> getPizzaNameList() {
        ArrayList<String> pizzaNameList = new ArrayList<>();
        if (!mPizzaList.isEmpty()) {
            for (Pizza pizza : mPizzaList) {
                pizzaNameList.add(pizza.getName());
            }
        }
        return pizzaNameList;
    }

    @Override
    public void onBeforeViewShows(View contentView) {
        ViewCompat.setTransitionName(((MainActivity) getActivity()).getToolbarTitle(), getString(R.string.title_element));
        ViewCompat.setTransitionName(mMainView, "option_wrapper");

        TransitionUtil.excludeEnterTarget(getActivity(), R.id.toolbar_container, true);
        TransitionUtil.excludeEnterTarget(getActivity(), R.id.full_screen, true);
    }

    @Override
    public boolean onBeforeBack() {
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
        TransitionUtil.fadeThenFinish(mRecyclerView, getActivity());
        TransitionUtil.fadeThenFinish(MainActivity.of(getActivity()).getFabButton(), getActivity());
        return false;
    }

    @Override
    public void onBeforeEnter(View contentView) {
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
    }

    public void loadPizzas() {
        if (isInternetAvailable()) {
            mRefreshView.setRefreshing(true);
            new DataManager(getActivity()).getPizzas(new ServiceCallback() {
                @Override
                public void onSuccess(Object status, Object response) {
                    if (mRefreshView.isRefreshing()) {
                        mRefreshView.setRefreshing(false);
                    }
                    mErrorView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    if (Integer.parseInt(status.toString()) == AppContants.OK_HTTP_RESPONSE) {
                        mPizzaList = (List<Pizza>) response;
                        if (mPizzaList.isEmpty()) {
                            showEmptyView();
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                            mRecyclerAdapter.updateList(mPizzaList);
                        }
                    } else {
                        showErrorView(status.toString() + " " + getString(R.string.internal_server_error));
                    }
                }

                @Override
                public void onError(Object networkError) {
                    showErrorView(networkError.toString());
                }

                @Override
                public void onPreExecute() {

                }
            });
        } else {
            networkChangedState(false);
        }
    }


    public void showErrorView(String message) {
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mErrorMessage.setText(message);
    }

    private void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            MainActivity.of(getActivity()).getFabButton().setEnabled(true);
            loadPizzas();
        } else {
            showErrorView("No internet connection available");
            MainActivity.of(getActivity()).getFabButton().setEnabled(false);
            if (!MainActivity.of(getActivity()).getSnackBar().isShown()) {
                MainActivity.of(getActivity()).getSnackBar().show();
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
                mRecyclerAdapter.updateList(mPizzaList);
                return false;
            }
        });
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mRecyclerAdapter.updateList(filter(query));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Pizza> filter(String query) {
        query = query.toLowerCase();
        final List<Pizza> filteredModelList = new ArrayList<>();
        for (Pizza model : mPizzaList) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public void init() {
        initBackendComponents();
        initFrontendComponents();
    }

    @Override
    public void initFrontendComponents() {
        initRecyclerView();
        ((MainActivity) getActivity()).getToolbarTitle().setText(getActivity().getIntent().getStringExtra("item_text"));
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPizzas();
            }
        });
        initSearchView();
        initBodyText();
    }

    @Override
    public void initBackendComponents() {
        mPizzaList = new ArrayList<>();
    }
}