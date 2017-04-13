package com.example.app.pizzaapp.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.fragment.AddPizzaFragment;
import com.example.app.pizzaapp.fragment.AddToppingFragment;
import com.example.app.pizzaapp.fragment.HomeFragment;
import com.example.app.pizzaapp.fragment.PizzaDetailFragment;
import com.example.app.pizzaapp.fragment.PizzaListFragment;
import com.example.app.pizzaapp.fragment.ToppingListFragment;
import com.example.app.pizzaapp.model.Pizza;

import java.util.ArrayList;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class Navigator {

    public static int ANIM_DURATION = 350;

    public static void launchDetailProductFragment(MainActivity fromActivity, View fromView, Pizza item,
                                                   View backgroundView) {
        ViewCompat.setTransitionName(fromView, fromActivity.getString(R.string.detail_element));
        ViewCompat.setTransitionName(fromActivity.findViewById(R.id.fab), "fab");
        ViewCompat.setTransitionName(fromView.findViewById(R.id.title), "detail_title");
        ActivityOptionsCompat options =
                TransitionUtil.makeOptionsCompat(
                        fromActivity,
                        Pair.create(fromView, fromActivity.getString(R.string.detail_element)),
                        Pair.create(fromActivity.findViewById(R.id.fab), "fab"),
                        Pair.create(fromView.findViewById(R.id.title), "detail_title")
                );
        Intent intent = new Intent(fromActivity, MainActivity.class);
        intent.putExtra("item_text", item.getName());
        intent.putExtra("pizza_id", item.getId());
        intent.putExtra("item_description", item.getDescription());
        intent.putExtra(fromActivity.getString(R.string.fragment_resource_id), R.layout.fragment_pizza_detail);

        if (backgroundView != null)
            BitmapUtil.storeBitmapInIntent(BitmapUtil.createBitmap(backgroundView), intent);

        ActivityCompat.startActivity(fromActivity, intent, options.toBundle());

        fromActivity.overridePendingTransition(R.anim.slide_up, R.anim.scale_down);
    }

    public static void launchAddProductFragment(MainActivity fromActivity, View backgroundView,
                                                ArrayList<String> productNameList, int fragmentResourceId) {
        ActivityOptionsCompat options =
                TransitionUtil.makeOptionsCompat(
                        fromActivity
                );
        Intent intent = new Intent(fromActivity, MainActivity.class);
        intent.putExtra(fromActivity.getString(R.string.fragment_resource_id), fragmentResourceId);
        intent.putStringArrayListExtra(fromActivity.getString(R.string.product_name_list), productNameList);

        if (backgroundView != null)
            BitmapUtil.storeBitmapInIntent(BitmapUtil.createBitmap(backgroundView), intent);

        ActivityCompat.startActivity(fromActivity, intent, options.toBundle());

        fromActivity.overridePendingTransition(R.anim.slide_up, R.anim.scale_down);
    }

    public static void launchList(MainActivity fromActivity, String title, View fromView, int layout, View optionWrapper) {
        ViewCompat.setTransitionName(fromView, fromActivity.getString(R.string.title_element));
        ViewCompat.setTransitionName(optionWrapper, "option_wrapper");
        ActivityOptionsCompat options =
                TransitionUtil.makeOptionsCompat(
                        fromActivity,
                        Pair.create(fromView, fromActivity.getString(R.string.title_element)),
                        Pair.create(optionWrapper, "option_wrapper")
                );
        Intent intent = new Intent(fromActivity, MainActivity.class);
        intent.putExtra("item_text", title);
        intent.putExtra(fromActivity.getString(R.string.fragment_resource_id), layout);

        BitmapUtil.storeBitmapInIntent(BitmapUtil.createBitmap(fromActivity.findViewById(R.id.base_fragment_container)), intent);
        ActivityCompat.startActivity(fromActivity, intent, options.toBundle());
        fromActivity.overridePendingTransition(R.anim.slide_up, R.anim.scale_down);
    }

    public static void launchToppingList(MainActivity fromActivity, String title, View fromView, int layout, int pizzaId,
                                         ArrayList<Integer> toppingsIds) {
        ViewCompat.setTransitionName(fromView, fromActivity.getString(R.string.title_element));
        ActivityOptionsCompat options =
                TransitionUtil.makeOptionsCompat(
                        fromActivity,
                        Pair.create(fromView, fromActivity.getString(R.string.title_element))
                );
        Intent intent = new Intent(fromActivity, MainActivity.class);
        intent.putExtra("item_text", title);
        intent.putExtra(fromActivity.getString(R.string.fragment_resource_id), layout);
        intent.putExtra("pizzaId", pizzaId);
        intent.putIntegerArrayListExtra("toppingIds", toppingsIds);

        ActivityCompat.startActivity(fromActivity, intent, options.toBundle());
        fromActivity.overridePendingTransition(R.anim.slide_up, R.anim.scale_down);
    }

    public static TransitionUtil.BaseFragment getBaseFragment(Activity fromActivity) {
        int fragmentResourceId = fromActivity.getIntent().getIntExtra(fromActivity.getString(R.string.fragment_resource_id), 0);
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

    public static void setBaseFragment(FragmentActivity fromActivity, TransitionUtil.BaseFragment fragment) {
        if (fragment == null) return;
        FragmentTransaction transaction = fromActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base_fragment, fragment, AppContants.BASE_FRAGMENT);
        transaction.commit();
    }
}