package com.example.android.architecture.blueprints.todoapp.taskdetail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.data.Task;

import net.grandcentrix.thirtyinch.viewmodel.LocalizedString;

public class TaskDetailViewModel {

    private LocalizedString mTitle = LocalizedString.empty();
    private LocalizedString mDescription = LocalizedString.empty();
    private boolean mIsLoading = false;
    private Task mTask;

    public LocalizedString getDescription() {
        return mDescription;
    }

    public LocalizedString getTitle() {
        return mTitle;
    }

    @Nullable
    public Task getTask() {
        return mTask;
    }

    public void setTask(@NonNull Task task) {
        mTask = task;
        mTitle = LocalizedString.create(mTask.getTitle());
        mDescription = LocalizedString.create(mTask.getDescription());
    }

    public void markTaskAsMissing() {
        mTitle = LocalizedString.empty();
        mDescription = LocalizedString.create(R.string.no_data);

    }

    public void setLoading(final boolean loading) {
        mIsLoading = loading;
    }

    public boolean isLoading() {
        return mIsLoading;
    }

}
