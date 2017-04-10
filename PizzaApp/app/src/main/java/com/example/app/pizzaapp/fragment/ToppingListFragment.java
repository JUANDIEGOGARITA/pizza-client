
package com.example.app.pizzaapp.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
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

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.adapter.BaseRecyclerAdapter;
import com.example.app.pizzaapp.adapter.ToppingRecyclerAdapter;
import com.example.app.pizzaapp.datamanager.DataManager;
import com.example.app.pizzaapp.datamanager.ServiceCallback;
import com.example.app.pizzaapp.helper.TransitionHelper;
import com.example.app.pizzaapp.model.PostToppingByPizza;
import com.example.app.pizzaapp.model.Topping;
import com.example.app.pizzaapp.util.Navigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class ToppingListFragment extends TransitionHelper.BaseFragment implements CheckListener {
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.main_view)
    RelativeLayout main_view;
    ToppingRecyclerAdapter recyclerAdapter;
    int a, b, c;

    List<Integer> toppingIdList;

    boolean isCheckAvailable = false;
    int mPizzaId;

    public ToppingListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topping_list, container, false);
        ButterKnife.bind(this, rootView);
        toppingIdList = new ArrayList<>();
        a = getActivity().getIntent().getIntExtra("a", 0);
        b = getActivity().getIntent().getIntExtra("b", 0);
        c = getActivity().getIntent().getIntExtra("c", 0);
        //TODO GET FLAG EXTRA
        MainActivity activity = MainActivity.of(getActivity());
        String itemDescription = getActivity().getIntent().getStringExtra("item_text");
        mPizzaId = getActivity().getIntent().getIntExtra("pizzaId", -1);
        if (mPizzaId != -1) {
            isCheckAvailable = true;
        }

        ((MainActivity) getActivity()).toolbarTitle.setText(itemDescription);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadToppings();
            }
        });

        initRecyclerView();
        initBodyText();
        return rootView;
    }

    private void initBodyText() {
        recyclerView.setAlpha(0);
        recyclerView.setTranslationY(100);
        MainActivity.of(getActivity()).getFabButton().setAlpha(0.f);
        MainActivity.of(getActivity()).getFabButton().setTranslationY(100);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                recyclerView.animate()
                        .alpha(1)
                        .setStartDelay(Navigator.ANIM_DURATION / 3)
                        .setDuration(Navigator.ANIM_DURATION * 5)
                        .setInterpolator(new DecelerateInterpolator(9))
                        .translationY(0)
                        .start();
                MainActivity.of(getActivity()).getFabButton().animate()
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
        if(isCheckAvailable){
            recyclerAdapter = new ToppingRecyclerAdapter(this, isCheckAvailable);
        }else{
            recyclerAdapter = new ToppingRecyclerAdapter();
        }

        recyclerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<Topping>() {
            @Override
            public void onItemClick(View view, Topping item, boolean isLongClick) {
                if (isLongClick) {
                    MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.X);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);

        if (isCheckAvailable) {
            validateFabButtonState();
        }
        MainActivity.of(getActivity()).getFabButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckAvailable) {
                    postToppingsByPizza(mPizzaId);
                } else {
                    Navigator.launchAddToppingFragment(MainActivity.of(getActivity()), v, getActivity().findViewById(R.id.base_fragment_container));
                }
            }
        });
    }

    private void loadToppings() {
        recyclerAdapter.updateList(new ArrayList<Topping>());
        new DataManager(getActivity()).getToppings(new ServiceCallback() {
            @Override
            public void onSuccess(Object response) {
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
                ArrayList<Topping> toppingArrayList = (ArrayList<Topping>) response;

             /*   for (int i = 0; i < toppingArrayList.size(); i++) {
                    addyExtras.add(toppingArrayList.get(i).getName());
                }*/
                recyclerAdapter.updateList(toppingArrayList);
            }

            @Override
            public void onError(Object networkError) {

            }

            @Override
            public void onPreExecute() {

            }
        });
    }

    private void postToppingsByPizza(int pizzaId) {
        DataManager dataManager = new DataManager(getActivity());

        for (int i = 0; i < toppingIdList.size(); i++) {
            dataManager.postToppingByPizza(pizzaId, new PostToppingByPizza(toppingIdList.get(i)), new ServiceCallback() {
                @Override
                public void onSuccess(Object response) {
                    MainActivity.of(getActivity()).goBack();
                }

                @Override
                public void onError(Object networkError) {

                }

                @Override
                public void onPreExecute() {

                }
            });

        }
    }

    @Override
    public void onBeforeViewShows(View contentView) {
        ViewCompat.setTransitionName(((MainActivity) getActivity()).toolbarTitle, "title_element");
        TransitionHelper.excludeEnterTarget(getActivity(), R.id.toolbar_container, true);
        TransitionHelper.excludeEnterTarget(getActivity(), R.id.full_screen, true);

    }

    @Override
    public void onAfterEnter() {
        MainActivity.of(getActivity()).getFabButton().setVisibility(View.VISIBLE);
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
    public void onResume() {
        super.onResume();
        loadToppings();
    }

    @Override
    public boolean onBeforeBack() {
        animateRevealHide(main_view);
        animateRevealHide(MainActivity.of(getActivity()).getFabButton());
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
        MainActivity.of(getActivity()).getFabButton().setVisibility(View.INVISIBLE);
        MainActivity.of(getActivity()).fragmentBackround.animate().scaleX(.92f).scaleY(.92f).alpha(.6f).setDuration(Navigator.ANIM_DURATION).setInterpolator(new AccelerateInterpolator()).start();
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);

    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            loadToppings();
        } else {
            //TODO DISPLAY NO VIEW
        }
    }

    private void setFabButtonStyle() {
        if (toppingIdList.isEmpty()) {

        }
    }

    @Override
    public void onToppingChecked(int toppingId) {
        if (!toppingIdList.contains(toppingId)) {
            toppingIdList.add(toppingId);
        }
        validateFabButtonState();
    }

    @Override
    public void onToppingUnChecked(int toppingId) {
        if (toppingIdList.contains(toppingId)) {
            for(int i = 0; i<toppingIdList.size(); i++){
                if(toppingIdList.get(i) == toppingId);
                toppingIdList.remove(i);
            }
        }
        validateFabButtonState();
    }

    private void validateFabButtonState() {
        FloatingActionButton fab = MainActivity.of(getActivity()).getFabButton();
        if (toppingIdList.isEmpty()) {
            fab.setEnabled(false);
            mRefreshLayout.setEnabled(true);
        } else {
            fab.setEnabled(true);
            mRefreshLayout.setEnabled(false);

        }
    }
}