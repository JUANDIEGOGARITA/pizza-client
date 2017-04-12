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
import android.widget.LinearLayout;
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
import com.example.app.pizzaapp.util.Navigator;
import com.example.app.pizzaapp.util.TransitionUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class PizzaDetailFragment extends TransitionUtil.BaseFragment implements SearchView.OnQueryTextListener {

    @Bind(R.id.detail_title)
    AppCompatTextView titleTextView;

    @Bind(R.id.detail_body)
    TextView detailBodyTextView;

    @Bind(R.id.recycler)
    RecyclerView recyclerView;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.pizza_details)
    LinearLayout pizza_details;

    @Bind(R.id.detail_layout)
    CardView detail_layout;

    @Bind(R.id.error_view)
    RelativeLayout mErrorView;

    @Bind(R.id.error_message)
    TextView error_message;

    @Bind(R.id.empty_view)
    RelativeLayout mEmptyView;

    ToppingRecyclerAdapter recyclerAdapter;
    ArrayList<String> list;

    ArrayList<Integer> listIds;
    List<Topping> toppingList;
    int pizzaId;

    public static PizzaDetailFragment create() {
        return new PizzaDetailFragment();
    }

    public PizzaDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pizza_detail, container, false);
        ButterKnife.bind(this, rootView);
        MainActivity activity = MainActivity.of(getActivity());
        toppingList = new ArrayList<>();
        activity.getToolbarButton().setVisibility(View.VISIBLE);
        String itemText = getActivity().getIntent().getStringExtra("item_text");
        String itemDescription = getActivity().getIntent().getStringExtra("item_description");
        list = getActivity().getIntent().getStringArrayListExtra("list");
        pizzaId = Integer.parseInt(getActivity().getIntent().getStringExtra("pizza_id"));
        ((MainActivity) getActivity()).getToolbarTitle().setText(itemText);
        detailBodyTextView.setText(itemDescription);
        initRecyclerView();
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadToppingsByPizza();
            }
        });
        initDetailBody();
        initSearchView();
        initBodyText();
        return rootView;
    }

    private void initBodyText() {
        recyclerView.setAlpha(0);
        recyclerView.setTranslationY(100);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                recyclerView.animate()
                        .alpha(1)
                        .setStartDelay(Navigator.ANIM_DURATION / 3)
                        .setDuration(Navigator.ANIM_DURATION * 5)
                        .setInterpolator(new DecelerateInterpolator(9))
                        .translationY(0)
                        .start();
            }
        }, 200);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadToppingsByPizza();
    }


    private void loadToppingsByPizza() {
        mRefreshLayout.setRefreshing(true);
        new DataManager(getActivity()).getToppingsByPizzaId(pizzaId, new ServiceCallback() {
            @Override
            public void onSuccess(Object status, Object response) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                toppingList = new ArrayList<Topping>();
                mErrorView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (Integer.parseInt(status.toString()) == AppContants.OK_HTTP_RESPONSE) {
                    listIds = new ArrayList<>();
                    ArrayList<GetToppingByPizzaResult> getToppingByPizzaResultList = (ArrayList<GetToppingByPizzaResult>) response;
                    for (GetToppingByPizzaResult model : getToppingByPizzaResultList) {
                        toppingList.add(new Topping(model.getId(), model.getName()));
                        listIds.add(model.getToppingId());
                    }
                  /*  for (int i = 0; i < getToppingByPizzaResultList.size(); i++) {
                        GetToppingByPizzaResult g = getToppingByPizzaResultList.get(i);
                        toppingList.add(i, new Topping(g.getId(), g.getName()));
                        listIds.add(getToppingByPizzaResultList.get(i).getToppingId());
                    }*/
                    if (toppingList.isEmpty()) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                        recyclerAdapter.updateList(toppingList);
                    }
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
    }

    private void initRecyclerView() {
        recyclerAdapter = new ToppingRecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);

        MainActivity.of(getActivity()).getFabButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cx = v.getLeft() + (v.getWidth() / 2); //middle of button
                int cy = v.getTop() + (v.getHeight() / 2); //middle of button
                int radius = (int) Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2)); //hypotenuse to top left
                Navigator.launchToppingList(MainActivity.of(getActivity()), "Add toppings to your Pizza",
                        v, cx, cy, radius, R.layout.fragment_topping_list, pizzaId, listIds);
            }
        });
    }

    private void initDetailBody() {
        detailBodyTextView.setAlpha(0);
        recyclerView.setAlpha(0);
        titleTextView.setAlpha(0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                detailBodyTextView.animate().alpha(1).start();
                recyclerView.animate().alpha(1).start();
                titleTextView.animate().alpha(1).start();
            }
        }, 500);
    }

    @Override
    public void onBeforeViewShows(View contentView) {
        ViewCompat.setTransitionName(detail_layout, "detail_element");
        ViewCompat.setTransitionName(MainActivity.of(getActivity()).getFabButton(), "fab");
        ViewCompat.setTransitionName(((MainActivity) getActivity()).getToolbarTitle(), "detail_title");

        TransitionUtil.excludeEnterTarget(getActivity(), R.id.toolbar_container, true);
        TransitionUtil.excludeEnterTarget(getActivity(), R.id.full_screen, true);

    }

    @Override
    public void onBeforeEnter(View contentView) {
       // detail_layout.setVisibility(View.INVISIBLE);
        MainActivity.of(getActivity()).getFragmentBackground().animate().scaleX(.92f).scaleY(.92f).alpha(.6f).setDuration(Navigator.ANIM_DURATION).setInterpolator(new AccelerateInterpolator()).start();
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
    }

    @Override
    public boolean onBeforeBack() {
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
        MainActivity.of(getActivity()).getFragmentBackground().animate().scaleX(1).scaleY(1).alpha(1).translationY(0).setDuration(Navigator.ANIM_DURATION / 4).setInterpolator(new DecelerateInterpolator()).start();
        TransitionUtil.fadeThenFinish(detailBodyTextView, getActivity());
        TransitionUtil.fadeThenFinish(titleTextView, getActivity());

        return false;
    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            MainActivity.of(getActivity()).getFabButton().setEnabled(true);
            loadToppingsByPizza();
        } else {
            showEmptyOrErrorView("No internet connection available");
            MainActivity.of(getActivity()).getFabButton().setEnabled(false);
            if (!MainActivity.of(getActivity()).getSnackBar().isShown()) {
                MainActivity.of(getActivity()).getSnackBar().show();
            }
        }
    }

    public void showEmptyOrErrorView(String message) {
        recyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        error_message.setText(message);

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
                recyclerAdapter.updateList(toppingList);
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

    private List<Topping> filter(String query) {
        query = query.toLowerCase();
        final List<Topping> filteredModelList = new ArrayList<>();
        for (Topping model : toppingList) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

}