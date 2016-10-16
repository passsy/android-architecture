package com.example.android.architecture.blueprints.todoapp.addedittask;


import com.example.android.architecture.blueprints.todoapp.Injection;
import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository;

import net.grandcentrix.thirtyinch.TiActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

/**
 * Displays an add or edit task screen.
 */
public class AddEditTaskActivity
        extends TiActivity<AddEditTaskPresenter, AddEditTaskNewView>
        implements AddEditTaskNewView {

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    private TextView mDescription;

    private FloatingActionButton mFab;

    private ContentLoadingProgressBar mLoadingIndicator;

    private TextView mTitle;

    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }

    @NonNull
    @Override
    public AddEditTaskPresenter providePresenter() {
        final String taskId = getIntent()
                .getStringExtra(AddEditTaskActivity.ARGUMENT_EDIT_TASK_ID);
        final TasksRepository tasksRepository = Injection
                .provideTasksRepository(getApplicationContext());

        return new AddEditTaskPresenter(taskId, tasksRepository);
    }

    @Override
    public void setDescription(String description) {
        mDescription.setText(description);
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void showEmptyTaskError() {
        Snackbar.make(mTitle, getString(R.string.empty_task_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showLoadingIndicator(final boolean show) {
        mFab.setEnabled(!show);
        if (show) {
            mLoadingIndicator.show();
        } else {
            mLoadingIndicator.hide();
        }
    }

    @Override
    public void showTasksList() {
        supportFinishAfterTransition();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_act);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mTitle = (TextView) findViewById(R.id.add_task_title);
        mDescription = (TextView) findViewById(R.id.add_task_description);
        mLoadingIndicator = (ContentLoadingProgressBar) findViewById(
                R.id.add_task_loading_indicator);

        mFab = (FloatingActionButton) findViewById(R.id.fab_edit_task_done);
        mFab.setImageResource(R.drawable.ic_done);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().saveTask();
            }
        });

        mDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(final Editable s) {
                getPresenter().onDescriptionChanges(s.toString());
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count,
                    final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before,
                    final int count) {

            }
        });

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(final Editable s) {
                getPresenter().onTitleChanges(s.toString());
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count,
                    final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before,
                    final int count) {

            }
        });
    }

}
