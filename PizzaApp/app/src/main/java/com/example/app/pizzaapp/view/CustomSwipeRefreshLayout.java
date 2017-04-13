package com.example.app.pizzaapp.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.example.app.pizzaapp.R;


/**
 * Created by juandiegoGL on 4/6/17.
 */

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
        setup();
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
    }
}
