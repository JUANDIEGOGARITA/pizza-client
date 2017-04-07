package com.example.app.pizzaapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class Topping extends Product implements Parcelable {

    @Override
    public String toString() {
        return "Topping{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mId);
        parcel.writeString(this.mName);
    }

    protected Topping(Parcel in) {
        this.mId = in.readInt();
        this.mName = in.readString();
    }

    public static final Parcelable.Creator<Topping> CREATOR = new Parcelable.Creator<Topping>() {
        @Override
        public Topping createFromParcel(Parcel source) {
            return new Topping(source);
        }

        @Override
        public Topping[] newArray(int size) {
            return new Topping[size];
        }
    };

}
