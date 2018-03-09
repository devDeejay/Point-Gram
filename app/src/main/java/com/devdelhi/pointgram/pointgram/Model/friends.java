package com.devdelhi.pointgram.pointgram.Model;

/**
 * Created by deejay on 9/3/18.
 */

public class friends {

    String date;
    boolean isSelected;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    friends() {}

    friends(String date) {
        this.date = date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
