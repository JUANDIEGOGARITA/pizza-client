package com.example.app.pizzaapp.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.example.app.pizzaapp.R;


/**
 * Created by joshujones on 9/2/16.
 */
public class CustonSwipeRefreshLayout extends SwipeRefreshLayout {
    public CustonSwipeRefreshLayout(Context context) {
        super(context);
        setup();
    }

    public CustonSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup(){
        setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
    }
}
