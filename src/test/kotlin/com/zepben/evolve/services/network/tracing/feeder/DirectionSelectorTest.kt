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
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class DirectionSelectorTest {

    private val terminal = spy<Terminal>()

    @Test
    fun testNormalDirectionSelectorGet() {
        val ds = DirectionSelector.NORMAL_DIRECTION.select(terminal)

        ds.value

        verify(terminal).normalFeederDirection
        verifyNoMoreInteractions(terminal)
    }

    @Test
    fun testNormalDirectionSelectorSet() {
        val ds = DirectionSelector.NORMAL_DIRECTION.select(terminal)

        assertThat(ds.set(FeederDirection.UPSTREAM), equalTo(true))
        assertThat(ds.set(FeederDirection.UPSTREAM), equalTo(false))

        val inOrder = inOrder(terminal)

        inOrder.verify(terminal).normalFeederDirection
        inOrder.verify(terminal).normalFeederDirection = FeederDirection.UPSTREAM

        inOrder.verify(terminal).normalFeederDirection

        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun testNormalDirectionSelectorAdd() {
        val ds = DirectionSelector.NORMAL_DIRECTION.select(terminal)

        assertThat(ds.add(FeederDirection.UPSTREAM), equalTo(true))
        assertThat(ds.add(FeederDirection.DOWNSTREAM), equalTo(true))
        assertThat(ds.add(FeederDirection.DOWNSTREAM), equalTo(false))

        val inOrder = inOrder(terminal)

        inOrder.verify(terminal).normalFeederDirection
        inOrder.verify(terminal).normalFeederDirection = FeederDirection.UPSTREAM

        inOrder.verify(terminal).normalFeederDirection
        inOrder.verify(terminal).normalFeederDirection = FeederDirection.BOTH

        inOrder.verify(terminal).normalFeederDirection

        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun testNormalDirectionSelectorRemove() {
        val ds = DirectionSelector.NORMAL_DIRECTION.select(terminal)

        assertThat(ds.set(FeederDirection.BOTH), equalTo(true))
        assertThat(ds.remove(FeederDirection.DOWNSTREAM), equalTo(true))
        assertThat(ds.remove(FeederDirection.DOWNSTREAM), equalTo(false))

        val inOrder = inOrder(terminal)

        inOrder.verify(terminal).normalFeederDirection
        inOrder.verify(terminal).normalFeederDirection = FeederDirection.BOTH

        inOrder.verify(terminal).normalFeederDirection
        inOrder.verify(terminal).normalFeederDirection = FeederDirection.UPSTREAM

        inOrder.verify(terminal).normalFeederDirection

        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun testCurrentDirectionSelectorGet() {
        val ds = DirectionSelector.CURRENT_DIRECTION.select(terminal)

        ds.value

        verify(terminal).currentFeederDirection
        verifyNoMoreInteractions(terminal)
    }

    @Test
    fun testCurrentDirectionSelectorSet() {
        val ds = DirectionSelector.CURRENT_DIRECTION.select(terminal)

        assertThat(ds.set(FeederDirection.UPSTREAM), equalTo(true))
        assertThat(ds.set(FeederDirection.UPSTREAM), equalTo(false))

        val inOrder = inOrder(terminal)

        inOrder.verify(terminal).currentFeederDirection
        inOrder.verify(terminal).currentFeederDirection = FeederDirection.UPSTREAM

        inOrder.verify(terminal).currentFeederDirection

        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun testCurrentDirectionSelectorAdd() {
        val ds = DirectionSelector.CURRENT_DIRECTION.select(terminal)

        assertThat(ds.add(FeederDirection.UPSTREAM), equalTo(true))
        assertThat(ds.add(FeederDirection.DOWNSTREAM), equalTo(true))
        assertThat(ds.add(FeederDirection.DOWNSTREAM), equalTo(false))

        val inOrder = inOrder(terminal)

        inOrder.verify(terminal).currentFeederDirection
        inOrder.verify(terminal).currentFeederDirection = FeederDirection.UPSTREAM

        inOrder.verify(terminal).currentFeederDirection
        inOrder.verify(terminal).currentFeederDirection = FeederDirection.BOTH

        inOrder.verify(terminal).currentFeederDirection

        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun testCurrentDirectionSelectorRemove() {
        val ds = DirectionSelector.CURRENT_DIRECTION.select(terminal)

        assertThat(ds.set(FeederDirection.BOTH), equalTo(true))
        assertThat(ds.remove(FeederDirection.DOWNSTREAM), equalTo(true))
        assertThat(ds.remove(FeederDirection.DOWNSTREAM), equalTo(false))

        val inOrder = inOrder(terminal)

        inOrder.verify(terminal).currentFeederDirection
        inOrder.verify(terminal).currentFeederDirection = FeederDirection.BOTH

        inOrder.verify(terminal).currentFeederDirection
        inOrder.verify(terminal).currentFeederDirection = FeederDirection.UPSTREAM

        inOrder.verify(terminal).currentFeederDirection

        inOrder.verifyNoMoreInteractions()
    }

}
