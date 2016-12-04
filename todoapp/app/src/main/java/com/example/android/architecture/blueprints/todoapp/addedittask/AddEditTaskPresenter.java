package com.example.android.architecture.blueprints.todoapp.addedittask;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.architecture.blueprints.todoapp.base.BaseTiPresenter;
import com.example.android.architecture.blueprints.todoapp.base.NotifyChangeListener;
import com.example.android.architecture.blueprints.todoapp.data.Task;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

class AddEditTaskPresenter extends BaseTiPresenter<AddEditTaskView> {

    @Nullable
    private final String mTaskId;

    @NonNull
    private final TasksDataSource mTasksRepository;

    private AddEditTaskViewModel mViewModel;

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
        mViewModel.setDescription(description);
    }

    /**
     * View informs presenter about text changes of the title
     *
     * @param title new title
     */
    void onTitleChanges(final String title) {
        mViewModel.setTitle(title);
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

        final Task task = mViewModel.getOriginalTask();
        if (task == null) {
            // not loaded
            view.showEmptyTaskError();
        } else {
            // create new task with same id
            final Task newTask = new Task(
                    mViewModel.getTitle(),
                    mViewModel.getDescription(),
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
    protected void onAttachView(@NonNull final AddEditTaskView view) {
        super.onAttachView(view);

        // immediately bind the viewmodel to the view, calls for every change
        mViewModel.setOnChangeListener(new NotifyChangeListener<AddEditTaskViewModel>() {
            @Override
            public void onChange(final AddEditTaskViewModel addEditTaskViewModel) {
                bindViewModel(view);
            }
        });
    }

    @Override
    protected void onDetachView() {
        super.onDetachView();
        mViewModel.setOnChangeListener(null);
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        mViewModel = new AddEditTaskViewModel();

        if (mTaskId == null) {
            mViewModel.setOriginalTask(new Task("", ""));
        } else {
            // try fetching the task
            loadTask(mTaskId);
        }
    }

    private void loadTask(final String taskId) {
        mViewModel.setLoadingTask(true);
        mTasksRepository.getTask(taskId, new TasksDataSource.GetTaskCallback() {
            @Override
            public void onDataNotAvailable() {
                sendToView(new ViewAction<AddEditTaskView>() {
                    @Override
                    public void call(final AddEditTaskView view) {
                        view.showEmptyTaskError();
                        mViewModel.setLoadingTask(false);
                    }
                });
            }

            @Override
            public void onTaskLoaded(final Task task) {
                mViewModel.setOriginalTask(task);
                mViewModel.setLoadingTask(false);
            }
        });
    }

    private void bindViewModel(final AddEditTaskView view) {
        view.setTitle(mViewModel.getTitle());
        view.setDescription(mViewModel.getDescription());
        view.showLoadingIndicator(mViewModel.isLoading());
    }
}
