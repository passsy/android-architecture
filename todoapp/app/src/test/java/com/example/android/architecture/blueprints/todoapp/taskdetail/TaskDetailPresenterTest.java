package com.example.android.architecture.blueprints.todoapp.taskdetail;


import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository;

import net.grandcentrix.thirtyinch.test.TiPresenterInstructor;
import net.grandcentrix.thirtyinch.viewmodel.LocalizedString;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class TaskDetailPresenterTest {

    @Mock
    private TasksRepository mTasksRepository;

    @Mock
    private TaskDetailView mView;

    @Before
    public void setupMocksAndView() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getUnknownTaskFromRepositoryAndLoadIntoView() {
        // When loading of a task is requested with an invalid task ID.
        final String invalidId = "";

        final TaskDetailPresenter presenter = new TaskDetailPresenter(mTasksRepository, invalidId);
        final TiPresenterInstructor<TaskDetailView> instructor = new TiPresenterInstructor<>(presenter);
        instructor.attachView(mView);

        verify(mView).showTitle(LocalizedString.empty());
        verify(mView).showDescription(LocalizedString.create(R.string.no_data));
    }
}
