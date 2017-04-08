package com.example.app.pizzaapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class Pizza extends Product implements Parcelable {

    @SerializedName("description")
    String mDescription;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    @Override
    public String toString() {
        return "Pizza{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mDescription='" + mDescription + '\'' +
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
        parcel.writeString(this.mDescription);
    }

    protected Pizza(Parcel in) {
        this.mId = in.readInt();
        this.mName = in.readString();
        this.mDescription = in.readString();
    }

    public static final Parcelable.Creator<Pizza> CREATOR = new Parcelable.Creator<Pizza>() {
        @Override
        public Pizza createFromParcel(Parcel source) {
            return new Pizza(source);
        }

        @Override
        public Pizza[] newArray(int size) {
            return new Pizza[size];
        }
    };
}
