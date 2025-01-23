/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

import kotlin.reflect.KMutableProperty1

class FeederDirectionStateOperatorsTest {

    private val normal = FeederDirectionStateOperations.NORMAL
    private val current = FeederDirectionStateOperations.CURRENT

    @Test
    fun getDirection() {
        fun test(operations: FeederDirectionStateOperations, directionProp: KMutableProperty1<Terminal, FeederDirection>) {
            val terminal = Terminal()
            directionProp.set(terminal, FeederDirection.UPSTREAM)
            assertThat(operations.getDirection(terminal), equalTo(FeederDirection.UPSTREAM))
        }

        test(normal, Terminal::normalFeederDirection)
        test(current, Terminal::currentFeederDirection)
    }

    @Test
    fun setDirection() {
        fun test(operations: FeederDirectionStateOperations, directionProp: KMutableProperty1<Terminal, FeederDirection>) {
            val terminal = Terminal()
            directionProp.set(terminal, FeederDirection.NONE)
            assertThat(operations.setDirection(terminal, FeederDirection.UPSTREAM), equalTo(true))
            assertThat(directionProp.get(terminal), equalTo(FeederDirection.UPSTREAM))

            // Attempting to add a direction the terminal already has should return false
            assertThat(operations.setDirection(terminal, FeederDirection.UPSTREAM), equalTo(false))

            // Setting direction should replace the existing direction
            assertThat(operations.setDirection(terminal, FeederDirection.DOWNSTREAM), equalTo(true))
            assertThat(directionProp.get(terminal), equalTo(FeederDirection.DOWNSTREAM))
        }

        test(normal, Terminal::normalFeederDirection)
        test(current, Terminal::currentFeederDirection)
    }

    @Test
    fun addDirection() {
        fun test(operations: FeederDirectionStateOperations, directionProp: KMutableProperty1<Terminal, FeederDirection>) {
            val terminal = Terminal()
            directionProp.set(terminal, FeederDirection.NONE)
            assertThat(operations.addDirection(terminal, FeederDirection.UPSTREAM), equalTo(true))
            assertThat(directionProp.get(terminal), equalTo(FeederDirection.UPSTREAM))

            // Attempting to add a direction the terminal already has should return false
            assertThat(operations.addDirection(terminal, FeederDirection.UPSTREAM), equalTo(false))

            // Adding a direction should end up with a combination of the directions
            assertThat(operations.addDirection(terminal, FeederDirection.DOWNSTREAM), equalTo(true))
            assertThat(directionProp.get(terminal), equalTo(FeederDirection.BOTH))
        }

        test(normal, Terminal::normalFeederDirection)
        test(current, Terminal::currentFeederDirection)
    }

    @Test
    fun removeDirection() {
        fun test(operations: FeederDirectionStateOperations, directionProp: KMutableProperty1<Terminal, FeederDirection>) {
            val terminal = Terminal()
            directionProp.set(terminal, FeederDirection.BOTH)
            assertThat(operations.removeDirection(terminal, FeederDirection.UPSTREAM), equalTo(true))
            assertThat(directionProp.get(terminal), equalTo(FeederDirection.DOWNSTREAM))

            // Attempting to remove a direction the terminal does not have should return false
            assertThat(operations.removeDirection(terminal, FeederDirection.UPSTREAM), equalTo(false))
        }

        test(normal, Terminal::normalFeederDirection)
        test(current, Terminal::currentFeederDirection)
    }
}
