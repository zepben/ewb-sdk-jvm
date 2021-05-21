/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get

import io.grpc.stub.StreamObserver
import java.util.concurrent.CountDownLatch

class AwaitableStreamObserver<T>(private val processNext: (T) -> Unit) : StreamObserver<T> {

    internal val latch = CountDownLatch(1)
    private var error: Throwable? = null

    override fun onNext(response: T) {
        processNext(response)
    }

    override fun onError(throwable: Throwable) {
        error = throwable
        latch.countDown()
    }

    override fun onCompleted() {
        latch.countDown()
    }

    fun await() {
        latch.await()
        error?.let { throw it }
    }

}
