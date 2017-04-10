package com.example.app.pizzaapp.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.activity.MainActivity;
import com.example.app.pizzaapp.adapter.ToppingRecyclerAdapter;
import com.example.app.pizzaapp.datamanager.DataManager;
import com.example.app.pizzaapp.datamanager.ServiceCallback;
import com.example.app.pizzaapp.helper.TransitionHelper;
import com.example.app.pizzaapp.model.GetToppingByPizzaResult;
import com.example.app.pizzaapp.model.Topping;
import com.example.app.pizzaapp.util.Navigator;

import java.util.ArrayList;
import java.util.List;

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
    @Bind(R.id.recycler)
    RecyclerView recyclerView;

    @Bind(R.id.pizza_details)
    LinearLayout pizza_details;

    @Bind(R.id.detail_layout)
    CardView detail_layout;

    ToppingRecyclerAdapter recyclerAdapter;
    ArrayList<String> list;

    int pizzaId;

    public static PizzaDetailFragment create() {
        return new PizzaDetailFragment();
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
        list = getActivity().getIntent().getStringArrayListExtra("list");
        pizzaId = Integer.parseInt(getActivity().getIntent().getStringExtra("pizza_id"));
        ((MainActivity) getActivity()).toolbarTitle.setText(itemText);
        detailBodyTextView.setText(itemDescription);
        initRecyclerView();
        initDetailBody();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadToppings();
    }


    private void loadToppings() {
        new DataManager(getActivity()).getToppingsByPizzaId(pizzaId, new ServiceCallback() {
            @Override
            public void onSuccess(Object response) {
                ArrayList<GetToppingByPizzaResult> getToppingByPizzaResultList = (ArrayList<GetToppingByPizzaResult>) response;
                if (getToppingByPizzaResultList != null) {
                    List<Topping> toppingList = new ArrayList<Topping>();
                    for (int i = 0; i < getToppingByPizzaResultList.size(); i++) {
                        GetToppingByPizzaResult g = getToppingByPizzaResultList.get(i);
                        toppingList.add(i, new Topping(g.getId(), g.getName()));
                    }
                    recyclerAdapter.updateList(toppingList);
                }else{
                    Log.e("JD", "CHECK THIS ERROR");
                }
            }

            @Override
            public void onError(Object networkError) {

            }

            @Override
            public void onPreExecute() {

            }
        });
    }

    private void initRecyclerView() {
        recyclerAdapter = new ToppingRecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);

        MainActivity.of(getActivity()).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cx = v.getLeft() + (v.getWidth() / 2); //middle of button
                int cy = v.getTop() + (v.getHeight() / 2); //middle of button
                int radius = (int) Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2)); //hypotenuse to top left
                Navigator.launchList(MainActivity.of(getActivity()), "Add toppings to your Pizza",
                        v, cx, cy, radius, R.layout.fragment_topping_list, pizzaId);
            }
        });
    }


    private void initDetailBody() {
        detailBodyTextView.setAlpha(0);
        recyclerView.setAlpha(0);
        titleTextView.setAlpha(0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                detailBodyTextView.animate().alpha(1).start();
                recyclerView.animate().alpha(1).start();
                titleTextView.animate().alpha(1).start();
            }
        }, 500);
    }

    @Override
    public void onBeforeViewShows(View contentView) {
        ViewCompat.setTransitionName(detail_layout, "detail_element");
        ViewCompat.setTransitionName(MainActivity.of(getActivity()).getFabButton(), "fab");
        // ViewCompat.setTransitionName(getActivity().findViewById(R.id.fab), "fab");
        ViewCompat.setTransitionName(((MainActivity) getActivity()).toolbarTitle, "detail_title");
        // MainActivity.of(getActivity()).fab.setTranslationY(400);

        TransitionHelper.excludeEnterTarget(getActivity(), R.id.toolbar_container, true);
        TransitionHelper.excludeEnterTarget(getActivity(), R.id.full_screen, true);

    }

    @Override
    public void onBeforeEnter(View contentView) {
        MainActivity.of(getActivity()).fragmentBackround.animate().scaleX(.92f).scaleY(.92f).alpha(.6f).setDuration(Navigator.ANIM_DURATION).setInterpolator(new AccelerateInterpolator()).start();
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
    }


    @Override
    public boolean onBeforeBack() {
        MainActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
        MainActivity.of(getActivity()).fragmentBackround.animate().scaleX(1).scaleY(1).alpha(1).translationY(0).setDuration(Navigator.ANIM_DURATION / 4).setInterpolator(new DecelerateInterpolator()).start();
        TransitionHelper.fadeThenFinish(detailBodyTextView, getActivity());
        TransitionHelper.fadeThenFinish(titleTextView, getActivity());

        return false;
    }


}
