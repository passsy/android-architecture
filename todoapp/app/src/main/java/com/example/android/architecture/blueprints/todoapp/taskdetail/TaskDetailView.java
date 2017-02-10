package com.example.android.architecture.blueprints.todoapp.taskdetail;

import com.example.android.architecture.blueprints.todoapp.data.Task;

import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;
import net.grandcentrix.thirtyinch.distinctuntilchanged.DistinctUntilChanged;
import net.grandcentrix.thirtyinch.viewmodel.LocalizedString;

public interface TaskDetailView extends TiView {

    @CallOnMainThread
    @DistinctUntilChanged
    void showLoadingIndicator(final boolean show);

    @DistinctUntilChanged
    void showTitle(final LocalizedString text);

    @DistinctUntilChanged
    void showDescription(final LocalizedString text);

    @DistinctUntilChanged
    void showCompletionStatus(final boolean completed);

    @CallOnMainThread
    void showEditTask(final Task task);

    @CallOnMainThread
    void close();

    @CallOnMainThread
    void showTaskMarkedComplete();

    @CallOnMainThread
    void showTaskMarkedActive();

}
