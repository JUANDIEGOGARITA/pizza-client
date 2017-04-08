package com.example.app.pizzaapp.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.adapter.ToppingRecyclerAdapter;
import com.example.app.pizzaapp.helper.TransitionHelper;
import com.example.app.pizzaapp.util.Navigator;
import com.example.app.pizzaapp.view.OverScrollView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class PizzaDetailFragment extends TransitionHelper.BaseFragment {

    @Bind(R.id.detail_title)
    TextView titleTextView;
    @Bind(R.id.detail_body)
    TextView detailBodyTextView;
    @Bind(R.id.overscroll_view)
    OverScrollView scrollView;
    @Bind(R.id.recycler)
    RecyclerView recyclerView;

    ToppingRecyclerAdapter recyclerAdapter;

    public static PizzaDetailFragment create() {
        PizzaDetailFragment f = new PizzaDetailFragment();
        return f;
    }

    public PizzaDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pizza_detail, container, false);
        ButterKnife.bind(this, rootView);
        MainActivity activity = MainActivity.of(getActivity());
        activity.homeButton.setVisibility(View.VISIBLE);
        String itemText = getActivity().getIntent().getStringExtra("item_text");
        String itemDescription = getActivity().getIntent().getStringExtra("item_description");
        ArrayList<String> list = getActivity().getIntent().getStringArrayListExtra("list");
        int itemId = getActivity().getIntent().getIntExtra("pizza_id", 0);
        titleTextView.setText(itemText);
        detailBodyTextView.setText(itemDescription);
        initRecyclerView();
        recyclerAdapter.updateList(list);
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


    private void initRecyclerView() {
        recyclerAdapter = new ToppingRecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);

        MainActivity.of(getActivity()).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.launchOverlay(MainActivity.of(getActivity()), v, getActivity().findViewById(R.id.base_fragment_container));
            }
        });
    }


    private void initDetailBody() {
        detailBodyTextView.setAlpha(0);
        recyclerView.setAlpha(0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                detailBodyTextView.animate().alpha(1).start();
                recyclerView.animate().alpha(1).start();
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
        MainActivity.of(getActivity()).fragmentBackround.animate().scaleX(.96f).scaleY(.96f).alpha(.3f).setDuration(Navigator.ANIM_DURATION).setInterpolator(new AccelerateInterpolator()).start();
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
    }

    @Override
    public boolean onBeforeBack() {
        //   MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.X);
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);
        MainActivity.of(getActivity()).homeButton.startAnimation(shake);
        MainActivity.of(getActivity()).toolbarTitle.startAnimation(shake);
        MainActivity.of(getActivity()).fragmentBackround.animate().scaleX(1).scaleY(1).alpha(1).translationY(0).setDuration(Navigator.ANIM_DURATION).setInterpolator(new DecelerateInterpolator()).start();
        TransitionHelper.fadeThenFinish(detailBodyTextView, getActivity());
        return false;
    }
}
