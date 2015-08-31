package org.dodgybits.shuffle.android.list.view.task;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.persistence.EntityCache;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.core.view.Location;
import org.dodgybits.shuffle.android.list.event.*;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.list.model.ListSettingsCache;
import org.dodgybits.shuffle.android.list.model.ListTitles;
import org.dodgybits.shuffle.android.preference.model.ListSettings;

public class TaskListContext implements Parcelable {
    @NonNull
    protected TaskSelector mSelector;
    protected int mTitleId;
    private boolean mProjectNameVisible = true;

    public static final Parcelable.Creator<TaskListContext> CREATOR
            = new Parcelable.Creator<TaskListContext>() {

        @Override
        public TaskListContext createFromParcel(Parcel source) {
            TaskSelector selector = source.readParcelable(TaskSelector.class.getClassLoader());
            return create(selector);
        }

        @Override
        public TaskListContext[] newArray(int size) {
            return new TaskListContext[size];
        }
    };

    public static final TaskListContext createForContext(Id contextId) {
        TaskSelector selector = TaskSelector.newBuilder().setListQuery(ListQuery.context).
                setContextId(contextId).build();
        return create(selector);
    }

    public static final TaskListContext createForProject(Id projectId) {
        TaskSelector selector = TaskSelector.newBuilder().setListQuery(ListQuery.project).
                setProjectId(projectId).
                build();
        return create(selector);
    }

    public static final TaskListContext createForSearch(String query) {
        TaskSelector selector = TaskSelector.newBuilder().setListQuery(ListQuery.search).
                setSearchQuery(query).build();
        return create(selector);
    }

    public static final TaskListContext create(Location location) {
        TaskSelector.Builder builder = TaskSelector.newBuilder()
                .setListQuery(location.getListQuery())
                .setSearchQuery(location.getSearchQuery());
        if (location.getListQuery() == ListQuery.project) {
            builder.setProjectId(location.getProjectId());
        } else if (location.getListQuery() == ListQuery.context) {
            builder.setContextId(location.getContextId());
        }
        return create(builder.build());
    }

    public static final TaskListContext create(ListQuery query) {
        TaskSelector selector = TaskSelector.newBuilder().setListQuery(query).build();
        return create(selector);
    }

    public static final TaskListContext create(ListQuery query, Id contextId, Id projectId) {
        TaskListContext listContext;
        if (contextId.isInitialised()) {
            listContext = TaskListContext.createForContext(contextId);
        } else {
            if (projectId.isInitialised()) {
                listContext = TaskListContext.createForProject(projectId);
            } else {
                listContext = TaskListContext.create(query);
            }
        }
        return listContext;
    }

    private static final TaskListContext create(TaskSelector selector) {
        ListQuery query = selector.getListQuery();
        return new TaskListContext(selector, ListTitles.getTitleId(query));
    }

    protected TaskListContext(TaskSelector selector, int titleId) {
        mProjectNameVisible = !selector.getProjectId().isInitialised();
        mSelector = selector;
        mTitleId = titleId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mSelector, 0);
    }

    public ListQuery getListQuery() {
        return mSelector.getListQuery();
    }

    public boolean isProjectNameVisible() {
        return mProjectNameVisible;
    }

    public Id getContextId() {
        Id result = Id.NONE;
        if (mSelector.getContextId().isInitialised()) {
            result = mSelector.getContextId();
        }
        return result;
    }

    public Id getProjectId() {
        Id result = Id.NONE;
        if (mSelector.getProjectId().isInitialised()) {
            result = mSelector.getProjectId();
        }
        return result;
    }

    public TaskSelector createSelectorWithPreferences(Context context) {
        ListSettings settings = ListSettingsCache.findSettings(mSelector.getListQuery());
        return mSelector.builderFrom().applyListPreferences(context, settings).build();
    }

    public String createTitle(Context androidContext,
                            EntityCache<org.dodgybits.shuffle.android.core.model.Context> contextCache,
                            EntityCache<Project> projectCache) {
        String title;
        String name;
        if (mSelector.getContextId().isInitialised()) {
            // it's possible the context no longer exists at this point
            org.dodgybits.shuffle.android.core.model.Context context = contextCache.findById(mSelector.getContextId());
            name = context == null ? "?" : context.getName();
            title = name; //androidContext.getString(mTitleId, name);
        } else if (mSelector.getProjectId().isInitialised()) {
            // it's possible the project no longer exists at this point
            Project project = projectCache.findById(mSelector.getProjectId());
            name = project == null ? "?" : project.getName();
            title = name; //androidContext.getString(mTitleId, name);
        } else {
            title = androidContext.getString(mTitleId);
        }

        return title;
    }

    @Override
    public String toString() {
        return "[TaskListContext " + mSelector.getListQuery() + "]";
    }

    public boolean showMoveActions() {
        return getListQuery() == ListQuery.project;
    }

    public boolean showEditActions() {
        return getListQuery() == ListQuery.project || getListQuery() == ListQuery.context;
    }

    public String getSearchQuery() {
        return getListQuery() == ListQuery.search ? mSelector.getSearchQuery() : null;
    }
    
    public String getEditEntityName(Context context) {
        String name;
        switch (getListQuery()) {
            case context:
                name = context.getString(R.string.context_name);
                break;

            case project:
                name = context.getString(R.string.project_name);
                break;

            default:
                throw new UnsupportedOperationException("Cannot create edit event for listContext " + this);
        }

        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskListContext that = (TaskListContext) o;

        if (!mSelector.equals(that.mSelector)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return mSelector.hashCode();
    }
}
