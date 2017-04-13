package com.example.app.pizzaapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by juandiegoGL on 4/8/17.
 */

public class GetToppingByPizzaResult extends Topping implements Parcelable {

    @SerializedName("pizza_id")
    int mPizzaId;
    @SerializedName("topping_id")
    int mToppingId;


    @Override
    public String toString() {
        return "GetToppingByPizzaResult{" +
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
        parcel.writeString(this.mId);
        parcel.writeString(this.mName);
        parcel.writeInt(this.mPizzaId);
        parcel.writeInt(mToppingId);
    }

    protected GetToppingByPizzaResult(Parcel in) {
        super(in);
        this.mPizzaId = in.readInt();
        this.mToppingId = in.readInt();
    }

    public static final Parcelable.Creator<GetToppingByPizzaResult> CREATOR = new Parcelable.Creator<GetToppingByPizzaResult>() {
        @Override
        public GetToppingByPizzaResult createFromParcel(Parcel source) {
            return new GetToppingByPizzaResult(source);
        }

        @Override
        public GetToppingByPizzaResult[] newArray(int size) {
            return new GetToppingByPizzaResult[size];
        }
    };
}
