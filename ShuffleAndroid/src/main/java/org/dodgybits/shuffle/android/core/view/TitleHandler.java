package org.dodgybits.shuffle.android.core.view;

import android.util.Log;

import com.google.inject.Inject;

import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.persistence.EntityCache;
import org.dodgybits.shuffle.android.core.util.UiUtilities;
import org.dodgybits.shuffle.android.list.model.ListQuery;

import roboguice.inject.ContextSingleton;

@ContextSingleton
public class TitleHandler {
    private static final String TAG = "TitleHandler";

    @Inject
    EntityCache<Context> mContextCache;

    @Inject
    EntityCache<Project> mProjectCache;

    public String getTitle(android.content.Context context, Location location) {
        String title = "";
        ListQuery listQuery = location.getListQuery();
        switch (location.getViewMode()) {
            case TASK:
                // blank for task view unless in landscape mode on tablet
                // also don't show in project task list view as project already shown in task view
                if (UiUtilities.showListOnViewTask(context.getResources()) && listQuery != ListQuery.project) {
                    title = getTaskListTitle(context, location);
                }
                break;
            case TASK_LIST:
                title = getTaskListTitle(context, location);
                break;
            case CONTEXT_LIST:
            case PROJECT_LIST:
            case SEARCH_RESULTS_LIST:
            case SEARCH_RESULTS_TASK:
                title = getListQueryTitle(context, listQuery);
                break;
        }
        return title;
    }

    private String getTaskListTitle(android.content.Context androidContext, Location location) {
        String title = "";
        ListQuery listQuery = location.getListQuery();
        if (listQuery == ListQuery.context) {
            Context context = mContextCache.findById(location.getContextId());
            if (context != null) {
                title = context.getName();
            } else {
                Log.e(TAG, "Failed to find context in cache " + location.getContextId());
            }
        } else if (listQuery == ListQuery.project) {
            Project project = mProjectCache.findById(location.getProjectId());
            if (project != null) {
                title = project.getName();
            } else {
                Log.e(TAG, "Failed to find project in cache " + location.getProjectId());
            }
        }
        if (title.isEmpty()) {
            title = getListQueryTitle(androidContext, listQuery);
        }
        return title;
    }

    private String getListQueryTitle(android.content.Context context, ListQuery listQuery) {
        return UiUtilities.getTitle(context.getResources(), listQuery);
    }

}
