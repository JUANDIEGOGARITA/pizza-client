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

public class ToppingRecyclerAdapter extends BaseRecyclerAdapter<String> {

    @Override
    public ToppingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topping_item_list, parent, false);
        return new ToppingHolder(view);
    }

    public class ToppingHolder extends BaseRecyclerAdapter<String>.ViewHolder {
        @Bind(R.id.title)
        TextView titleTextView;

        public ToppingHolder(View itemView) {
            super(itemView);
        }

        public void populate(String item) {
            titleTextView.setText(item);
        }
    }
}

