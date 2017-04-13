package com.example.app.pizzaapp.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.datamanager.DataManager;
import com.example.app.pizzaapp.datamanager.ServiceCallback;
import com.example.app.pizzaapp.model.Pizza;
import com.example.app.pizzaapp.model.PostPizza;
import com.example.app.pizzaapp.util.AppContants;
import com.example.app.pizzaapp.util.DialogUtil;
import com.example.app.pizzaapp.util.DialogUtilListener;
import com.example.app.pizzaapp.util.EditTextValidator;
import com.example.app.pizzaapp.util.Initializer;
import com.example.app.pizzaapp.util.Navigator;
import com.example.app.pizzaapp.util.TransitionUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class AddPizzaFragment extends TransitionUtil.BaseFragment implements Initializer {

    @Bind(R.id.main_view)
    LinearLayout mMainView;

    @Bind(R.id.title)
    AppCompatTextView mTitle;

    @Bind(R.id.name_edit_text)
    EditText mNameEditText;

    @Bind(R.id.description_edit_text)
    EditText mDescriptionEditText;

    @Bind(R.id.name_edit_text_wrapper)
    TextInputLayout mNameEditTextWrapper;

    List<String> mPizzaNameList;

    DialogUtilListener mDialogListener = new DialogUtilListener() {
        @Override
        public void onPositiveButtonClicked() {
            postPizza();
        }

        @Override
        public void onNegativeButtonClicked(DialogInterface d) {
            d.dismiss();
        }
    };

    public AddPizzaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_pizza, container, false);
        ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    private void initBodyText() {
        mTitle.setAlpha(0);
        mTitle.setTranslationY(100);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mTitle.animate()
                        .alpha(1)
                        .setStartDelay(Navigator.ANIM_DURATION / 3)
                        .setDuration(Navigator.ANIM_DURATION * 5)
                        .setInterpolator(new DecelerateInterpolator(9))
                        .translationY(0)
                        .start();
            }
        }, 200);
    }

    private boolean validateEditTexts() {
        if (EditTextValidator.validateEditText(mPizzaNameList, mNameEditTextWrapper, mNameEditText)) {
            return true;
        } else {
            return false;
        }
    }

    public void postPizza() {
        if (isInternetAvailable() && validateEditTexts()) {
            new DataManager(getActivity()).postPizza(new PostPizza(new Pizza(mNameEditText.getText().toString(),
                    mDescriptionEditText.getText().toString())), new ServiceCallback() {
                @Override
                public void onSuccess(Object status, Object response) {
                    if (Integer.parseInt(status.toString()) == AppContants.OK_HTTP_RESPONSE) {
                        MainActivity.of(getActivity()).goBack();
                    } else {
                        showErrorDialog(status.toString() + " " + getString(R.string.internal_server_error));
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

    private void showErrorDialog(String errorMessage) {
        DialogUtil.showErrorDialog(mDialogListener, getActivity(), errorMessage);
    }

    @Override
    public void onBeforeEnter(View contentView) {
        mMainView.setVisibility(View.INVISIBLE);
        MainActivity.of(getActivity()).getFabButton().setImageResource(R.mipmap.ic_check);
        MainActivity.of(getActivity()).getToolbarButton().setVisibility(View.VISIBLE);
        MainActivity.of(getActivity()).setHomeIcon(MaterialMenuDrawable.IconState.BURGER);
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
    }

    @Override
    public void onAfterEnter() {
        TransitionUtil.animateRevealShow(getActivity(), mMainView);
    }

    @Override
    public boolean onBeforeBack() {
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
        TransitionUtil.animateRevealHide(getActivity(), mMainView);
        return false;
    }

    @Override
    public void onBeforeViewShows(View contentView) {
        ViewCompat.setTransitionName(getActivity().findViewById(R.id.fab), "fab");
        TransitionUtil.excludeEnterTarget(getActivity(), R.id.toolbar_container, true);
        TransitionUtil.excludeEnterTarget(getActivity(), R.id.full_screen, true);
        TransitionUtil.excludeEnterTarget(getActivity(), R.id.main_view, true);
    }

    @Override
    public void init() {
        initBackendComponents();
        initFrontendComponents();
    }

    @Override
    public void initFrontendComponents() {

        initBodyText();
        MainActivity.of(getActivity()).getFabButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postPizza();
            }
        });

        mNameEditText.addTextChangedListener(new TextWatcher() {
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
    }

    @Override
    public void initBackendComponents() {
        mPizzaNameList = getActivity().getIntent().getStringArrayListExtra(getString(R.string.product_name_list));
    }
}