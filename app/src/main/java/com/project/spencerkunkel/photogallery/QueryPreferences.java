package com.project.spencerkunkel.photogallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static QueryPreferences instance = new QueryPreferences();

    private QueryPreferences() {
    }

    public static QueryPreferences getInstance() {
        if (instance == null) {
            instance = new QueryPreferences();
        }
        return instance;
    }

    public String getStoredQuery(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREF_SEARCH_QUERY, "");
    }

    public void setStoredQuery(Context context, String query){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();
    }
}
