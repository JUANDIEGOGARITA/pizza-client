package com.example.app.pizzaapp.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.model.Pizza;

import butterknife.Bind;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class PizzaRecyclerAdapter extends BaseRecyclerAdapter<Pizza> {

    @Override
    public PizzaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pizza_item_list, parent, false);
        return new PizzaHolder(view);
    }

    public class PizzaHolder extends BaseRecyclerAdapter<Pizza>.ViewHolder {
        @Bind(R.id.title)
        AppCompatTextView mTitleTextView;
        @Bind(R.id.description)
        AppCompatTextView mDescriptionView;

        public PizzaHolder(View itemView) {
            super(itemView);
        }

        public void populate(Pizza item) {
            mTitleTextView.setText(item.getName());
            mDescriptionView.setText(item.getDescription());
        }
    }
}