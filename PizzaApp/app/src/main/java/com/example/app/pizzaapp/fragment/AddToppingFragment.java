package com.example.app.pizzaapp.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.app.pizzaapp.model.PostTopping;
import com.example.app.pizzaapp.model.Topping;
import com.example.app.pizzaapp.util.AppContants;
import com.example.app.pizzaapp.util.DialogUtil;
import com.example.app.pizzaapp.util.DialogUtilListener;
import com.example.app.pizzaapp.util.EditTextValidator;
import com.example.app.pizzaapp.util.Navigator;
import com.example.app.pizzaapp.util.TransitionUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/9/17.
 */

public class AddToppingFragment extends TransitionUtil.BaseFragment {

    @Bind(R.id.overlay)
    LinearLayout overlayLayout;

    @Bind(R.id.title)
    AppCompatTextView textView;

    @Bind(R.id.topping)
    EditText mTopping;

    @Bind(R.id.time_wrapper)
    TextInputLayout mTimeWrapper;

    List<String> toppingNameList;

    DialogUtilListener mDialogListener = new DialogUtilListener() {
        @Override
        public void onPositiveButtonClicked() {
            postTopping();
        }

        @Override
        public void onNegativeButtonClicked(DialogInterface d) {
            d.dismiss();
        }
    };

    public AddToppingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_topping, container, false);
        MainActivity.of(getActivity()).setToolbarTitleText("Add a New Topping");

        toppingNameList = getActivity().getIntent().getStringArrayListExtra("topping_name_list");

        ButterKnife.bind(this, rootView);
        initBodyText();
        MainActivity.of(getActivity()).getFabButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postTopping();
            }
        });
        mTopping.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateEditTexts();
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

    public void postTopping() {
        if (isInternetAvailable() && validateEditTexts()) {
            new DataManager(getActivity()).postTopping(new PostTopping(new Topping(mTopping.getText().toString())), new ServiceCallback() {
                @Override
                public void onSuccess(Object status, Object response) {
                    if (Integer.parseInt(status.toString()) == AppContants.OK_HTTP_RESPONSE) {
                        MainActivity.of(getActivity()).goBack();
                    } else {
                        showErrorDialog(status.toString() + " Internal Server Error");
                    }
                }

                @Override
                public void onError(Object networkError) {
                    showErrorDialog(networkError.toString());
                }

                @Override
                public void onPreExecute() {

                }
            });
        }
    }

    private boolean validateEditTexts() {
        if (EditTextValidator.validateEditText(toppingNameList, mTimeWrapper, mTopping)) {
            return true;
        } else {
            return false;
        }
    }

    private void showErrorDialog(String errorMessage) {
        DialogUtil.showErrorDialog(mDialogListener, getActivity(), errorMessage);
    }

    @Override
    public void onBeforeEnter(View contentView) {
        overlayLayout.setVisibility(View.INVISIBLE);
        MainActivity.of(getActivity()).getFabButton().setImageResource(R.mipmap.ic_check);
        MainActivity.of(getActivity()).getHomeButton().setVisibility(View.VISIBLE);
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
        TransitionUtil.excludeEnterTarget(getActivity(), R.id.toolbar_container, true);
        TransitionUtil.excludeEnterTarget(getActivity(), R.id.full_screen, true);
        TransitionUtil.excludeEnterTarget(getActivity(), R.id.overlay, true);
    }

    public void animateRevealShow(View viewRoot) {
        View fab = MainActivity.of(getActivity()).getFabButton();
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
        View fab = MainActivity.of(getActivity()).getFabButton();
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

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            MainActivity.of(getActivity()).getFabButton().setEnabled(true);
            // TODO ANIMATE THIS PART, REPLACE ANIMATION
            //  animateRevealShow(main_view);
        } else {
            //   showEmptyOrErrorView("No internet connection available");
            MainActivity.of(getActivity()).getFabButton().setEnabled(false);
            if (!MainActivity.of(getActivity()).getSnackbar().isShown()) {
                MainActivity.of(getActivity()).getSnackbar().show();
            }
        }
    }

}