package com.example.app.pizzaapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.pizzaapp.R;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter<T>.ViewHolder> {
    private List<T> mItems = Collections.emptyList();
    private OnItemClickListener<T> mOnItemClickListener;


    @Override
    public BaseRecyclerAdapter<T>.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pizza_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerAdapter<T>.ViewHolder holder, int position) {
        holder.populate(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void updateList(List<T> productList) {
        this.mItems = productList;
        notifyDataSetChanged();
    }


    public interface OnItemClickListener<T> {
        void onItemClick(View view, T item, boolean isLongClick);
    }

    public void setOnItemClickListener(final OnItemClickListener<T> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void populate(T item) {

        }

        @Override
        public void onClick(View v) {
            handleClick(v, false);
        }

        @Override
        public boolean onLongClick(View v) {
            return handleClick(v, true);
        }

        private boolean handleClick(View v, boolean isLongClick) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, mItems.get(getAdapterPosition()), isLongClick);
                return true;
            }
            return false;
        }
    }
}