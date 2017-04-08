package com.example.app.pizzaapp.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.helper.TransitionHelper;
import com.example.app.pizzaapp.util.Navigator;
import com.example.app.pizzaapp.view.OverScrollView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ThingDetailFragment extends TransitionHelper.BaseFragment {

    @Bind(R.id.detail_title)
    TextView titleTextView;
    @Bind(R.id.detail_body)
    TextView detailBodyTextView;
    @Bind(R.id.overscroll_view)
    OverScrollView scrollView;

    public static ThingDetailFragment create() {
        ThingDetailFragment f = new ThingDetailFragment();
        return f;
    }

    public ThingDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pizza_detail, container, false);
        ButterKnife.bind(this, rootView);
        String itemText = getActivity().getIntent().getStringExtra("item_text");
        String itemDescription = getActivity().getIntent().getStringExtra("item_description");
        titleTextView.setText(itemText);
        detailBodyTextView.setText(itemDescription);

        scrollView.setOverScrollListener(new OverScrollView.OverScrollListener() {
            int translationThreshold = 100;
            @Override
            public boolean onOverScroll(int yDistance, boolean isReleased) {
                if (Math.abs(yDistance) > translationThreshold) { //passed threshold
                    if (isReleased) {
                        getActivity().onBackPressed();
                        return true;
                    } else {
                        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.X);
                    }
                } else {
                    MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
                }
                return false;
            }
        });

        initDetailBody();
        return rootView;
    }

    private void initDetailBody() {
        detailBodyTextView.setAlpha(0);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                detailBodyTextView.animate().alpha(1).start();
            }
        }, 500);
    }

    @Override
    public void onBeforeViewShows(View contentView) {
        ViewCompat.setTransitionName(scrollView, "detail_element");
        ViewCompat.setTransitionName(getActivity().findViewById(R.id.fab), "fab");
        MainActivity.of(getActivity()).fab.setTranslationY(400);

        TransitionHelper.excludeEnterTarget(getActivity(), R.id.toolbar_container, true);
        TransitionHelper.excludeEnterTarget(getActivity(), R.id.full_screen, true);
    }

    @Override
    public void onBeforeEnter(View contentView) {
        MainActivity.of(getActivity()).fragmentBackround.animate().scaleX(.92f).scaleY(.92f).alpha(.6f).setDuration(Navigator.ANIM_DURATION).setInterpolator(new AccelerateInterpolator()).start();
        MainActivity.of(getActivity()).setHomeIcon(MaterialMenuDrawable.IconState.BURGER);
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
    }

    @Override
    public boolean onBeforeBack() {
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.BURGER);
        MainActivity.of(getActivity()).fragmentBackround.animate().scaleX(1).scaleY(1).alpha(1).translationY(0).setDuration(Navigator.ANIM_DURATION/4).setInterpolator(new DecelerateInterpolator()).start();
        TransitionHelper.fadeThenFinish(detailBodyTextView, getActivity());
        return false;
    }
}
