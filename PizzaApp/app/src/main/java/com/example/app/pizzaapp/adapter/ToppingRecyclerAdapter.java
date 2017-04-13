package com.example.app.pizzaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.app.pizzaapp.R;
import com.example.app.pizzaapp.fragment.CheckListener;
import com.example.app.pizzaapp.model.Topping;

import butterknife.Bind;

/**
 * Created by juandiegoGL on 4/8/17.
 */

public class ToppingRecyclerAdapter extends BaseRecyclerAdapter<Topping> {

    private boolean mIsCheckAvailable = false;
    private CheckListener mListener;

    public ToppingRecyclerAdapter(CheckListener listener, boolean isCheckAvailable) {
        this.mIsCheckAvailable = isCheckAvailable;
        this.mListener = listener;
    }

    public ToppingRecyclerAdapter() {
    }

    @Override
    public ToppingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topping_item_list, parent, false);
        return new ToppingHolder(view);
    }

    public class ToppingHolder extends BaseRecyclerAdapter<Topping>.ViewHolder {
        @Bind(R.id.title)
        TextView mTitle;
        @Bind(R.id.check)
        CheckBox mCheck;

        public ToppingHolder(View itemView) {
            super(itemView);
        }

        public void populate(final Topping item) {
            mTitle.setText(item.getName());
            if (mIsCheckAvailable) {
                mCheck.setVisibility(View.VISIBLE);
            }
            mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        mListener.onToppingChecked(Integer.parseInt(item.getId()));
                    } else {
                        mListener.onToppingUnChecked(Integer.parseInt(item.getId()));
                    }
                }
            });
        }
    }
}