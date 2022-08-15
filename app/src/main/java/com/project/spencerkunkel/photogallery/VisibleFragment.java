package com.project.spencerkunkel.photogallery;

import static com.project.spencerkunkel.photogallery.PollWorker.ACTION_SHOW_NOTIFICATION;
import static com.project.spencerkunkel.photogallery.PollWorker.PERM_PRIVATE;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public abstract class VisibleFragment extends Fragment {

    private static final String TAG = "VisibleFragment";

    private final BroadcastReceiver onShowNotification = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // If we receive this, we're visible, so cancel
            // the notification
            Log.i(TAG, "canceling notification");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ACTION_SHOW_NOTIFICATION);
        requireActivity().registerReceiver(onShowNotification, filter, PERM_PRIVATE, null);
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().unregisterReceiver(onShowNotification);
    }
}
