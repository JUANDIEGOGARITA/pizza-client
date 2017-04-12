
package com.example.app.pizzaapp.fragment;

import android.animation.Animator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.adapter.BaseRecyclerAdapter;
import com.example.app.pizzaapp.adapter.ToppingRecyclerAdapter;
import com.example.app.pizzaapp.datamanager.DataManager;
import com.example.app.pizzaapp.datamanager.ServiceCallback;
import com.example.app.pizzaapp.model.PostToppingByPizza;
import com.example.app.pizzaapp.model.Topping;
import com.example.app.pizzaapp.util.AppContants;
import com.example.app.pizzaapp.util.DialogUtil;
import com.example.app.pizzaapp.util.DialogUtilListener;
import com.example.app.pizzaapp.util.Navigator;
import com.example.app.pizzaapp.util.TransitionUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class ToppingListFragment extends TransitionUtil.BaseFragment implements SearchView.OnQueryTextListener {

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
    ToppingRecyclerAdapter recyclerAdapter;

    int a, b, c;
    List<Integer> toppingIdList, toppingIdListByPizza;
    ArrayList<Topping> mToppingList;

    boolean isCheckAvailable = false;
    int mPizzaId;

    boolean isLastOne = false;

    String itemDescription;

    CheckListener mCheckListener = new CheckListener() {
        @Override
        public void onToppingChecked(int toppingId) {
            if (!toppingIdList.contains(toppingId)) {
                toppingIdList.add(toppingId);
            }
            validateFabButtonState();
        }

        @Override
        public void onToppingUnChecked(int toppingId) {
            if (toppingIdList.contains(toppingId)) {
                for (int i = 0; i < toppingIdList.size(); i++) {
                    if (toppingIdList.get(i) == toppingId) ;
                    toppingIdList.remove(i);
                }
            }
            validateFabButtonState();
        }
    };

    DialogUtilListener mDialogListener = new DialogUtilListener() {
        @Override
        public void onPositiveButtonClicked() {
            postToppingsByPizza();
        }

        @Override
        public void onNegativeButtonClicked(DialogInterface d) {
            d.dismiss();
        }
    };

    public ToppingListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topping_list, container, false);
        ButterKnife.bind(this, rootView);
        toppingIdList = new ArrayList<>();
        a = getActivity().getIntent().getIntExtra("a", 0);
        b = getActivity().getIntent().getIntExtra("b", 0);
        c = getActivity().getIntent().getIntExtra("c", 0);
        //TODO GET FLAG EXTRA
        itemDescription = getActivity().getIntent().getStringExtra("item_text");
        mPizzaId = getActivity().getIntent().getIntExtra("pizzaId", -1);
        toppingIdListByPizza = getActivity().getIntent().getIntegerArrayListExtra("toppingIds");

        if (mPizzaId != -1) {
            isCheckAvailable = true;
        }

        ((MainActivity) getActivity()).getToolbarTitle().setText(itemDescription);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadToppings();
            }
        });

        initRecyclerView();
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
        if (isCheckAvailable) {
            recyclerAdapter = new ToppingRecyclerAdapter(mCheckListener, isCheckAvailable);
        } else {
            recyclerAdapter = new ToppingRecyclerAdapter();
        }

        recyclerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<Topping>() {
            @Override
            public void onItemClick(View view, Topping item, boolean isLongClick) {
                if (isLongClick) {
                    MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.X);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);

        if (isCheckAvailable) {
            validateFabButtonState();
        }
        MainActivity.of(getActivity()).getFabButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.of(getActivity()).getSearchView().onActionViewCollapsed();
                ((MainActivity) getActivity()).getToolbarTitle().setVisibility(View.VISIBLE);
                if (!toppingIdList.isEmpty()) {
                    postToppingsByPizza();
                } else {
                    Navigator.launchAddToppingFragment(MainActivity.of(getActivity()), v, getActivity().findViewById(R.id.base_fragment_container), getToppingNameList());
                }
            }
        });
    }

    private ArrayList<String> getToppingNameList() {
        ArrayList<String> toppingNameList = new ArrayList<>();
        if (!mToppingList.isEmpty()) {
            for (Topping topping : mToppingList) {
                toppingNameList.add(topping.getName());
            }
        }
        return toppingNameList;
    }

    private void loadToppings() {
        if (isInternetAvailable()) {
            mRefreshLayout.setRefreshing(true);
            new DataManager(getActivity()).getToppings(new ServiceCallback() {
                @Override
                public void onSuccess(Object status, Object response) {
                    if (mRefreshLayout.isRefreshing()) {
                        mRefreshLayout.setRefreshing(false);
                    }
                    empty_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    if (Integer.parseInt(status.toString()) == AppContants.OK_HTTP_RESPONSE) {
                        mToppingList = (ArrayList<Topping>) response;
                        ArrayList<Topping> responseList = mToppingList;


                        ArrayList<Topping> listToDisplay = getFilteredByPizzaId(mPizzaId, responseList);
                        if (listToDisplay.isEmpty()) {
                            showEmptyOrErrorView("No more toppings to add");
                        } else {
                            recyclerAdapter.updateList(listToDisplay);
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
        } else {
            networkChangedState(false);
        }


        new DataManager(getActivity()).getToppings(new ServiceCallback() {
            @Override
            public void onSuccess(Object status, Object response) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }

                ArrayList<Topping> responseList = (ArrayList<Topping>) response;


                ArrayList<Topping> listToDisplay = getFilteredByPizzaId(mPizzaId, responseList);
                if (listToDisplay.isEmpty()) {
                    //TODO IF TOPPINGLIST IS EMPTY DISPLAY MESSAGE
                } else {
                    recyclerAdapter.updateList(listToDisplay);
                }
            }

            @Override
            public void onError(Object networkError) {

            }

            @Override
            public void onPreExecute() {

            }
        });
    }

    private ArrayList<Topping> getFilteredByPizzaId(int pizzaId, ArrayList<Topping> responseList) {
        if (pizzaId == -1) {
            return responseList;
        } else {
            ArrayList<Topping> responseListFiltered = new ArrayList<>();
            for (int i = 0; i < responseList.size(); i++) {
                Topping currentTopping = responseList.get(i);
                if (!toppingIdListByPizza.contains(Integer.parseInt(currentTopping.getId()))) {
                    responseListFiltered.add(currentTopping);
                }
            }
            return responseListFiltered;
        }
    }

    private void postToppingsByPizza() {

        DataManager dataManager = new DataManager(getActivity());

        for (int i = 0; i < toppingIdList.size(); i++) {
            if (i == toppingIdList.size() - 1) {
                isLastOne = true;
            }
            dataManager.postToppingByPizza(mPizzaId, new PostToppingByPizza(toppingIdList.get(i)), new ServiceCallback() {
                @Override
                public void onSuccess(Object status, Object response) {
                    if (Integer.parseInt(status.toString()) == AppContants.OK_HTTP_RESPONSE) {
                        //TODO ADD ERROR VALIDATION
                        if (isLastOne) {
                            MainActivity.of(getActivity()).goBack();
                        }
                    } else {
                        showErrorDialog(status.toString() + " Internal Server Error");
                    }
                }

                @Override
                public void onError(Object networkError) {
                    showErrorDialog(networkError.toString());
                }

                @Override
                public void onPreExecute() {

                }
            });
        }
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

    private void showErrorDialog(String errorMessage) {
        DialogUtil.showErrorDialog(mDialogListener, getActivity(), errorMessage);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadToppings();
    }

    public void showEmptyOrErrorView(String message) {
        recyclerView.setVisibility(View.GONE);
        empty_view.setVisibility(View.VISIBLE);
        error_message.setText(message);

    }

    private void validateFabButtonState() {
        if (toppingIdList.isEmpty()) {
            mRefreshLayout.setEnabled(true);
            MainActivity.of(getActivity()).getFabButton().setImageResource(R.mipmap.ic_add);

        } else {
            mRefreshLayout.setEnabled(false);
            MainActivity.of(getActivity()).getFabButton().setImageResource(R.mipmap.ic_check);
        }
    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            MainActivity.of(getActivity()).getFabButton().setEnabled(true);
            loadToppings();
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
                recyclerAdapter.updateList(mToppingList);
                return false;
            }
        });
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (!mToppingList.isEmpty()) {
            recyclerAdapter.updateList(filter(query));
        }
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
}