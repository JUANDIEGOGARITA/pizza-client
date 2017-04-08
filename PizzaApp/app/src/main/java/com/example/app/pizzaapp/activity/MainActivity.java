package com.example.app.pizzaapp.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.adapter.TabAdapter;
import com.example.app.pizzaapp.fragment.AddPizzaFragment;
import com.example.app.pizzaapp.fragment.PizzaDetailFragment;
import com.example.app.pizzaapp.fragment.PizzaListFragment;
import com.example.app.pizzaapp.helper.TransitionHelper;
import com.example.app.pizzaapp.receiver.NetworkStateChangeReceiver;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */
public class MainActivity extends TransitionHelper.BaseActivity implements NetworkStateChangeReceiver.InternetStateHasChange {

    protected static String BASE_FRAGMENT = "base_fragment";
    public
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public
    @Bind(R.id.tab_layout)
    TabLayout tabBar;
    public
    @Bind(R.id.material_menu_button)
    MaterialMenuView homeButton;
    public
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    public
    @Bind(R.id.fab)
    FloatingActionButton fab;

    public
    @Bind(R.id.base_fragment_background)
    View fragmentBackround;

    public
    @Bind(R.id.pager)
    ViewPager pager;

    TabAdapter pagerAdapter;

    @Bind(R.id.base_fragment_container)
    CoordinatorLayout full_screen;

    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        ButterKnife.bind(this);
        //initBaseFragment(savedInstanceState);
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        networkStateChangeReceiver.setInternetStateHasChange(this);
        snackbar = Snackbar.make(full_screen, getString(R.string.lost_internet_connection),
                Snackbar.LENGTH_INDEFINITE).setAction("", null);
        tabBar.addTab(tabBar.newTab().setText("Pizzas"));
        tabBar.addTab(tabBar.newTab().setText("Toppings"));
        tabBar.setTabGravity(TabLayout.GRAVITY_FILL);

        initToolbar();
        pagerAdapter = new TabAdapter(getSupportFragmentManager(), 2);
        pager.setAdapter(pagerAdapter);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabBar));
        tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        initMainView(savedInstanceState);
    }

    private void initMainView(Bundle savedInstanceState) {
        TransitionHelper.BaseFragment fragment = null;
        if (savedInstanceState == null) {
            fragment = getBaseFragment();
        }
         setBaseFragment(fragment);
    }

    protected TransitionHelper.BaseFragment getBaseFragment() {
        int fragmentResourceId = getIntent().getIntExtra("fragment_resource_id", 0);
        switch (fragmentResourceId) {
            case R.layout.fragment_pizza_list:
                return new PizzaListFragment();
            case R.layout.fragment_pizza_detail:
                return PizzaDetailFragment.create();
            case R.layout.fragment_overaly:
                return new AddPizzaFragment();
            default:
                return null;
        }
    }

    public void setBaseFragment(TransitionHelper.BaseFragment fragment) {
        if (fragment == null) return;
       FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base_fragment, fragment);
        transaction.commit();
    }

    private void initToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("");
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }


    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    private MaterialMenuDrawable.IconState currentIconState;

    public boolean animateHomeIcon(MaterialMenuDrawable.IconState iconState) {
        if (currentIconState == iconState) return false;
        currentIconState = iconState;
        homeButton.animateState(currentIconState);
        return true;
    }

    public void setHomeIcon(MaterialMenuDrawable.IconState iconState) {
        if (currentIconState == iconState) return;
        currentIconState = iconState;
        homeButton.setState(currentIconState);

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
     /*   TransitionHelper.BaseFragment page = (TransitionHelper.BaseFragment)
                getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + pager.getCurrentItem());
        page.networkChangedState(isInternetAvailable);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStateChangeReceiver);
    }
}