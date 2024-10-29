/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get

import com.zepben.evolve.streaming.data.CurrentStateEvent
import com.zepben.evolve.streaming.data.SwitchAction
import com.zepben.evolve.streaming.data.SwitchStateEvent
import com.zepben.evolve.streaming.get.testservices.TestQueryNetworkStateService
import com.zepben.protobuf.ns.QueryNetworkStateServiceGrpc
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.mockito.kotlin.spy
import java.time.LocalDateTime
import kotlin.streams.toList

class QueryNetworkStateClientTest {
    @JvmField
    @Rule
    val grpcCleanup: GrpcCleanupRule = GrpcCleanupRule()

    private val serverName = InProcessServerBuilder.generateName()
    private val channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build())
    private val stub = spy(QueryNetworkStateServiceGrpc.newStub(channel))
    private val client = QueryNetworkStateClient(stub, null)
    private val service = TestQueryNetworkStateService()

    init {
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(service).build().start())
    }

    @Test
    fun getCurrentStates() {
        testGetCurrentStates { from, to -> client.getCurrentStates(1, from, to).toList() }
    }

    @Test
    fun getCurrentStatesStream() {
        testGetCurrentStates { from, to -> client.getCurrentStatesStream(1, from, to).toList() }
    }

    private fun testGetCurrentStates(act: (LocalDateTime, LocalDateTime) -> List<List<CurrentStateEvent>>) {
        val from = LocalDateTime.now().plusDays(-1)
        val to = LocalDateTime.now()
        val batches = listOf(
            listOf(SwitchStateEvent("event1", from, "switch-1", SwitchAction.OPEN)),
            listOf(SwitchStateEvent("event2", from, "switch-2", SwitchAction.OPEN)),
            listOf(SwitchStateEvent("event3", from.plusHours(1), "switch-1", SwitchAction.CLOSE)),
            listOf(SwitchStateEvent("event4", from.plusHours(1), "switch-2", SwitchAction.CLOSE))
        )

        service.onGetCurrentStates = spy { _, _ -> batches.asSequence() }

        val result = act(from, to)

        assertThat(result, equalTo(batches))
    }
}
