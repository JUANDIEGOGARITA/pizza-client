package com.example.app.pizzaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.model.Pizza;

import butterknife.Bind;

public class PizzaRecyclerAdapter extends BaseRecyclerAdapter<Pizza> {

    @Override
    public PizzaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pizza_item_list, parent, false);
        return new PizzaHolder(view);
    }

    public class PizzaHolder extends BaseRecyclerAdapter<Pizza>.ViewHolder {
        @Bind(R.id.title)
        TextView titleTextView;
        @Bind(R.id.description)
        TextView descriptionView;

        public PizzaHolder(View itemView) {
            super(itemView);
        }

        public void populate(Pizza item) {
            titleTextView.setText(item.getName());
            descriptionView.setText(item.getDescription());
        }
    }
}
