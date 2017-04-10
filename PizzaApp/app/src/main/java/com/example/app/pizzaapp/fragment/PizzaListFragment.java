package com.example.app.pizzaapp.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.adapter.BaseRecyclerAdapter;
import com.example.app.pizzaapp.adapter.PizzaRecyclerAdapter;
import com.example.app.pizzaapp.datamanager.DataManager;
import com.example.app.pizzaapp.datamanager.ServiceCallback;
import com.example.app.pizzaapp.helper.TransitionHelper;
import com.example.app.pizzaapp.model.Pizza;
import com.example.app.pizzaapp.util.Navigator;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class PizzaListFragment extends TransitionHelper.BaseFragment {
    @Bind(R.id.recycler)
    RecyclerView recyclerView;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @Bind(R.id.main_view)
    RelativeLayout main_view;

    @Bind(R.id.empty_view)
    RelativeLayout empty_view;

    @Bind(R.id.error_message)
    TextView error_message;

    PizzaRecyclerAdapter recyclerAdapter;

    int a, b, c;

    public PizzaListFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pizza_list, container, false);
        ButterKnife.bind(this, rootView);
        String itemDescription = getActivity().getIntent().getStringExtra("item_text");
        a = getActivity().getIntent().getIntExtra("a", 0);
        b = getActivity().getIntent().getIntExtra("b", 0);
        c = getActivity().getIntent().getIntExtra("c", 0);
        initRecyclerView();

        ((MainActivity) getActivity()).toolbarTitle.setText(itemDescription);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPizzas();
            }
        });
        initBodyText();
        return rootView;
    }

    private void initBodyText() {
        recyclerView.setAlpha(0);
        recyclerView.setTranslationY(100);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                recyclerView.animate()
                        .alpha(1)
                        .setStartDelay(Navigator.ANIM_DURATION / 3)
                        .setDuration(Navigator.ANIM_DURATION * 5)
                        .setInterpolator(new DecelerateInterpolator(9))
                        .translationY(0)
                        .start();
            }
        }, 350);
    }

    private void initRecyclerView() {
        recyclerAdapter = new PizzaRecyclerAdapter();
        recyclerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<Pizza>() {
            @Override
            public void onItemClick(View view, Pizza item, boolean isLongClick) {
                if (isLongClick) {
                    MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.X);
                } else {
                    Navigator.launchDetail(MainActivity.of(getActivity()), view, item, recyclerView);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);

        MainActivity.of(getActivity()).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.launchOverlay(MainActivity.of(getActivity()), v, getActivity().findViewById(R.id.base_fragment_container));
            }
        });
    }

    @Override
    public void onBeforeViewShows(View contentView) {
        ViewCompat.setTransitionName(((MainActivity) getActivity()).toolbarTitle, "title_element");
        TransitionHelper.excludeEnterTarget(getActivity(), R.id.toolbar_container, true);
        TransitionHelper.excludeEnterTarget(getActivity(), R.id.full_screen, true);

    }

    @Override
    public void onAfterEnter() {
        animateRevealShow(main_view);
    }

    public void animateRevealShow(View viewRoot) {

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, a, b, 0, c);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(Navigator.ANIM_DURATION);
        anim.start();
    }

    @Override
    public boolean onBeforeBack() {
        animateRevealHide(main_view);
        return false;
    }

    public void animateRevealHide(final View viewRoot) {
        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, a, b, c, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewRoot.setVisibility(View.INVISIBLE);
            }
        });
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(Navigator.ANIM_DURATION);
        anim.start();

        Integer colorTo = getResources().getColor(android.R.color.white);
        Integer colorFrom = getResources().getColor(android.R.color.white);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                main_view.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
        colorAnimation.setInterpolator(new AccelerateInterpolator(2));
        colorAnimation.setDuration(Navigator.ANIM_DURATION);
        colorAnimation.start();
    }


    @Override
    public void onBeforeEnter(View contentView) {
        main_view.setVisibility(View.INVISIBLE);
        MainActivity.of(getActivity()).fragmentBackround.animate().scaleX(.92f).scaleY(.92f).alpha(.6f).setDuration(Navigator.ANIM_DURATION).setInterpolator(new AccelerateInterpolator()).start();
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
    }

    public void loadPizzas() {
        if (isInternetAvailable()) {
            mRefreshLayout.setRefreshing(true);
            List<Pizza> pizzas = null;
            new DataManager(getActivity()).getPizzas(new ServiceCallback() {
                @Override
                public void onSuccess(Object response) {
                    if (mRefreshLayout.isRefreshing()) {
                        mRefreshLayout.setRefreshing(false);
                    }
                    if (empty_view.getVisibility() == View.VISIBLE) {
                        empty_view.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    recyclerAdapter.updateList((List<Pizza>) response);
                }

                @Override
                public void onError(Object networkError) {
                    showEmptyView(networkError.toString());
                }

                @Override
                public void onPreExecute() {

                }
            });
        } else {

        }
    }

    private void showEmptyView(String message) {
        recyclerView.setVisibility(View.GONE);
        empty_view.setVisibility(View.VISIBLE);
        error_message.setText(message);
    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            loadPizzas();
          // TODO ANIMATE THIS PART, REPLACE ANIMATION
            //  animateRevealShow(main_view);
        } else {
            showEmptyView("No internet connection available");
            MainActivity.of(getActivity()).getSnackbar().setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadPizzas();
                }
            });
        }
    }
}