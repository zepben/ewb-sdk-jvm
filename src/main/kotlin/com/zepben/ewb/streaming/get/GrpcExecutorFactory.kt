/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

object GrpcExecutorFactory {
    private val globalThreadCount = AtomicInteger(1)

    fun create(serviceName: String): ExecutorService {
        return Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable).apply {
                name = "ewb-sdk-$serviceName-${globalThreadCount.getAndIncrement()}"
            }
        }
    }
}
