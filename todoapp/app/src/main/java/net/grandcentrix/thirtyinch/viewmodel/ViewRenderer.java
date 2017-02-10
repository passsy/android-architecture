/*
 * Copyright (C) 2017 grandcentrix GmbH
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.grandcentrix.thirtyinch.viewmodel;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.grandcentrix.thirtyinch.TiLifecycleObserver;
import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.TiView;

/**
 * Automatically calls the render function when the view model changes or a new view attaches.
 * Works best with immutable view models because every call to {@link #setViewModel(Object)}
 * triggers rendering. When you viewModel is mutable use {@link #invalidate()} to
 * trigger a call to the render function.
 * <p>
 *
 * @param <V>  the type of the {@link TiView}
 * @param <VM> type of the view model
 */
public class ViewRenderer<V extends TiView, VM> implements TiLifecycleObserver {

    @NonNull
    private final TiPresenter<V> mPresenter;
    /**
     * render() function executing the rendering of the view based on the view model
     */
    private final RenderFunc<V, VM> mRenderBlock;
    /**
     * Lock synchronizing write access on {@link #mViewModel}, {@link #mIsRenderingPending} and
     * {@link #mLastRenderedVirtualView}
     */
    private final Object mRenderingLock = new Object();
    /**
     * boolean flag indicating a rendering on the main thread is pending. It's therefore not
     * required to call runOnUiThread(() -> performRender(true)); again because a runnable already
     * got posted
     */
    private boolean mIsRenderingPending = false;
    /**
     * the last view model which got rendered. Used for checks with current view model, if both
     * are equal, render shouldn't be called
     */
    @Nullable
    private VM mLastRenderedVirtualView = null;
    /**
     * The 1:1 representation of the View as pojo will be used to automatically call
     * render(Object, TiView) when the setter or {@link #invalidate()} gets
     * called.
     */
    @NonNull
    private VM mViewModel;

    /**
     * indicator for the next render that a force was queued
     */
    private boolean mForceQueued;

    public ViewRenderer(@NonNull final TiPresenter<V> presenter, @NonNull final VM
            initialViewModel, final RenderFunc<V, VM> renderBlock) {
        mPresenter = presenter;
        mViewModel = initialViewModel;
        mRenderBlock = renderBlock;

        mPresenter.addLifecycleObserver(this);
    }

    @NonNull
    public VM getViewModel() {
        return mViewModel;
    }

    public void setViewModel(@NonNull final VM viewModel) {
        synchronized (mRenderingLock) {
            mViewModel = viewModel;
            dispatchRender(false);
        }
    }

    /**
     * It's recommended to use a immutable view model. In case you are using a mutable view model
     * you can call this method to trigger a call of render(Object, TiView)
     */
    public void invalidate() {
        dispatchRender(true);
    }

    @Override
    public void onChange(final TiPresenter.State state,
                         final boolean hasLifecycleMethodBeenCalled) {

        if (state == TiPresenter.State.VIEW_ATTACHED && hasLifecycleMethodBeenCalled) {
            // after onAttachView(view)
            dispatchRender(false);
        }

        if (state == TiPresenter.State.VIEW_DETACHED && hasLifecycleMethodBeenCalled) {
            // after onDetachView()
            synchronized (mRenderingLock) {
                // next attached view will be rendered again, we don't know if it is the same view
                mLastRenderedVirtualView = null;
                mIsRenderingPending = false;
            }
        }
    }

    /**
     * schedules a call to {@link #performRender(boolean)} on the ui thread, skips when a call to
     * render is already pending
     *
     * @param force when true forces a call to render() even when the view model object did not
     *              change
     */
    private void dispatchRender(final boolean force) {
        if (mPresenter.getView() == null) {
            // no view to render, rendering will be automatically triggered when the view attaches
            mForceQueued = true;
            return;
        }
        synchronized (mRenderingLock) {
            if (!mIsRenderingPending || force) {
                mIsRenderingPending = true;
                // no render operation posted, dispatch render on the UI thread
                mPresenter.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        performRender(force);
                    }
                });
            }
        }
    }

    /**
     * calls render(VM, V) when the view model wasn't already rendered to the view
     *
     * @param force forces a render(Object, TiView) call even when the view model hasn't changed
     */
    private void performRender(final boolean force) {

        synchronized (mRenderingLock) {
            try {
                final V view = mPresenter.getView();
                if (view == null) {
                    // no view to render to
                    return;
                }

                final VM newViewModel = mViewModel;
                final VM lastRenderedViewModel = mLastRenderedVirtualView;

                if (!(force || mForceQueued)) {
                    if (newViewModel.equals(lastRenderedViewModel)) {
                        // render not required, virtual view hasn't changed
                        return;
                    }
                }
                mForceQueued = false;

                mRenderBlock.render(view, newViewModel);
                mLastRenderedVirtualView = newViewModel;
            } finally {
                mIsRenderingPending = false;
            }
        }
    }

    public interface RenderFunc<V, VM> {

        /**
         * render the view which should represent the virtualView, will be called on the ui thread
         */
        void render(@NonNull final V view, @NonNull final VM viewModel);
    }
}
