package com.example.app.pizzaapp.helper;

import android.animation.Animator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.transition.Transition;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.example.app.pizzaapp.receiver.NetworkStateChangeReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class TransitionHelper {

    private Activity mActivity;
    private List<Listener> mListeners = new ArrayList<>();
    private boolean isAfterEnter = false;
    private boolean isViewCreatedAlreadyCalled = false;
    private boolean isPostponeEnterTransition = false;

    private TransitionHelper(Activity activity, Bundle savedInstanceState) {
        this.mActivity = activity;
        isAfterEnter = savedInstanceState != null;
        postponeEnterTransition();
    }

    public void onResume() {
        if (isAfterEnter) return;

        if (!isViewCreatedAlreadyCalled) onViewCreated();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            onAfterEnter();
        } else {
            mActivity.getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    if (isAfterEnter())
                        for (Listener listener : mListeners) listener.onBeforeReturn();
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    if (!isAfterEnter()) onAfterEnter();
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    if (!isAfterEnter()) onAfterEnter();
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            });
        }
    }

    public void onBackPressed() {
        boolean isConsumed = false;
        for (Listener listener : mListeners) {
            isConsumed = listener.onBeforeBack() || isConsumed;
        }
        if (!isConsumed) ActivityCompat.finishAfterTransition(mActivity);
    }


    public void onViewCreated() {
        if (isViewCreatedAlreadyCalled) return;
        isViewCreatedAlreadyCalled = true;

        View contentView = mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        for (Listener listener : mListeners) listener.onBeforeViewShows(contentView);
        if (!isAfterEnter()) {
            for (Listener listener : mListeners) listener.onBeforeEnter(contentView);
        }

        if (isPostponeEnterTransition) startPostponedEnterTransition();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isAfterEnter", isAfterEnter);
    }


    public interface Source {
        TransitionHelper getTransitionHelper();

        void setTransitionHelper(TransitionHelper transitionHelper);
    }

    public interface Listener {
        void onBeforeViewShows(View contentView);

        void onBeforeEnter(View contentView);

        void onAfterEnter();

        boolean onBeforeBack();

        void onBeforeReturn();
    }


    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    private void onAfterEnter() {
        for (Listener listener : mListeners) listener.onAfterEnter();
        isAfterEnter = true;
    }

    public boolean isAfterEnter() {
        return isAfterEnter;
    }

    private void postponeEnterTransition() {
        if (isAfterEnter) return;
        ActivityCompat.postponeEnterTransition(mActivity);
        isPostponeEnterTransition = true;
    }


    private void startPostponedEnterTransition() {
        final View decor = mActivity.getWindow().getDecorView();
        decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                decor.getViewTreeObserver().removeOnPreDrawListener(this);
                ActivityCompat.startPostponedEnterTransition(mActivity);
                return true;
            }
        });
    }

    public static void excludeEnterTarget(Activity activity, int targetId, boolean exclude) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getEnterTransition().excludeTarget(targetId, exclude);
        }
    }

    public static TransitionHelper of(Activity a) {
        return ((Source) a).getTransitionHelper();
    }

    public static void init(Source source, Bundle savedInstanceState) {
        source.setTransitionHelper(new TransitionHelper((Activity) source, savedInstanceState));
    }

    public static ActivityOptionsCompat makeOptionsCompat(Activity fromActivity, Pair<View, String>... sharedElements) {
        ArrayList<Pair<View, String>> list = new ArrayList<>(Arrays.asList(sharedElements));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            list.add(Pair.create(fromActivity.findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
            list.add(Pair.create(fromActivity.findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
        }

        //remove any views that are null
        for (ListIterator<Pair<View, String>> iter = list.listIterator(); iter.hasNext(); ) {
            Pair pair = iter.next();
            if (pair.first == null) iter.remove();
        }

        sharedElements = list.toArray(new Pair[list.size()]);
        return ActivityOptionsCompat.makeSceneTransitionAnimation(
                fromActivity,
                sharedElements
        );
    }

    public static void fadeThenFinish(View v, final Activity a) {
        if (v != null) {
            v.animate()
                    .alpha(0)
                    .setDuration(100)
                    .setListener(
                            new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ActivityCompat.finishAfterTransition(a);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }
                    )
                    .start();
        }
    }

    public static class BaseActivity extends ActionBarActivity implements TransitionHelper.Source, TransitionHelper.Listener {
        TransitionHelper transitionHelper;

        @Override
        public TransitionHelper getTransitionHelper() {
            return transitionHelper;
        }

        @Override
        public void setTransitionHelper(TransitionHelper transitionHelper) {
            this.transitionHelper = transitionHelper;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            TransitionHelper.init(this, savedInstanceState);
            TransitionHelper.of(this).addListener(this);
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            TransitionHelper.of(this).onSaveInstanceState(outState);
            super.onSaveInstanceState(outState);
        }

        @Override
        protected void onResume() {
            TransitionHelper.of(this).onResume();
            super.onResume();
        }

        @Override
        public void onBackPressed() {
            TransitionHelper.of(this).onBackPressed();
        }


        @Override
        public void onBeforeViewShows(View contentView) {

        }

        @Override
        public void onBeforeEnter(View contentView) {

        }

        @Override
        public void onAfterEnter() {

        }

        @Override
        public boolean onBeforeBack() {
            return false;
        }

        @Override
        public void onBeforeReturn() {

        }
    }

    public static class BaseFragment extends Fragment implements TransitionHelper.Listener, NetworkStateChangeReceiver.InternetStateHasChange {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            TransitionHelper.of(getActivity()).addListener(this);
            super.onCreate(savedInstanceState);

        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            TransitionHelper.of(getActivity()).onViewCreated();
            super.onViewCreated(view, savedInstanceState);
        }

        @Override
        public void onBeforeViewShows(View contentView) {

        }

        @Override
        public void onBeforeEnter(View contentView) {

        }

        @Override
        public void onAfterEnter() {

        }

        @Override
        public boolean onBeforeBack() {
            return false;
        }

        @Override
        public void onBeforeReturn() {

        }

        @Override
        public void networkChangedState(boolean isInternetAvailable) {

        }
    }
}