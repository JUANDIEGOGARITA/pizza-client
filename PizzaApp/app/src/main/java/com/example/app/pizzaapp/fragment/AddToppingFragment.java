package com.example.app.pizzaapp.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.datamanager.DataManager;
import com.example.app.pizzaapp.datamanager.ServiceCallback;
import com.example.app.pizzaapp.helper.TransitionHelper;
import com.example.app.pizzaapp.model.PostTopping;
import com.example.app.pizzaapp.model.Topping;
import com.example.app.pizzaapp.util.Navigator;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/9/17.
 */

public class AddToppingFragment extends TransitionHelper.BaseFragment {

    @Bind(R.id.overlay)
    LinearLayout overlayLayout;
    @Bind(R.id.title)
    AppCompatTextView textView;
    @Bind(R.id.topping)
    EditText mTopping;


    boolean isPostToppingByPizza = false;
    public AddToppingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_topping, container, false);
        MainActivity.of(getActivity()).setToolbarTitleText("Add a New Topping");
        ButterKnife.bind(this, rootView);
        initBodyText();
        MainActivity.of(getActivity()).getFabButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabClick();
            }
        });
        return rootView;
    }

    private void initBodyText() {
        textView.setAlpha(0);
        textView.setTranslationY(100);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                textView.animate()
                        .alpha(1)
                        .setStartDelay(Navigator.ANIM_DURATION / 3)
                        .setDuration(Navigator.ANIM_DURATION * 5)
                        .setInterpolator(new DecelerateInterpolator(9))
                        .translationY(0)
                        .start();
            }
        }, 200);
    }

    public void onFabClick() {
        new DataManager(getActivity()).postTopping(new PostTopping(new Topping(mTopping.getText().toString())), new ServiceCallback() {
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

    @Override
    public void onBeforeEnter(View contentView) {
        overlayLayout.setVisibility(View.INVISIBLE);
        MainActivity.of(getActivity()).homeButton.setVisibility(View.VISIBLE);
        MainActivity.of(getActivity()).setHomeIcon(MaterialMenuDrawable.IconState.BURGER);
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
    }

    @Override
    public void onAfterEnter() {
        animateRevealShow(overlayLayout);
    }

    @Override
    public boolean onBeforeBack() {
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
        animateRevealHide(overlayLayout);

        return false;
    }

    @Override
    public void onBeforeViewShows(View contentView) {
        TransitionHelper.excludeEnterTarget(getActivity(), R.id.toolbar_container, true);
        TransitionHelper.excludeEnterTarget(getActivity(), R.id.full_screen, true);
        TransitionHelper.excludeEnterTarget(getActivity(), R.id.overlay, true);
    }

    public void animateRevealShow(View viewRoot) {
        View fab = MainActivity.of(getActivity()).fab;
        int cx = fab.getLeft() + (fab.getWidth() / 2); //middle of button
        int cy = fab.getTop() + (fab.getHeight() / 2); //middle of button
        int radius = (int) Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2)); //hypotenuse to top left

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, radius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(Navigator.ANIM_DURATION);
        anim.start();


    }

    public void animateRevealHide(final View viewRoot) {
        View fab = MainActivity.of(getActivity()).fab;
        int cx = fab.getLeft() + (fab.getWidth() / 2); //middle of button
        int cy = fab.getTop() + (fab.getHeight() / 2); //middle of button
        int radius = (int) Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2)); //hypotenuse to top left

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, radius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewRoot.setVisibility(View.INVISIBLE);
            }
        });
        //anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(Navigator.ANIM_DURATION);
        anim.start();

        Integer colorTo = getResources().getColor(R.color.colorPrimary);
        Integer colorFrom = getResources().getColor(android.R.color.white);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                overlayLayout.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
        colorAnimation.setInterpolator(new AccelerateInterpolator(2));
        colorAnimation.setDuration(Navigator.ANIM_DURATION);
        colorAnimation.start();
    }
}