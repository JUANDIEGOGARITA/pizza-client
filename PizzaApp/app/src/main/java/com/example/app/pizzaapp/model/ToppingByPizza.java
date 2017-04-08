package com.example.app.pizzaapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by juandiegoGL on 4/8/17.
 */

public class ToppingByPizza extends Topping implements Parcelable {

    @SerializedName("pizza_id")
    int mPizzaId;
    @SerializedName("topping_id")
    int mToppingId;


    @Override
    public String toString() {
        return "ToppingByPizza{" +
                "mId=" + mId +
                "mName=" + mName +
                "mPizzaId=" + mPizzaId +
                ", mToppingId=" + mToppingId +
                '}';
    }

    public int getPizzaId() {
        return mPizzaId;
    }

    public void setPizzaId(int mPizzaId) {
        this.mPizzaId = mPizzaId;
    }

    public int getToppingId() {
        return mToppingId;
    }

    public void setToppingId(int mToppingId) {
        this.mToppingId = mToppingId;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.mId);
        parcel.writeString(this.mName);
        parcel.writeInt(this.mPizzaId);
        parcel.writeInt(mToppingId);
    }

    protected ToppingByPizza(Parcel in) {
        super(in);
        this.mPizzaId = in.readInt();
        this.mToppingId = in.readInt();
    }

    public static final Parcelable.Creator<ToppingByPizza> CREATOR = new Parcelable.Creator<ToppingByPizza>() {
        @Override
        public ToppingByPizza createFromParcel(Parcel source) {
            return new ToppingByPizza(source);
        }

        @Override
        public ToppingByPizza[] newArray(int size) {
            return new ToppingByPizza[size];
        }
    };
}
