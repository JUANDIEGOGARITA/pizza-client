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

    public Topping(String name) {
        this.mId = null;
        this.mName = name;
    }

    public Topping(Integer id) {
        this.mId = id.toString();
        this.mName = null;
    }

    public Topping(String id, String name) {
        this.mId = id;
        this.mName = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mId);
        parcel.writeString(this.mName);
    }

    protected Topping(Parcel in) {
        this.mId = in.readString();
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
