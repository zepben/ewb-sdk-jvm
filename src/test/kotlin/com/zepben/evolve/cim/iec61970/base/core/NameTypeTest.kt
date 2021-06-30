/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class NameTypeTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    fun constructorCoverage() {
        assertThat(NameType("nt").name, equalTo("nt"))
    }

    @Test
    fun accessors() {
        val nt = NameType("nt")
        assertThat(nt.description, equalTo(""))

        nt.description = "description"
        assertThat(nt.description, equalTo("description"))
    }

    @Test
    fun getOrAddNames() {
        val nt = NameType("nt")

        val j1 = Junction()
        val j2 = Junction()
        val j3 = Junction()

        val n1a = nt.getOrAddName("n1", j1)
        val n1b = nt.getOrAddName("n1", j2)
        val n1c = nt.getOrAddName("n1", j3)
        val n2 = nt.getOrAddName("n2", j2)

        assertThat(nt.hasName("n1"), equalTo(true))
        assertThat(nt.hasName("n2"), equalTo(true))
        assertThat(n1a, not(sameInstance(n1b)))
        assertThat(n1a, not(sameInstance(n1c)))
        assertThat(n1b, not(sameInstance(n1c)))

        // test returns same instance if added again
        assertThat(n1a, sameInstance(nt.getOrAddName("n1", j1)))
        assertThat(n1b, sameInstance(nt.getOrAddName("n1", j2)))
        assertThat(n1c, sameInstance(nt.getOrAddName("n1", j3)))
        assertThat(n2, sameInstance(nt.getOrAddName("n2", j2)))
    }

    @Test
    fun names() {
        val nt = NameType("nt")

        val j1 = Junction()
        val j2 = Junction()

        val n1a = nt.getOrAddName("n1", j1)
        val n1b = nt.getOrAddName("n1", j2)
        val n2 = nt.getOrAddName("n2", j2)

        assertThat(nt.names.toList(), containsInAnyOrder(n1a, n1b, n2))
    }

    @Test
    fun getNames() {
        val nt = NameType("nt")

        val j1 = Junction()
        val j2 = Junction()

        val n1a = nt.getOrAddName("n1", j1)
        val n1b = nt.getOrAddName("n1", j2)
        val n2 = nt.getOrAddName("n2", j2)

        assertThat(nt.getNames("n1"), containsInAnyOrder(n1a, n1b))
        assertThat(nt.getNames("n2"), containsInAnyOrder(n2))
        assertThat(nt.getNames("n3"), empty())
    }

    @Test
    fun removesNames() {
        val nt = NameType("nt")

        val j1 = Junction()
        val j2 = Junction()

        val n1a = nt.getOrAddName("n1", j1)
        val n1b = nt.getOrAddName("n1", j2)
        val n2 = nt.getOrAddName("n2", j2)

        assertThat(nt.names.toList(), containsInAnyOrder(n1a, n1b, n2))

        assertThat(nt.removeNames("n1"), equalTo(true))
        assertThat(nt.names.toList(), containsInAnyOrder(n2))

        assertThat(nt.removeNames("n2"), equalTo(true))
        assertThat(nt.names.toList(), empty())

        assertThat(nt.removeNames("n1"), equalTo(false))
    }

    @Test
    fun removeName() {
        val nt = NameType("nt")

        val j1 = Junction()
        val j2 = Junction()

        val n1a = nt.getOrAddName("n1", j1)
        val n1b = nt.getOrAddName("n1", j2)
        val n2 = nt.getOrAddName("n2", j2)
        val n3 = Name("n3", NameType("other"), Junction())

        assertThat(nt.names.toList(), containsInAnyOrder(n1a, n1b, n2))

        assertThat(nt.removeName(n1b), equalTo(true))
        assertThat(nt.names.toList(), containsInAnyOrder(n1a, n2))

        assertThat(nt.removeName(n1a), equalTo(true))
        assertThat(nt.names.toList(), containsInAnyOrder(n2))

        assertThat(nt.removeName(n2), equalTo(true))
        assertThat(nt.names.toList(), empty())

        assertThat(nt.removeName(n1a), equalTo(false))
        assertThat(nt.removeName(n3), equalTo(false))
    }

    @Test
    fun clearNames() {
        val nt = NameType("nt")

        val j1 = Junction()

        val n1 = nt.getOrAddName("n1", j1)
        val n2 = nt.getOrAddName("n2", j1)

        assertThat(nt.names.toList(), containsInAnyOrder(n1, n2))

        nt.clearNames()
        assertThat(nt.names.toList(), empty())
    }

}
