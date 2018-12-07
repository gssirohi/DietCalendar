
package com.techticz.app.model.recipe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Name {
    public Name() {
    }

    @SerializedName("english")
    @Expose
    String english;
    @SerializedName("hindi")
    @Expose
    String hindi;

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getHindi() {
        return hindi;
    }

    public void setHindi(String hindi) {
        this.hindi = hindi;
    }

}
