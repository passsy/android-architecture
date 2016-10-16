package com.example.android.architecture.blueprints.todoapp.base;


import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.TiView;

import java.util.concurrent.LinkedBlockingQueue;

public class BaseTiPresenter<V extends TiView> extends TiPresenter<V> {

    public static interface ViewAction<T> {
        void call(T t);
    }

    private LinkedBlockingQueue<ViewAction<V>> mPostponedViewActions = new LinkedBlockingQueue<>();

    @Override
    protected void onWakeUp() {
        super.onWakeUp();
        sendPostponedActionsToView(getView());
    }

    /**
     * send actions to the view which may currently not be attached. Once the view is connected
     * again the action gets called.
     *
     * @see #sendPostponedActionsToView
     */
    protected void sendToView(ViewAction<V> action) {
        final V view = getView();
        if (view != null) {
            action.call(view);
        } else {
            mPostponedViewActions.add(action);
        }
    }

    private void sendPostponedActionsToView(V view) {
        while (!mPostponedViewActions.isEmpty()) {
            mPostponedViewActions.poll().call(view);
        }
    }

}
