/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

internal class AwaitableStreamObserverTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val processor = mock<(Int) -> Unit>()
    private val streamObserver = AwaitableStreamObserver(processor)

    @BeforeEach
    internal fun beforeEach() {
        assertThat(streamObserver.latch.count, equalTo(1))
    }

    @Test
    internal fun `onNext calls executor`() {
        streamObserver.onNext(1)
        streamObserver.onNext(2)

        verify(processor).invoke(1)
        verify(processor).invoke(2)
        verifyNoMoreInteractions(processor)

        assertThat(streamObserver.latch.count, equalTo(1))
    }

    @Test
    internal fun `onError throws on await`() {
        val throwable = Throwable()

        streamObserver.onError(throwable)

        assertThat(streamObserver.latch.count, equalTo(0))
        assertThat(expect { streamObserver.await() }.toThrowAny().exception, equalTo(throwable))
    }


    @Test
    internal fun `onComplete triggers await`() {
        streamObserver.onCompleted()
        assertThat(streamObserver.latch.count, equalTo(0))
        streamObserver.await()
    }

}
