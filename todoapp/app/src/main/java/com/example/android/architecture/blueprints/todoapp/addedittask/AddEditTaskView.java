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

import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;
import net.grandcentrix.thirtyinch.distinctuntilchanged.DistinctUntilChanged;
import net.grandcentrix.thirtyinch.distinctuntilchanged.EqualsComparator;

/**
 * View interface specifying the functionality of the view
 */
interface AddEditTaskView extends TiView {

    @CallOnMainThread
    @DistinctUntilChanged
    void setDescription(String description);

    @DistinctUntilChanged(comparator = EqualsComparator.class)
    @CallOnMainThread
    void setTitle(String title);

    @CallOnMainThread
    void showEmptyTaskError();

    @CallOnMainThread
    void showLoadingIndicator(boolean show);

    @CallOnMainThread
    void showTasksList();
}
