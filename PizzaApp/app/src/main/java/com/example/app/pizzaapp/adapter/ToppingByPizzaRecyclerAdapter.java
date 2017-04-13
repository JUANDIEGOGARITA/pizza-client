package com.example.app.pizzaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app.pizzaapp.R;

import butterknife.Bind;

/**
 * Created by juandiegoGL on 4/8/17.
 */

public class ToppingByPizzaRecyclerAdapter extends BaseRecyclerAdapter<String> {

    @Override
    public ToppingByPizzaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topping_item_list, parent, false);
        return new ToppingByPizzaHolder(view);
    }

    public class ToppingByPizzaHolder extends BaseRecyclerAdapter<String>.ViewHolder {
        @Bind(R.id.title)
        TextView mTitleTextView;

        public ToppingByPizzaHolder(View itemView) {
            super(itemView);
        }

        public void populate(String item) {
            mTitleTextView.setText(item);
        }
    }
}