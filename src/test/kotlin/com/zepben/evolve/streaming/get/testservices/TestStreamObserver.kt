/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.testservices

import io.grpc.Status
import io.grpc.stub.StreamObserver

class TestStreamObserver<T, U>(private val response: StreamObserver<U>, private val onNextHandler: (request: T, response: StreamObserver<U>) -> Unit) :
    StreamObserver<T> {

    override fun onNext(request: T) {
        try {
            onNextHandler(request, response)
        } catch (t: Throwable) {
            onError(Status.ABORTED.withDescription(t.message).asRuntimeException())
        }
    }

    override fun onError(throwable: Throwable) {
        response.onError(throwable)
    }

    override fun onCompleted() {
        response.onCompleted()
    }
}
