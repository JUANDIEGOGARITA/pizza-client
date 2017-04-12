package com.example.app.pizzaapp.activity;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SearchView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.fragment.AddPizzaFragment;
import com.example.app.pizzaapp.fragment.AddToppingFragment;
import com.example.app.pizzaapp.fragment.HomeFragment;
import com.example.app.pizzaapp.fragment.PizzaDetailFragment;
import com.example.app.pizzaapp.fragment.PizzaListFragment;
import com.example.app.pizzaapp.fragment.ToppingListFragment;
import com.example.app.pizzaapp.receiver.NetworkStateChangeReceiver;
import com.example.app.pizzaapp.util.BitmapUtil;
import com.example.app.pizzaapp.util.TransitionUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */
public class MainActivity extends TransitionUtil.BaseActivity implements NetworkStateChangeReceiver.InternetStateHasChange {

    protected static String BASE_FRAGMENT = "base_fragment";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_search)
    SearchView mSearchView;

    @Bind(R.id.material_menu_button)
    MaterialMenuView mToolbarButton;

    @Bind(R.id.toolbar_title)
    AppCompatTextView mToolbarTitle;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Bind(R.id.base_fragment_background)
    View mFragmentBackground;


    @Bind(R.id.base_fragment_container)
    CoordinatorLayout mFullScreen;

    private NetworkStateChangeReceiver mNetworkStateChangeReceiver;
    private Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        ButterKnife.bind(this);
        ;
        initToolbar();
        mNetworkStateChangeReceiver = new NetworkStateChangeReceiver();
        mNetworkStateChangeReceiver.setInternetStateHasChange(this);
        snackbar = Snackbar.make(mFullScreen, getString(R.string.lost_internet_connection),
                Snackbar.LENGTH_INDEFINITE).setAction("", null);

        initMainView(savedInstanceState);
    }

    private void initMainView(Bundle savedInstanceState) {
        if (getIntent().hasExtra("bitmap_id")) {
            mFragmentBackground.setBackground(new BitmapDrawable(getResources(), BitmapUtil.fetchBitmapFromIntent(getIntent())));
        }
        TransitionUtil.BaseFragment fragment = null;
        if (savedInstanceState == null) {
            fragment = getBaseFragment();
        }
        setBaseFragment(fragment);
    }

    protected TransitionUtil.BaseFragment getBaseFragment() {
        int fragmentResourceId = getIntent().getIntExtra("fragment_resource_id", 0);
        switch (fragmentResourceId) {
            case R.layout.fragment_pizza_list:
                return new PizzaListFragment();
            case R.layout.fragment_pizza_detail:
                return PizzaDetailFragment.create();
            case R.layout.fragment_add_pizza:
                return new AddPizzaFragment();
            case R.layout.fragment_topping_list:
                return new ToppingListFragment();
            case R.layout.fragment_add_topping:
                return new AddToppingFragment();
            default:
                return new HomeFragment();
        }
    }

    public void setBaseFragment(TransitionUtil.BaseFragment fragment) {
        if (fragment == null) return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base_fragment, fragment, BASE_FRAGMENT);
        transaction.commit();
    }

    private void initToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("");
            mToolbarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    public void setToolbarTitleText(String text) {
        mToolbarTitle.setText(text);
    }

    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    private MaterialMenuDrawable.IconState currentIconState;

    public boolean animateHomeIcon(MaterialMenuDrawable.IconState iconState) {
        if (currentIconState == iconState) return false;
        currentIconState = iconState;
        mToolbarButton.animateState(currentIconState);
        return true;
    }

    public void setHomeIcon(MaterialMenuDrawable.IconState iconState) {
        if (currentIconState == iconState) return;
        currentIconState = iconState;
        mToolbarButton.setState(currentIconState);

    }

    @Override
    public boolean onBeforeBack() {
        ActivityCompat.finishAfterTransition(this);
        return false;
    }

    public static MainActivity of(Activity activity) {
        return (MainActivity) activity;
    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
        } else {
            snackbar.show();

        }
        TransitionUtil.BaseFragment fragment = (TransitionUtil.BaseFragment) getSupportFragmentManager().findFragmentByTag(BASE_FRAGMENT);
        fragment.networkChangedState(isInternetAvailable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkStateChangeReceiver);
    }


    public void goBack() {
        onBackPressed();
    }

    //Getters
    public FloatingActionButton getFabButton() {
        return mFab;
    }

    public NetworkStateChangeReceiver getNetworkStateChangeReceiver() {
        return mNetworkStateChangeReceiver;
    }

    public AppCompatTextView getToolbarTitle() {
        return mToolbarTitle;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public MaterialMenuView getToolbarButton() {
        return mToolbarButton;
    }

    public Snackbar getSnackbar() {
        return snackbar;
    }

    public SearchView getSearchView() {
        return mSearchView;
    }

    public View getFragmentBackground() {
        return mFragmentBackground;
    }
}