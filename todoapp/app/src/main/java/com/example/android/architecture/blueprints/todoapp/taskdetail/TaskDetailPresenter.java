package com.example.android.architecture.blueprints.todoapp.taskdetail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.architecture.blueprints.todoapp.data.Task;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository;
import com.google.common.base.Strings;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.viewmodel.ViewRenderer;

public class TaskDetailPresenter extends TiPresenter<TaskDetailView>
        implements ViewRenderer.RenderFunc<TaskDetailView, TaskDetailViewModel> {

    private final TasksRepository mTasksRepository;

    private final String mTaskId;

    private final ViewRenderer<TaskDetailView, TaskDetailViewModel> mRenderer =
            new ViewRenderer<>(this, new TaskDetailViewModel(), this);

    public TaskDetailPresenter(final TasksRepository tasksRepository, @Nullable final String taskId) {
        mTasksRepository = tasksRepository;
        mTaskId = taskId;
    }

    @Override
    protected void onAttachView(@NonNull TaskDetailView view) {
        super.onAttachView(view);
        loadTask(mTaskId);
    }

    private void loadTask(final String taskId) {
        if (Strings.isNullOrEmpty(taskId)) {
            mRenderer.getViewModel().markTaskAsMissing();
            mRenderer.invalidate();
            return;
        }

        mRenderer.getViewModel().setLoading(true);
        mRenderer.invalidate();

        mTasksRepository.getTask(taskId, new TasksDataSource.GetTaskCallback() {
            @Override
            public void onDataNotAvailable() {
                mRenderer.getViewModel().setLoading(false);
                mRenderer.getViewModel().markTaskAsMissing();
                mRenderer.invalidate();
            }

            @Override
            public void onTaskLoaded(final Task task) {
                mRenderer.getViewModel().setTask(task);
                mRenderer.getViewModel().setLoading(false);
                mRenderer.invalidate();
            }
        });
    }

    public void onEditTaskClicked() {
        final TaskDetailView view = getViewOrThrow();
        final TaskDetailViewModel viewModel = mRenderer.getViewModel();
        final Task task = viewModel.getTask();

        if (task == null) {
            viewModel.markTaskAsMissing();
            mRenderer.invalidate();
        }

        view.showEditTask(task);
    }

    @NonNull
    private TaskDetailView getViewOrThrow() {
        final TaskDetailView view = getView();
        if (view == null) {
            throw new IllegalStateException("this method is expected to be called from the view");
        }
        return view;
    }

    public void onDeleteClicked() {
        final TaskDetailView view = getViewOrThrow();
        final TaskDetailViewModel viewModel = mRenderer.getViewModel();
        final Task task = viewModel.getTask();

        if (task == null) {
            viewModel.markTaskAsMissing();
            mRenderer.invalidate();
            return;
        }

        mTasksRepository.deleteTask(task.getId());
        view.close();
    }

    public void onCompletionCheckboxClicked(boolean isChecked) {
        final TaskDetailView view = getViewOrThrow();
        final TaskDetailViewModel viewModel = mRenderer.getViewModel();
        final Task task = viewModel.getTask();

        if (task == null) {
            viewModel.markTaskAsMissing();
            mRenderer.invalidate();
            return;
        }

        if (task.isActive()) {
            mTasksRepository.completeTask(task.getId());
            view.showTaskMarkedComplete();
        } else {
            mTasksRepository.activateTask(task.getId());
            view.showTaskMarkedActive();
        }
        loadTask(task.getId());
    }

    @Override
    public void render(@NonNull TaskDetailView view, @NonNull TaskDetailViewModel viewModel) {
        view.showDescription(viewModel.getDescription());
        view.showTitle(viewModel.getTitle());
        view.showLoadingIndicator(viewModel.isLoading());
        final Task task = viewModel.getTask();
        if (task != null) {
            view.showCompletionStatus(task.isCompleted());
        } else {
            view.showCompletionStatus(false);
        }
    }
}
