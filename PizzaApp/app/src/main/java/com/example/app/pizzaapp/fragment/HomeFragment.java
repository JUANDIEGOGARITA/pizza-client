package com.example.app.pizzaapp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.util.Navigator;
import com.example.app.pizzaapp.util.TransitionUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by juandiegoGL on 4/9/17.
 */

public class HomeFragment extends TransitionUtil.BaseFragment {

    @Bind(R.id.tv_title)
    AppCompatTextView mTitle;

    @Bind(R.id.toppingLabel)
    AppCompatTextView mToppingLabel;

    @Bind(R.id.about)
    TextView about;

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
        ((MainActivity) getActivity()).getToolbarButton().setVisibility(View.GONE);
        MainActivity.of(getActivity()).getFabButton().setVisibility(View.GONE);
        return rootView;
    }

    @OnClick(R.id.pizza_option_wrapper)
    public void onLaunchPizzaList() {
        Navigator.launchList(MainActivity.of(getActivity()), mTitle.getText().toString(),
                mTitle, R.layout.fragment_pizza_list, mPizzaOptionWrapper);
    }

    @OnClick(R.id.topping_option_wrapper)
    public void onLaunchToppingsList() {
        Navigator.launchList(MainActivity.of(getActivity()), mToppingLabel.getText().toString(),
                mToppingLabel, R.layout.fragment_topping_list, mToppingOptionWrapper);
    }

    @OnClick(R.id.about)
    public void showProfileDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.profile_dialog);
        dialog.show();
    }
}