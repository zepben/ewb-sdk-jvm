/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

internal class DirectionSelectorTest {

    private val terminal = spy<Terminal>()

    @Test
    internal fun testNormalDirectionSelectorGet() {
        val ds = DirectionSelector.NORMAL_DIRECTION.select(terminal)

        ds.value

        verify(terminal).normalFeederDirection
        verifyNoMoreInteractions(terminal)
    }

    @Test
    internal fun testNormalDirectionSelectorSet() {
        val ds = DirectionSelector.NORMAL_DIRECTION.select(terminal)

        assertThat("Setting normal direction of normally undirected terminal to UPSTREAM should true", ds.set(FeederDirection.UPSTREAM))
        assertThat("Setting normal direction of normally upstream terminal to UPSTREAM should false", !ds.set(FeederDirection.UPSTREAM))

        val inOrder = inOrder(terminal)

        inOrder.verify(terminal).normalFeederDirection
        inOrder.verify(terminal).normalFeederDirection = FeederDirection.UPSTREAM

        inOrder.verify(terminal).normalFeederDirection

        inOrder.verifyNoMoreInteractions()
    }

    @Test
    internal fun testNormalDirectionSelectorAdd() {
        val ds = DirectionSelector.NORMAL_DIRECTION.select(terminal)

        assertThat("Adding normal upstream direction to normally undirected terminal should return true", ds.add(FeederDirection.UPSTREAM))
        assertThat("Adding normal downstream direction to normally upstream terminal should return true", ds.add(FeederDirection.DOWNSTREAM))
        assertThat("Adding normal downstream direction to normally bidirectional terminal should return false", !ds.add(FeederDirection.DOWNSTREAM))

        val inOrder = inOrder(terminal)

        inOrder.verify(terminal).normalFeederDirection
        inOrder.verify(terminal).normalFeederDirection = FeederDirection.UPSTREAM

        inOrder.verify(terminal).normalFeederDirection
        inOrder.verify(terminal).normalFeederDirection = FeederDirection.BOTH

        inOrder.verify(terminal).normalFeederDirection

        inOrder.verifyNoMoreInteractions()
    }

    @Test
    internal fun testNormalDirectionSelectorRemove() {
        val ds = DirectionSelector.NORMAL_DIRECTION.select(terminal)

        assertThat("Setting normal direction of normally undirected terminal to BOTH should return true", ds.set(FeederDirection.BOTH))
        assertThat("Removing normal downstream direction from normally bidirectional terminal should return true", ds.remove(FeederDirection.DOWNSTREAM))
        assertThat("Removing normal downstream direction from normally upstream terminal should return false", !ds.remove(FeederDirection.DOWNSTREAM))

        val inOrder = inOrder(terminal)

        inOrder.verify(terminal).normalFeederDirection
        inOrder.verify(terminal).normalFeederDirection = FeederDirection.BOTH

        inOrder.verify(terminal).normalFeederDirection
        inOrder.verify(terminal).normalFeederDirection = FeederDirection.UPSTREAM

        inOrder.verify(terminal).normalFeederDirection

        inOrder.verifyNoMoreInteractions()
    }

    @Test
    internal fun testCurrentDirectionSelectorGet() {
        val ds = DirectionSelector.CURRENT_DIRECTION.select(terminal)

        ds.value

        verify(terminal).currentFeederDirection
        verifyNoMoreInteractions(terminal)
    }

    @Test
    internal fun testCurrentDirectionSelectorSet() {
        val ds = DirectionSelector.CURRENT_DIRECTION.select(terminal)

        assertThat("Setting direction of undirected terminal to UPSTREAM should true", ds.set(FeederDirection.UPSTREAM))
        assertThat("Setting direction of upstream terminal to UPSTREAM should false", !ds.set(FeederDirection.UPSTREAM))

        val inOrder = inOrder(terminal)

        inOrder.verify(terminal).currentFeederDirection
        inOrder.verify(terminal).currentFeederDirection = FeederDirection.UPSTREAM

        inOrder.verify(terminal).currentFeederDirection

        inOrder.verifyNoMoreInteractions()
    }

    @Test
    internal fun testCurrentDirectionSelectorAdd() {
        val ds = DirectionSelector.CURRENT_DIRECTION.select(terminal)

        assertThat("Adding upstream direction to undirected terminal should return true", ds.add(FeederDirection.UPSTREAM))
        assertThat("Adding downstream direction to upstream terminal should return true", ds.add(FeederDirection.DOWNSTREAM))
        assertThat("Adding downstream direction to bidirectional terminal should return false", !ds.add(FeederDirection.DOWNSTREAM))

        val inOrder = inOrder(terminal)

        inOrder.verify(terminal).currentFeederDirection
        inOrder.verify(terminal).currentFeederDirection = FeederDirection.UPSTREAM

        inOrder.verify(terminal).currentFeederDirection
        inOrder.verify(terminal).currentFeederDirection = FeederDirection.BOTH

        inOrder.verify(terminal).currentFeederDirection

        inOrder.verifyNoMoreInteractions()
    }

    @Test
    internal fun testCurrentDirectionSelectorRemove() {
        val ds = DirectionSelector.CURRENT_DIRECTION.select(terminal)

        assertThat("Setting direction of undirected terminal to BOTH should return true", ds.set(FeederDirection.BOTH))
        assertThat("Removing downstream direction from bidirectional terminal should return true", ds.remove(FeederDirection.DOWNSTREAM))
        assertThat("Removing downstream direction from upstream terminal should return false", !ds.remove(FeederDirection.DOWNSTREAM))

        val inOrder = inOrder(terminal)

        inOrder.verify(terminal).currentFeederDirection
        inOrder.verify(terminal).currentFeederDirection = FeederDirection.BOTH

        inOrder.verify(terminal).currentFeederDirection
        inOrder.verify(terminal).currentFeederDirection = FeederDirection.UPSTREAM

        inOrder.verify(terminal).currentFeederDirection

        inOrder.verifyNoMoreInteractions()
    }

}
