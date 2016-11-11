/*
 * Copyright (c) 2016, by Sergio Arnillas.
 */
package com.serarni.common;

import android.support.annotation.NonNull;

public interface TaskCallback<T> {

    void success(@NonNull T result);
    void error(@NonNull TaskError e);
    void completed();
}
