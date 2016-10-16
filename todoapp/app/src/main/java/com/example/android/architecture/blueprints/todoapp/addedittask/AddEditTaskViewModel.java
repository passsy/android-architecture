package com.example.android.architecture.blueprints.todoapp.addedittask;

import com.example.android.architecture.blueprints.todoapp.base.NotifyChangeListener;
import com.example.android.architecture.blueprints.todoapp.data.Task;

import android.support.annotation.NonNull;

class AddEditTaskViewModel {

    private String mDescription = "";

    private boolean mIsLoadingTask = false;

    private NotifyChangeListener<AddEditTaskViewModel> mNotifyChangeListener;

    private Task mOriginalTask = null;

    private String mTitle = "";

    public String getDescription() {
        return mDescription;
    }

    public Task getOriginalTask() {
        return mOriginalTask;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean isLoading() {
        return mIsLoadingTask;
    }

    public void setDescription(@NonNull final String description) {
        mDescription = description;
    }

    public void setLoadingTask(final boolean loadingTask) {
        if (mIsLoadingTask != loadingTask) {
            mIsLoadingTask = loadingTask;
            notifyChange();
        }
    }

    public void setOnChangeListener(
            final NotifyChangeListener<AddEditTaskViewModel> onChangeListener) {
        mNotifyChangeListener = onChangeListener;
        mNotifyChangeListener.onChange(this);
    }

    public void setOriginalTask(@NonNull final Task originalTask) {
        mOriginalTask = originalTask;
        mTitle = originalTask.getTitle();
        mDescription = originalTask.getDescription();
        notifyChange();
    }

    public void setTitle(final String title) {
        mTitle = title;
    }

    private void notifyChange() {
        if (mNotifyChangeListener != null) {
            mNotifyChangeListener.onChange(this);
        }
    }
}
