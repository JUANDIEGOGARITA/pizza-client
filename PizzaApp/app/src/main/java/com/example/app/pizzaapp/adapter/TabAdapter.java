package com.example.app.pizzaapp.adapter;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.app.pizzaapp.fragment.PizzaListFragment;
import com.example.app.pizzaapp.fragment.ToppingListFragment;
import com.example.app.pizzaapp.helper.TransitionHelper;

/**
 * Created by juandiegoGL on 4/8/17.
 */

public class TabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public TransitionHelper.BaseFragment getItem(int position) {

        switch (position) {
            case 0:
                TransitionHelper.BaseFragment tab1 = new PizzaListFragment();
                return tab1;
            case 1:
                TransitionHelper.BaseFragment tab2 = new ToppingListFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
