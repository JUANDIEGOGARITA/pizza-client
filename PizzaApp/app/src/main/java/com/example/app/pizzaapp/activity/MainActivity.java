package com.example.app.pizzaapp.activity;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SearchView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.receiver.NetworkStateChangeReceiver;
import com.example.app.pizzaapp.util.AppContants;
import com.example.app.pizzaapp.util.BitmapUtil;
import com.example.app.pizzaapp.util.Initializer;
import com.example.app.pizzaapp.util.Navigator;
import com.example.app.pizzaapp.util.TransitionUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */
public class MainActivity extends TransitionUtil.BaseActivity implements Initializer, NetworkStateChangeReceiver.InternetStateHasChange {

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
    private Snackbar mSnackBar;
    private MaterialMenuDrawable.IconState mCurrentIconState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initMainView(savedInstanceState);
    }

    private void initMainView(Bundle savedInstanceState) {
        if (getIntent().hasExtra("bitmap_id")) {
            mFragmentBackground.setBackground(new BitmapDrawable(getResources(), BitmapUtil.fetchBitmapFromIntent(getIntent())));
        }
        TransitionUtil.BaseFragment fragment = null;
        if (savedInstanceState == null) {
            fragment = Navigator.getBaseFragment(this);
        }
        Navigator.setBaseFragment(this, fragment);
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

    public boolean animateHomeIcon(MaterialMenuDrawable.IconState iconState) {
        if (mCurrentIconState == iconState) return false;
        mCurrentIconState = iconState;
        mToolbarButton.animateState(mCurrentIconState);
        return true;
    }

    public void setHomeIcon(MaterialMenuDrawable.IconState iconState) {
        if (mCurrentIconState == iconState) return;
        mCurrentIconState = iconState;
        mToolbarButton.setState(mCurrentIconState);
    }

    @Override
    public boolean onBeforeBack() {
        ActivityCompat.finishAfterTransition(this);
        return false;
    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            if (mSnackBar.isShown()) {
                mSnackBar.dismiss();
            }
        } else {
            mSnackBar.show();

        }
        TransitionUtil.BaseFragment fragment = (TransitionUtil.BaseFragment) getSupportFragmentManager().findFragmentByTag(AppContants.BASE_FRAGMENT);
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

    public static MainActivity of(Activity activity) {
        return (MainActivity) activity;
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

    public MaterialMenuView getToolbarButton() {
        return mToolbarButton;
    }

    public Snackbar getSnackBar() {
        return mSnackBar;
    }

    public SearchView getSearchView() {
        return mSearchView;
    }

    public View getFragmentBackground() {
        return mFragmentBackground;
    }

    //Setters
    public void setToolbarTitleText(String text) {
        mToolbarTitle.setText(text);
    }

    @Override
    public void init() {
        ButterKnife.bind(this);
        initFrontendComponents();
        initBackendComponents();
    }

    @Override
    public void initFrontendComponents() {
        initToolbar();
        mSnackBar = Snackbar.make(mFullScreen, getString(R.string.lost_internet_connection),
                Snackbar.LENGTH_INDEFINITE).setAction("", null);
    }

    @Override
    public void initBackendComponents() {
        mNetworkStateChangeReceiver = new NetworkStateChangeReceiver();
        mNetworkStateChangeReceiver.setInternetStateHasChange(this);
    }
}