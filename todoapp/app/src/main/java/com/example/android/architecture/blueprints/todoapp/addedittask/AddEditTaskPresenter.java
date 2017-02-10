package com.example.android.architecture.blueprints.todoapp.addedittask;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.architecture.blueprints.todoapp.data.Task;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.ViewAction;
import net.grandcentrix.thirtyinch.viewmodel.ViewRenderer;

import static com.google.common.base.Preconditions.checkNotNull;

class AddEditTaskPresenter extends TiPresenter<AddEditTaskView>
        implements ViewRenderer.RenderFunc<AddEditTaskView, AddEditTaskViewModel> {

    @Nullable
    private final String mTaskId;

    @NonNull
    private final TasksDataSource mTasksRepository;

    private final ViewRenderer<AddEditTaskView, AddEditTaskViewModel> mRenderer =
            new ViewRenderer<>(this, new AddEditTaskViewModel(), this);

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param taskId          ID of the task to edit or null for a new task
     * @param tasksRepository a repository of data for tasks
     */
    AddEditTaskPresenter(@Nullable String taskId,
                         @NonNull TasksDataSource tasksRepository) {
        mTaskId = taskId;
        mTasksRepository = checkNotNull(tasksRepository);
    }

    /**
     * View informs presenter about text changes of the description
     *
     * @param description new description text
     */
    void onDescriptionChanges(final String description) {
        mRenderer.getViewModel().setDescription(description);
    }

    /**
     * View informs presenter about text changes of the title
     *
     * @param title new title
     */
    void onTitleChanges(final String title) {
        mRenderer.getViewModel().setTitle(title);
    }

    /**
     * User triggers the save action
     */
    void saveTask() {
        final AddEditTaskView view = getView();
        if (view == null) {
            throw new IllegalStateException(
                    "call this from the view, therefore view is expected to be non null");
        }

        final Task task = mRenderer.getViewModel().getOriginalTask();
        if (task == null) {
            // not loaded
            view.showEmptyTaskError();
        } else {
            // create new task with same id
            final Task newTask = new Task(
                    mRenderer.getViewModel().getTitle(),
                    mRenderer.getViewModel().getDescription(),
                    task.getId());

            if (newTask.isEmpty()) {
                // no enough information
                view.showEmptyTaskError();
                return;
            }

            mTasksRepository.saveTask(newTask);
            view.showTasksList();
        }
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        if (mTaskId == null) {
            mRenderer.getViewModel().setOriginalTask(new Task("", ""));
            mRenderer.invalidate();
        } else {
            // try fetching the task
            loadTask(mTaskId);
        }
    }

    private void loadTask(final String taskId) {
        mRenderer.getViewModel().setLoadingTask(true);
        mRenderer.invalidate();

        mTasksRepository.getTask(taskId, new TasksDataSource.GetTaskCallback() {
            @Override
            public void onDataNotAvailable() {
                sendToView(new ViewAction<AddEditTaskView>() {
                    @Override
                    public void call(final AddEditTaskView view) {
                        view.showEmptyTaskError();
                        mRenderer.getViewModel().setLoadingTask(false);
                        mRenderer.invalidate();
                    }
                });
            }

            @Override
            public void onTaskLoaded(final Task task) {
                mRenderer.getViewModel().setOriginalTask(task);
                mRenderer.getViewModel().setLoadingTask(false);
                mRenderer.invalidate();
            }
        });
    }

    @Override
    public void render(@NonNull AddEditTaskView view, @NonNull AddEditTaskViewModel viewModel) {
        view.setTitle(viewModel.getTitle());
        view.setDescription(viewModel.getDescription());
        view.showLoadingIndicator(viewModel.isLoading());
    }
}
