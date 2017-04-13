package com.example.app.pizzaapp.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.adapter.ToppingRecyclerAdapter;
import com.example.app.pizzaapp.datamanager.DataManager;
import com.example.app.pizzaapp.datamanager.ServiceCallback;
import com.example.app.pizzaapp.model.GetToppingByPizzaResult;
import com.example.app.pizzaapp.model.Topping;
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

public class PizzaDetailFragment extends TransitionUtil.BaseFragment implements SearchView.OnQueryTextListener, Initializer {

    @Bind(R.id.description_title)
    AppCompatTextView mDescriptionTitle;

    @Bind(R.id.description_body)
    TextView mDescriptionBody;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.refresh_view)
    SwipeRefreshLayout mRefreshView;

    @Bind(R.id.main_view)
    CardView mMainView;

    @Bind(R.id.error_view)
    RelativeLayout mErrorView;

    @Bind(R.id.error_message)
    TextView mErrorMessage;

    @Bind(R.id.empty_view)
    RelativeLayout mEmptyView;

    ToppingRecyclerAdapter mRecyclerAdapter;

    ArrayList<Integer> mToppingIdList;
    List<Topping> mToppingList;
    int mPizzaId;

    public PizzaDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pizza_detail, container, false);
        ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadToppingsByPizza();
    }


    private void loadToppingsByPizza() {
        mRefreshView.setRefreshing(true);
        new DataManager(getActivity()).getToppingsByPizzaId(mPizzaId, new ServiceCallback() {
            @Override
            public void onSuccess(Object status, Object response) {
                if (mRefreshView.isRefreshing()) {
                    mRefreshView.setRefreshing(false);
                }
                mToppingList = new ArrayList<>();
                mErrorView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);

                if (Integer.parseInt(status.toString()) == AppContants.OK_HTTP_RESPONSE) {
                    mToppingIdList = new ArrayList<>();
                    ArrayList<GetToppingByPizzaResult> getToppingByPizzaResultList = (ArrayList<GetToppingByPizzaResult>) response;
                    for (GetToppingByPizzaResult model : getToppingByPizzaResultList) {
                        mToppingList.add(new Topping(model.getId(), model.getName()));
                        mToppingIdList.add(model.getToppingId());
                    }
                    if (mToppingList.isEmpty()) {
                        showEmptyView();
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                        mRecyclerAdapter.updateList(mToppingList);
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
    }

    private void initRecyclerView() {
        mRecyclerAdapter = new ToppingRecyclerAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRecyclerAdapter);

        MainActivity.of(getActivity()).getFabButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.launchToppingList(MainActivity.of(getActivity()), "Add toppings to your Pizza",
                        v, R.layout.fragment_topping_list, mPizzaId, mToppingIdList);
            }
        });
    }

    private void initBodyText() {
        mDescriptionBody.setAlpha(0);
        mDescriptionTitle.setAlpha(0);
        mRecyclerView.setAlpha(0);
        mRecyclerView.setTranslationY(100);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mRecyclerView.animate()
                        .alpha(1)
                        .setStartDelay(Navigator.ANIM_DURATION / 3)
                        .setDuration(Navigator.ANIM_DURATION * 5)
                        .setInterpolator(new DecelerateInterpolator(9))
                        .translationY(0)
                        .start();
                mDescriptionBody.animate().alpha(1).start();
                mDescriptionTitle.animate().alpha(1).start();
            }
        }, 200);
    }


    @Override
    public void onBeforeViewShows(View contentView) {
        ViewCompat.setTransitionName(mMainView, "detail_element");
        ViewCompat.setTransitionName(MainActivity.of(getActivity()).getFabButton(), "fab");
        ViewCompat.setTransitionName(((MainActivity) getActivity()).getToolbarTitle(), "detail_title");

        TransitionUtil.excludeEnterTarget(getActivity(), R.id.toolbar_container, true);
        TransitionUtil.excludeEnterTarget(getActivity(), R.id.full_screen, true);

    }

    @Override
    public void onBeforeEnter(View contentView) {
        MainActivity.of(getActivity()).getFragmentBackground().animate().scaleX(.92f).scaleY(.92f).alpha(.6f).setDuration(Navigator.ANIM_DURATION).setInterpolator(new AccelerateInterpolator()).start();
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
    }

    @Override
    public boolean onBeforeBack() {
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
        MainActivity.of(getActivity()).getFragmentBackground().animate().scaleX(1).scaleY(1).alpha(1).translationY(0).setDuration(Navigator.ANIM_DURATION / 4).setInterpolator(new DecelerateInterpolator()).start();
        TransitionUtil.fadeThenFinish(mDescriptionBody, getActivity());
        TransitionUtil.fadeThenFinish(mDescriptionTitle, getActivity());

        return false;
    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            MainActivity.of(getActivity()).getFabButton().setEnabled(true);
            loadToppingsByPizza();
        } else {
            showErrorView("No internet connection available");
            MainActivity.of(getActivity()).getFabButton().setEnabled(false);
            if (!MainActivity.of(getActivity()).getSnackBar().isShown()) {
                MainActivity.of(getActivity()).getSnackBar().show();
            }
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
                mRecyclerAdapter.updateList(mToppingList);
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

    private List<Topping> filter(String query) {
        query = query.toLowerCase();
        final List<Topping> filteredModelList = new ArrayList<>();
        for (Topping model : mToppingList) {
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
        ((MainActivity) getActivity()).getToolbarTitle().setText(getActivity().getIntent().getStringExtra("item_text"));
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadToppingsByPizza();
            }
        });
        initRecyclerView();
        initSearchView();
        initBodyText();
    }

    @Override
    public void initBackendComponents() {
        mToppingList = new ArrayList<>();
        mPizzaId = Integer.parseInt(getActivity().getIntent().getStringExtra("pizza_id"));
        mDescriptionBody.setText(getActivity().getIntent().getStringExtra("item_description"));

    }
}