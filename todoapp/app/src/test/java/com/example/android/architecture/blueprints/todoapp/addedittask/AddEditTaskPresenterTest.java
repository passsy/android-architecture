/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.addedittask;

import com.example.android.architecture.blueprints.todoapp.data.Task;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository;

import net.grandcentrix.thirtyinch.test.TiPresenterInstructor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link AddEditTaskPresenter}.
 */
public class AddEditTaskPresenterTest {

    @Mock
    private TasksRepository mTasksRepository;

    @Before
    public void setupMocksAndView() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void saveNewTaskToRepository_showsSuccessMessageUi() {
        // Get a reference to the class under test
        final AddEditTaskPresenter presenter = new AddEditTaskPresenter(null, mTasksRepository);
        final TiPresenterInstructor<AddEditTaskView> instructor
                = new TiPresenterInstructor<>(presenter);

        final AddEditTaskView view = mock(AddEditTaskView.class);
        instructor.attachView(view);

        // When the user changes the task fields
        presenter.onTitleChanges("New Task Title");
        presenter.onDescriptionChanges("Some Task Description");

        // When the presenter is asked to save a task
        presenter.saveTask();

        // Then a task is saved in the repository and the view updated
        verify(mTasksRepository).saveTask(any(Task.class)); // saved to the model
        verify(view).showTasksList(); // shown in the UI
    }

    @Test
    public void saveTask_emptyTaskShowsErrorUi() {
        // Get a reference to the class under test
        final AddEditTaskPresenter presenter = new AddEditTaskPresenter(null, mTasksRepository);
        final TiPresenterInstructor<AddEditTaskView> instructor
                = new TiPresenterInstructor<>(presenter);

        final AddEditTaskView view = mock(AddEditTaskView.class);
        instructor.attachView(view);

        // When the presenter is asked to save an empty task
        presenter.saveTask();

        // Then an empty not error is shown in the UI
        verify(view).showEmptyTaskError();
    }

    @Test
    public void loadNonExistentTask_showsErrorMessageUi() {


        // Get a reference to the class under test, loading task which id doesn't exits
        final AddEditTaskPresenter presenter = new AddEditTaskPresenter("1", mTasksRepository);
        final TiPresenterInstructor<AddEditTaskView> instructor
                = new TiPresenterInstructor<>(presenter);

        // When task is requested and view is attached
        final AddEditTaskView view = mock(AddEditTaskView.class);
        instructor.attachView(view);

        // Return error when queried for a task
        final ArgumentCaptor<TasksDataSource.GetTaskCallback> captor =
                ArgumentCaptor.forClass(TasksDataSource.GetTaskCallback.class);
        verify(mTasksRepository).getTask(anyString(), captor.capture());
        // Simulate callback
        captor.getValue().onDataNotAvailable();

        // Then a error is shown
        verify(view).showEmptyTaskError();
    }

    @Test
    public void populateTask_callsRepoAndUpdatesView() {

        // Get a reference to the class under test
        final AddEditTaskPresenter presenter = new AddEditTaskPresenter("1", mTasksRepository);
        final TiPresenterInstructor<AddEditTaskView> instructor
                = new TiPresenterInstructor<>(presenter);

        // When task is requested and view is attached
        final AddEditTaskView view = mock(AddEditTaskView.class);
        instructor.attachView(view);

        verify(view).setTitle("");
        verify(view).setDescription("");
        verify(view).showLoadingIndicator(true);

        // get fake task from repository
        final ArgumentCaptor<TasksDataSource.GetTaskCallback> captor =
                ArgumentCaptor.forClass(TasksDataSource.GetTaskCallback.class);
        verify(mTasksRepository).getTask(eq("1"), captor.capture());

        // Simulate callback
        final Task testTask = new Task("TITLE", "DESCRIPTION");
        captor.getValue().onTaskLoaded(testTask);

        // Then the task repository is queried and the view updated
        verify(view).showLoadingIndicator(false);
        verify(view, atLeastOnce()).setTitle(testTask.getTitle());
        verify(view, atLeastOnce()).setDescription(testTask.getDescription());
    }

    @Test
    public void saveTaskWhenStillLoading_showError() {

        // Get a reference to the class under test
        final AddEditTaskPresenter presenter = new AddEditTaskPresenter("1", mTasksRepository);
        final TiPresenterInstructor<AddEditTaskView> instructor
                = new TiPresenterInstructor<>(presenter);

        // When task is requested and view is attached
        final AddEditTaskView view = mock(AddEditTaskView.class);
        instructor.attachView(view);

        verify(view).setTitle("");
        verify(view).setDescription("");

        // And the request is still loading
        final ArgumentCaptor<TasksDataSource.GetTaskCallback> captor =
                ArgumentCaptor.forClass(TasksDataSource.GetTaskCallback.class);
        verify(mTasksRepository).getTask(eq("1"), captor.capture());

        // And the user saves the task
        presenter.saveTask();

        // Then show error
        verify(view, atLeastOnce()).showLoadingIndicator(true);
        verify(view).showEmptyTaskError();
    }


    @Test
    public void attachNewViewToLoadedAndEditedTask_UpdateUiAccordingly() {

        // Get a reference to the class under test
        final AddEditTaskPresenter presenter = new AddEditTaskPresenter("1", mTasksRepository);
        final TiPresenterInstructor<AddEditTaskView> instructor
                = new TiPresenterInstructor<>(presenter);

        // When task is requested and view is attached
        final AddEditTaskView view = mock(AddEditTaskView.class);
        instructor.attachView(view);

        verify(view).setTitle("");
        verify(view).setDescription("");

        // get fake task from repository
        final ArgumentCaptor<TasksDataSource.GetTaskCallback> captor =
                ArgumentCaptor.forClass(TasksDataSource.GetTaskCallback.class);
        verify(mTasksRepository).getTask(eq("1"), captor.capture());

        // Simulate callback
        final Task testTask = new Task("TITLE", "DESCRIPTION");
        captor.getValue().onTaskLoaded(testTask);

        // Then the task repository is queried and the view updated
        verify(view, atLeastOnce()).setTitle(testTask.getTitle());
        verify(view, atLeastOnce()).setDescription(testTask.getDescription());

        // When the user edits the data
        presenter.onTitleChanges("a");
        presenter.onDescriptionChanges("b");

        // and a new view gets attached
        final AddEditTaskView secondView = mock(AddEditTaskView.class);
        instructor.attachView(secondView);

        // Then the UI doesn't load the task from the repository but shows the edited data
        verify(secondView).setTitle("a");
        verify(secondView).setDescription("b");

        // When saving the data
        presenter.saveTask();

        // Then the edited data is stored in the repository
        verify(mTasksRepository).saveTask(new Task("a", "b", testTask.getId()));
    }

    @Test
    public void saveTaskWithDetachedView_Throw() throws Exception {
        final AddEditTaskPresenter presenter = new AddEditTaskPresenter(null, mTasksRepository);

        try {
            presenter.saveTask();
            fail("did not throw");
        } catch (Exception e) {
            assertThat(e)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("view")
                    .hasMessageContaining("null");
        }
    }
}
