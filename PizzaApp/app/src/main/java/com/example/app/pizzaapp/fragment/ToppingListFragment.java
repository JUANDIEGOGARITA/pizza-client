
package com.example.app.pizzaapp.fragment;

import android.content.DialogInterface;
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
import com.example.app.pizzaapp.adapter.ToppingRecyclerAdapter;
import com.example.app.pizzaapp.datamanager.DataManager;
import com.example.app.pizzaapp.datamanager.ServiceCallback;
import com.example.app.pizzaapp.model.PostToppingByPizza;
import com.example.app.pizzaapp.model.Topping;
import com.example.app.pizzaapp.util.AppContants;
import com.example.app.pizzaapp.util.DialogUtil;
import com.example.app.pizzaapp.util.DialogUtilListener;
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

public class ToppingListFragment extends TransitionUtil.BaseFragment implements SearchView.OnQueryTextListener, Initializer {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.refresh_view)
    SwipeRefreshLayout mRefreshView;

    @Bind(R.id.main_view)
    RelativeLayout mMainView;

    @Bind(R.id.empty_view)
    RelativeLayout mEmptyView;

    @Bind(R.id.no_toppings_title)
    TextView mEmptyTile;

    @Bind(R.id.error_view)
    RelativeLayout mErrorView;

    @Bind(R.id.error_message)
    TextView mErrorMessage;

    ToppingRecyclerAdapter mRecyclerAdapter;

    List<Integer> mToppingIdList, mToppingIdListByPizza;
    ArrayList<Topping> mToppingList;

    boolean isCheckAvailable = false;
    int mPizzaId;
    boolean isLastOne = false;

    CheckListener mCheckListener = new CheckListener() {
        @Override
        public void onToppingChecked(int toppingId) {
            if (!mToppingIdList.contains(toppingId)) {
                mToppingIdList.add(toppingId);
            }
            validateFabButtonState();
        }

        @Override
        public void onToppingUnChecked(int toppingId) {
            if (mToppingIdList.contains(toppingId)) {
                for (int i = 0; i < mToppingIdList.size(); i++) {
                    if (mToppingIdList.get(i) == toppingId) ;
                    mToppingIdList.remove(i);
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
        if (isCheckAvailable) {
            mRecyclerAdapter = new ToppingRecyclerAdapter(mCheckListener, isCheckAvailable);
        } else {
            mRecyclerAdapter = new ToppingRecyclerAdapter();
        }

        mRecyclerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<Topping>() {
            @Override
            public void onItemClick(View view, Topping item, boolean isLongClick) {
                if (isLongClick) {
                    MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.X);
                }
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mRecyclerAdapter);

        if (isCheckAvailable) {
            validateFabButtonState();
        }
        MainActivity.of(getActivity()).getFabButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.of(getActivity()).getSearchView().onActionViewCollapsed();
                ((MainActivity) getActivity()).getToolbarTitle().setVisibility(View.VISIBLE);
                if (!mToppingIdList.isEmpty()) {
                    postToppingsByPizza();
                } else {
                    Navigator.launchAddProductFragment(MainActivity.of(getActivity()),
                            getActivity().findViewById(R.id.base_fragment_container), getToppingNameList(), R.layout.fragment_add_topping);
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
            mRefreshView.setRefreshing(true);
            new DataManager(getActivity()).getToppings(new ServiceCallback() {
                @Override
                public void onSuccess(Object status, Object response) {
                    if (mRefreshView.isRefreshing()) {
                        mRefreshView.setRefreshing(false);
                    }
                    mErrorView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);

                    if (Integer.parseInt(status.toString()) == AppContants.OK_HTTP_RESPONSE) {
                        mToppingList = (ArrayList<Topping>) response;

                        if (mToppingList.isEmpty()) {
                            showEmptyView();
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                            ArrayList<Topping> responseList = mToppingList;
                            mToppingList = getFilteredByPizzaId(mPizzaId, responseList);
                            if (mToppingList.isEmpty()) {
                                showEmptyView("No more toppings available \n Add a new one here");
                            } else {
                                mRecyclerAdapter.updateList(mToppingList);
                            }
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

    private ArrayList<Topping> getFilteredByPizzaId(int pizzaId, ArrayList<Topping> responseList) {
        if (pizzaId == -1) {
            return responseList;
        } else {
            ArrayList<Topping> responseListFiltered = new ArrayList<>();
            for (int i = 0; i < responseList.size(); i++) {
                Topping currentTopping = responseList.get(i);
                if (!mToppingIdListByPizza.contains(Integer.parseInt(currentTopping.getId()))) {
                    responseListFiltered.add(currentTopping);
                }
            }
            return responseListFiltered;
        }
    }

    private void postToppingsByPizza() {

        DataManager dataManager = new DataManager(getActivity());

        for (int i = 0; i < mToppingIdList.size(); i++) {
            if (i == mToppingIdList.size() - 1) {
                isLastOne = true;
            }
            dataManager.postToppingByPizza(mPizzaId, new PostToppingByPizza(mToppingIdList.get(i)), new ServiceCallback() {
                @Override
                public void onSuccess(Object status, Object response) {
                    if (Integer.parseInt(status.toString()) == AppContants.OK_HTTP_RESPONSE) {
                        if (isLastOne) {
                            MainActivity.of(getActivity()).goBack();
                        }
                    } else {
                        showErrorView(status.toString() + " " + getString(R.string.internal_server_error));
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

    private void showErrorDialog(String errorMessage) {
        DialogUtil.showErrorDialog(mDialogListener, getActivity(), errorMessage);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadToppings();
    }

    public void showErrorView(String message) {
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mErrorMessage.setText(message);
    }

    private void showEmptyView(String title) {
        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        if (title != null) {
            mEmptyTile.setText(title);
        }
    }

    private void showEmptyView() {
        showEmptyView(null);
    }


    private void validateFabButtonState() {
        if (mToppingIdList.isEmpty()) {
            mRefreshView.setEnabled(true);
            MainActivity.of(getActivity()).getFabButton().setImageResource(R.mipmap.ic_add);

        } else {
            mRefreshView.setEnabled(false);
            MainActivity.of(getActivity()).getFabButton().setImageResource(R.mipmap.ic_check);
        }
    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            MainActivity.of(getActivity()).getFabButton().setEnabled(true);
            loadToppings();
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
                mRecyclerAdapter.updateList(mToppingList);
                return false;
            }
        });
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (!mToppingList.isEmpty()) {
            mRecyclerAdapter.updateList(filter(query));
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

    @Override
    public void init() {
        initBackendComponents();
        initFrontendComponents();
    }

    @Override
    public void initFrontendComponents() {
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadToppings();
            }
        });
        initRecyclerView();
        initSearchView();
        initBodyText();
    }

    @Override
    public void initBackendComponents() {
        mToppingIdList = new ArrayList<>();
        mPizzaId = getActivity().getIntent().getIntExtra("pizzaId", -1);
        mToppingIdListByPizza = getActivity().getIntent().getIntegerArrayListExtra("toppingIds");
        ((MainActivity) getActivity()).getToolbarTitle().setText(getActivity().getIntent().getStringExtra("item_text"));
        if (mPizzaId != -1) {
            isCheckAvailable = true;
        }
    }
}