package com.example.app.pizzaapp.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.helper.TransitionHelper;
import com.example.app.pizzaapp.util.Navigator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by juandiegoGL on 4/9/17.
 */

public class HomeFragment extends TransitionHelper.BaseFragment {

    @Bind(R.id.tv_title)
    AppCompatTextView mTitle;

    @Bind(R.id.toppingLabel)
    AppCompatTextView mToppingLabel;
    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        ((MainActivity)getActivity()).homeButton.setVisibility(View.GONE);
        MainActivity.of(getActivity()).fab.setVisibility(View.GONE);
        return rootView;
    }

    @OnClick(R.id.tv_title)
    public void onLaunchPizzaList(){
        int cx = mTitle.getLeft() + (mTitle.getWidth()/2); //middle of button
        int cy = mTitle.getTop() + (mTitle.getHeight()/2); //middle of button
        int radius = (int) Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2)); //hypotenuse to top left
        Navigator.launchList(MainActivity.of(getActivity()), mTitle.getText().toString(),
                mTitle, cx, cy, radius, R.layout.fragment_pizza_list, -1);
    }

    @OnClick(R.id.toppingLabel)
    public void onLaunchToppingsList(){
        int cx = mToppingLabel.getLeft() + (mToppingLabel.getWidth()/2); //middle of button
        int cy = mToppingLabel.getTop() + (mToppingLabel.getHeight()/2); //middle of button
        int radius = (int) Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2)); //hypotenuse to top left
        Navigator.launchList(MainActivity.of(getActivity()), mToppingLabel.getText().toString(),
                mToppingLabel, cx, cy, radius, R.layout.fragment_topping_list, -1);
    }


}