/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.example.android.architecture.blueprints.todoapp.taskdetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.android.architecture.blueprints.todoapp.Injection;
import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskActivity;
import com.example.android.architecture.blueprints.todoapp.data.Task;
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository;

import net.grandcentrix.thirtyinch.TiActivity;
import net.grandcentrix.thirtyinch.viewmodel.LocalizedString;

/**
 * Displays task details screen.
 */
public class TaskDetailActivity extends TiActivity<TaskDetailPresenter, TaskDetailView> implements TaskDetailView, CompoundButton.OnCheckedChangeListener {

    public static final String EXTRA_TASK_ID = "TASK_ID";

    private static final int REQUEST_EDIT_TASK = 1;

    private TextView mDetailTitle;

    private TextView mDetailDescription;

    private CheckBox mDetailCompleteStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.taskdetail_act);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        mDetailTitle = (TextView) findViewById(R.id.task_detail_title);
        mDetailDescription = (TextView) findViewById(R.id.task_detail_description);
        mDetailCompleteStatus = (CheckBox) findViewById(R.id.task_detail_complete);
        mDetailCompleteStatus.setOnCheckedChangeListener(this);

        // Set up floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_edit_task);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().onEditTaskClicked();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                getPresenter().onDeleteClicked();
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.taskdetail_fragment_menu, menu);
        return true;
    }

    @NonNull
    @Override
    public TaskDetailPresenter providePresenter() {
        // Get the requested task id
        final String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        final TasksRepository tasksRepository = Injection
                .provideTasksRepository(getApplicationContext());

        // Create the presenter
        return new TaskDetailPresenter(tasksRepository, taskId);
    }

    @Override
    public void showLoadingIndicator(boolean show) {
        // TODO
    }

    @Override
    public void showTitle(final LocalizedString text) {
        mDetailTitle.setVisibility(text.isEmpty() ? View.GONE : View.VISIBLE);
        mDetailTitle.setText(text.getString(this));
    }

    @Override
    public void showDescription(final LocalizedString text) {
        mDetailDescription.setVisibility(text.isEmpty() ? View.GONE : View.VISIBLE);
        mDetailDescription.setText(text.getString(this));
    }

    @Override
    public void showCompletionStatus(boolean completed) {
        // prevent click listener from firing when setting a new state, only fire for user input events
        mDetailCompleteStatus.setOnCheckedChangeListener(null);
        mDetailCompleteStatus.setChecked(completed);
        mDetailCompleteStatus.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        getPresenter().onCompletionCheckboxClicked(isChecked);
    }

    @Override
    public void showEditTask(Task task) {
        final Intent intent = new Intent(this, AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskActivity.ARGUMENT_EDIT_TASK_ID, task.getId());
        startActivityForResult(intent, REQUEST_EDIT_TASK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_TASK) {
            // If the task was edited successfully, go back to the list.
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void showTaskMarkedComplete() {
        Snackbar.make(mDetailTitle, getString(R.string.task_marked_complete), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showTaskMarkedActive() {
        Snackbar.make(mDetailTitle, getString(R.string.task_marked_active), Snackbar.LENGTH_LONG)
                .show();
    }
}
