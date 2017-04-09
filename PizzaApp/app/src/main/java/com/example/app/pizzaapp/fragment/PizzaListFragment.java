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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.adapter.BaseRecyclerAdapter;
import com.example.app.pizzaapp.adapter.PizzaRecyclerAdapter;
import com.example.app.pizzaapp.datamanager.DataManager;
import com.example.app.pizzaapp.datamanager.ServiceCallback;
import com.example.app.pizzaapp.helper.TransitionHelper;
import com.example.app.pizzaapp.model.Pizza;
import com.example.app.pizzaapp.model.ToppingByPizza;
import com.example.app.pizzaapp.util.Navigator;

import java.util.ArrayList;
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
    PizzaRecyclerAdapter recyclerAdapter;
    List<Pizza> pizzas;

    int a, b, c;
    public PizzaListFragment() {
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
        getThings();


        ((MainActivity)getActivity()).toolbarTitle.setText(itemDescription);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               getThings();
            }
        });
        initBodyText();
        return rootView;
    }

    private void initBodyText() {
        recyclerView.setAlpha(0);
        recyclerView.setTranslationY(100);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                recyclerView.animate()
                        .alpha(1)
                        .setStartDelay(Navigator.ANIM_DURATION/3)
                        .setDuration(Navigator.ANIM_DURATION*5)
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
                    loadToppings(view, item);

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

    private void loadToppings(final View view, final Pizza item) {
        new DataManager(getActivity()).getToppingsByPizzaId(item.getId(), new ServiceCallback() {
            @Override
            public void onSuccess(Object response) {
                ArrayList<ToppingByPizza> toppingByPizzaList = (ArrayList<ToppingByPizza>) response;
                ArrayList<String> addyExtras = new ArrayList<String>();

                for (int i = 0; i < toppingByPizzaList.size(); i++) {
                    addyExtras.add(toppingByPizzaList.get(i).getName());
                }
                Navigator.launchDetail(MainActivity.of(getActivity()), view, item, addyExtras, recyclerView);
            }

            @Override
            public void onError(Object networkError) {

            }

            @Override
            public void onPreExecute() {

            }
        });
    }

    @Override
    public void onBeforeViewShows(View contentView) {
        ViewCompat.setTransitionName(((MainActivity)getActivity()).toolbarTitle, "title_element");
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
                main_view.setBackgroundColor((Integer)animator.getAnimatedValue());
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

    public List<Pizza> getThings() {
        new DataManager(getActivity()).getPizzas(new ServiceCallback() {
            @Override
            public void onSuccess(Object response) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                pizzas = (List<Pizza>) response;
                recyclerAdapter.updateList(pizzas);

            }

            @Override
            public void onError(Object networkError) {
                pizzas = null;
            }

            @Override
            public void onPreExecute() {

            }
        });
        return pizzas;
    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            getThings();
        } else {
            //TODO DISPLAY NO VIEW
        }
    }
}

