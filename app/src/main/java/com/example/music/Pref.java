package com.example.music;

import android.app.Activity;
import android.content.SharedPreferences;

public class Pref {
    private SharedPreferences preferences;

    public Pref(Activity activity) {
        this.preferences = activity.getPreferences(activity.MODE_PRIVATE);
    }

    public void savePosition(int position) {
        int currentPosition = position;

        int lastPosition = preferences.getInt("high_score", 0);
        if (position > lastPosition) {
            preferences.edit().putInt("high_score", currentPosition).apply();
        }
    }

    public int getPosition() {
        return preferences.getInt("high_score", 0);
    }

}

