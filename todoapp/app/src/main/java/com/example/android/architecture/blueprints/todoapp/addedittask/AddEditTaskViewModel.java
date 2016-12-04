package com.example.android.architecture.blueprints.todoapp.addedittask;

import android.support.annotation.NonNull;

import com.example.android.architecture.blueprints.todoapp.base.NotifyChangeListener;
import com.example.android.architecture.blueprints.todoapp.data.Task;

class AddEditTaskViewModel {

    private String mDescription = "";

    private boolean mIsLoadingTask = false;

    private NotifyChangeListener<AddEditTaskViewModel> mNotifyChangeListener;

    private Task mOriginalTask = null;

    private String mTitle = "";

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(@NonNull final String description) {
        mDescription = description;
    }

    public Task getOriginalTask() {
        return mOriginalTask;
    }

    public void setOriginalTask(@NonNull final Task originalTask) {
        mOriginalTask = originalTask;
        mTitle = originalTask.getTitle();
        mDescription = originalTask.getDescription();
        notifyChange();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String title) {
        mTitle = title;
    }

    public boolean isLoading() {
        return mIsLoadingTask;
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
        if (mNotifyChangeListener != null) {
            mNotifyChangeListener.onChange(this);
        }
    }

    private void notifyChange() {
        if (mNotifyChangeListener != null) {
            mNotifyChangeListener.onChange(this);
        }
    }
}
