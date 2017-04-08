package com.example.app.pizzaapp.util;


import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.helper.TransitionHelper;
import com.example.app.pizzaapp.model.Pizza;
import com.example.app.pizzaapp.model.ToppingByPizza;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class Navigator {

    public static int ANIM_DURATION = 350;

    public static void launchDetail(MainActivity fromActivity, View fromView, Pizza item, ArrayList<String> toppingByPizzaList,
                                    View backgroundView) {
        ViewCompat.setTransitionName(fromView, "detail_element");
        ViewCompat.setTransitionName(fromActivity.findViewById(R.id.fab), "fab");
        ActivityOptionsCompat options =
                TransitionHelper.makeOptionsCompat(
                        fromActivity,
                        Pair.create(fromView, "detail_element"),
                        Pair.create(fromActivity.findViewById(R.id.fab), "fab")
                );
        Intent intent = new Intent(fromActivity, MainActivity.class);
        intent.putExtra("item_text", item.getName());
        intent.putExtra("pizza_id", item.getId());
        intent.putExtra("item_description", item.getDescription());
        Log.d("JD", toppingByPizzaList.toString());
        intent.putStringArrayListExtra("list", toppingByPizzaList);
        intent.putExtra("fragment_resource_id", R.layout.fragment_pizza_detail);

        if (backgroundView != null) BitmapUtil.storeBitmapInIntent(BitmapUtil.createBitmap(backgroundView), intent);

        ActivityCompat.startActivity(fromActivity, intent, options.toBundle());

        fromActivity.overridePendingTransition(R.anim.slide_up, R.anim.scale_down);
    }

    public static void launchOverlay(MainActivity fromActivity, View fromView, View backgroundView) {
        ActivityOptionsCompat options =
                TransitionHelper.makeOptionsCompat(
                        fromActivity
                );
        Intent intent = new Intent(fromActivity, MainActivity.class);
        intent.putExtra("fragment_resource_id", R.layout.fragment_overaly);

        if (backgroundView != null) BitmapUtil.storeBitmapInIntent(BitmapUtil.createBitmap(backgroundView), intent);

        ActivityCompat.startActivity(fromActivity, intent, options.toBundle());

        fromActivity.overridePendingTransition(R.anim.slide_up, R.anim.scale_down);
    }

}
