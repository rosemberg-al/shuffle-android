package org.dodgybits.shuffle.android.core.listener;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.inject.Inject;

import org.dodgybits.shuffle.android.core.event.CompletedToggleEvent;
import org.dodgybits.shuffle.android.core.event.LoadListCursorEvent;
import org.dodgybits.shuffle.android.core.model.persistence.selector.Flag;
import org.dodgybits.shuffle.android.list.activity.ListSettingsEditorActivity;
import org.dodgybits.shuffle.android.list.event.EditListSettingsEvent;
import org.dodgybits.shuffle.android.list.model.ListSettingsCache;
import org.dodgybits.shuffle.android.preference.model.ListSettings;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import roboguice.event.EventManager;
import roboguice.event.Observes;
import roboguice.inject.ContextSingleton;

@ContextSingleton
public class ListSettingsListener {
    private static final String TAG = "ListSettingsListener";

    private Activity mActivity;
    private EventManager mEventManager;

    @Inject
    public ListSettingsListener(Activity activity, EventManager eventManager) {
        mActivity = activity;
        mEventManager = eventManager;
    }

    private void onCompletedToggle(@Observes CompletedToggleEvent event) {
        Flag flag = event.isChecked() ? Flag.yes : Flag.no;
        Log.d(TAG, "Received event " + event);
        ListSettingsCache.findSettings(event.getListQuery()).setCompleted(mActivity, flag);
        mEventManager.fire(new LoadListCursorEvent(event.getViewMode()));
    }

    private void onEditListSettings(@Observes EditListSettingsEvent event) {
        Intent intent = ListSettingsCache.createListSettingsEditorIntent(mActivity, event.getListQuery());
        event.getActivity().startActivityForResult(intent, event.getRequestCode());
    }

}
