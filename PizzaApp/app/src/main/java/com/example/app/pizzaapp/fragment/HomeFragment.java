package com.example.app.pizzaapp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.util.Initializer;
import com.example.app.pizzaapp.util.Navigator;
import com.example.app.pizzaapp.util.TransitionUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by juandiegoGL on 4/9/17.
 */

public class HomeFragment extends TransitionUtil.BaseFragment implements Initializer {

    @Bind(R.id.pizza_option)
    AppCompatTextView mPizzaOption;

    @Bind(R.id.topping_option)
    AppCompatTextView mToppingOption;

    @Bind(R.id.pizza_option_wrapper)
    LinearLayout mPizzaOptionWrapper;

    @Bind(R.id.topping_option_wrapper)
    LinearLayout mToppingOptionWrapper;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    @OnClick(R.id.pizza_option_wrapper)
    public void onLaunchPizzaList() {
        Navigator.launchList(MainActivity.of(getActivity()), mPizzaOption.getText().toString(),
                mPizzaOption, R.layout.fragment_pizza_list, mPizzaOptionWrapper);
    }

    @OnClick(R.id.topping_option_wrapper)
    public void onLaunchToppingsList() {
        Navigator.launchList(MainActivity.of(getActivity()), mToppingOption.getText().toString(),
                mToppingOption, R.layout.fragment_topping_list, mToppingOptionWrapper);
    }

    @OnClick(R.id.about_option)
    public void showProfileDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.profile_dialog);
        dialog.show();
    }

    @Override
    public void init() {
        initFrontendComponents();
        initBackendComponents();
    }

    @Override
    public void initFrontendComponents() {
        ((MainActivity) getActivity()).getToolbarButton().setVisibility(View.GONE);
        MainActivity.of(getActivity()).getFabButton().setVisibility(View.GONE);
    }

    @Override
    public void initBackendComponents() {

    }
}