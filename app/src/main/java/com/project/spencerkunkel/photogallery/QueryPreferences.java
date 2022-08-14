package com.project.spencerkunkel.photogallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_LAST_RESULT_ID = "lastResultId";
    private static final String PREF_IS_POLLING = "isPolling";

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

    public String getLastResultId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_RESULT_ID, "");
    }

    public void setLastResultId(Context context, String lastResultId){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_RESULT_ID, lastResultId)
                .apply();
    }

    public boolean isPolling(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_POLLING, false);
    }

    public void setPolling(Context context, boolean isOn){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_POLLING, isOn)
                .apply();
    }
}
