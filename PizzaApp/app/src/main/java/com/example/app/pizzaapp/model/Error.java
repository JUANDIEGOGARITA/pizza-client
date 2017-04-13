package com.example.app.pizzaapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by juandiegoGL on 4/9/17.
 */

public class Error implements Parcelable {

    @SerializedName("pizza_topping")
    List<String> mErrors;


    public List<String> getErrors() {
        return mErrors;
    }

    @Override
    public String toString() {
        return "Error{" +
                "errors=" + mErrors +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(this.mErrors);
    }

    public Error(Parcel in) {
        in.readStringList(mErrors);
    }

    public static final Parcelable.Creator<Error> CREATOR = new Parcelable.Creator<Error>() {
        @Override
        public Error createFromParcel(Parcel source) {
            return new Error(source);
        }

        @Override
        public Error[] newArray(int size) {
            return new Error[size];
        }
    };
}