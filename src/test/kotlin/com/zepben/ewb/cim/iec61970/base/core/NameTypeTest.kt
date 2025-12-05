/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.cim.iec61970.base.wires.Junction
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class NameTypeTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(NameType("nt").name, equalTo("nt"))
    }

    @Test
    internal fun accessors() {
        val nt = NameType("nt")
        assertThat(nt.description, nullValue())

        nt.description = "description"
        assertThat(nt.description, equalTo("description"))
    }

    @Test
    internal fun getOrAddNames() {
        val nt = NameType("nt")

        val j1 = Junction(generateId())
        val j2 = Junction(generateId())
        val j3 = Junction(generateId())

        val n1a = nt.getOrAddName("n1", j1)
        val n1b = nt.getOrAddName("n1", j2)
        val n1c = nt.getOrAddName("n1", j3)
        val n2 = nt.getOrAddName("n2", j2)

        assertThat("expected to have name n1", nt.hasName("n1"))
        assertThat("expected to have name n2", nt.hasName("n2"))
        assertThat(n1a, not(sameInstance(n1b)))
        assertThat(n1a, not(sameInstance(n1c)))
        assertThat(n1b, not(sameInstance(n1c)))

        // test returns same instance if added again
        assertThat(n1a, sameInstance(nt.getOrAddName("n1", j1)))
        assertThat(n1b, sameInstance(nt.getOrAddName("n1", j2)))
        assertThat(n1c, sameInstance(nt.getOrAddName("n1", j3)))
        assertThat(n2, sameInstance(nt.getOrAddName("n2", j2)))

        // identifiedObjects should have these names added to them as well
        assertThat(j1.numNames(), equalTo(1))
        assertThat(j2.numNames(), equalTo(2))
        assertThat(j3.numNames(), equalTo(1))
    }

    @Test
    internal fun names() {
        val nt = NameType("nt")

        val j1 = Junction(generateId())
        val j2 = Junction(generateId())

        val n1a = nt.getOrAddName("n1", j1)
        val n1b = nt.getOrAddName("n1", j2)
        val n2 = nt.getOrAddName("n2", j2)

        assertThat(nt.names.toList(), containsInAnyOrder(n1a, n1b, n2))
    }

    @Test
    internal fun getNames() {
        val nt = NameType("nt")

        val j1 = Junction(generateId())
        val j2 = Junction(generateId())

        val n1a = nt.getOrAddName("n1", j1)
        val n1b = nt.getOrAddName("n1", j2)
        val n2 = nt.getOrAddName("n2", j2)

        assertThat(nt.getNames("n1"), containsInAnyOrder(n1a, n1b))
        assertThat(nt.getNames("n2"), containsInAnyOrder(n2))
        assertThat(nt.getNames("n3"), empty())
    }

    @Test
    internal fun `removesNames remove names from nameType and associated identifiedObject`() {
        val nt = NameType("nt")

        val j1 = Junction(generateId())
        val j2 = Junction(generateId())

        val n1a = nt.getOrAddName("n1", j1)
        val n1b = nt.getOrAddName("n1", j2)
        val n2 = nt.getOrAddName("n2", j2)

        assertThat(j1.numNames(), equalTo(1))
        assertThat(j2.numNames(), equalTo(2))
        assertThat(nt.names.toList(), containsInAnyOrder(n1a, n1b, n2))

        assertThat("n1 successfully removed from nt", nt.removeNames("n1"))
        assertThat(nt.names.toList(), containsInAnyOrder(n2))
        assertThat(j1.numNames(), equalTo(0))
        assertThat(j2.numNames(), equalTo(1))

        assertThat("n2 successfully removed from nt", nt.removeNames("n2"))
        assertThat(nt.names.toList(), empty())
        assertThat(j2.numNames(), equalTo(0))

        assertThat("n1 can not be removed from nt", !nt.removeNames("n1"))
    }

    @Test
    internal fun `getNames retrieves all the names associated with a given identifiedObject`() {
        val nt = NameType("nt")

        val j1 = Junction(generateId())
        val j2 = Junction(generateId())

        val n1a = nt.getOrAddName("n1", j1)
        val n1b = nt.getOrAddName("n1", j2)
        val n2 = nt.getOrAddName("n2", j2)

        assertThat(nt.getNames(j1).size, equalTo(1))
        assertThat("n1a should be found", nt.getNames(j1).contains(n1a))
        assertThat(nt.getNames(j2).size, equalTo(2))
        assertThat("n1b and n2 should be found", nt.getNames(j2).containsAll(listOf(n1b, n2)))
    }

    @Test
    internal fun `removeName remove name from nameType and associated identifiedObject`() {
        val nt = NameType("nt")

        val j1 = Junction(generateId())
        val j2 = Junction(generateId())

        val n1a = nt.getOrAddName("n1", j1)
        val n1b = nt.getOrAddName("n1", j2)
        val n2 = nt.getOrAddName("n2", j2)
        val n3 = Name("n3", NameType("other"), Junction(generateId()))

        assertThat(j1.numNames(), equalTo(1))
        assertThat(j2.numNames(), equalTo(2))
        assertThat(nt.names.toList(), containsInAnyOrder(n1a, n1b, n2))

        assertThat("n1b successfully removed from nt", nt.removeName(n1b))
        assertThat(j1.numNames(), equalTo(1))
        assertThat(j2.numNames(), equalTo(1))
        assertThat(nt.names.toList(), containsInAnyOrder(n1a, n2))

        assertThat("n1a successfully removed from nt", nt.removeName(n1a))
        assertThat(j1.numNames(), equalTo(0))
        assertThat(j2.numNames(), equalTo(1))
        assertThat(nt.names.toList(), containsInAnyOrder(n2))

        assertThat("n2 successfully removed from nt", nt.removeName(n2))
        assertThat(j1.numNames(), equalTo(0))
        assertThat(j2.numNames(), equalTo(0))
        assertThat(nt.names.toList(), empty())

        assertThat("n1a can not be removed from nt", !nt.removeName(n1a))
        assertThat("n3 can not be removed from nt", !nt.removeName(n3))
    }

    @Test
    internal fun `clearNames remove all names from nameType and associated identifiedObject`() {
        val nt = NameType("nt")

        val j1 = Junction(generateId())
        val j2 = Junction(generateId())

        val n1 = nt.getOrAddName("n1", j1)
        val n2 = nt.getOrAddName("n2", j1)
        val n1b = nt.getOrAddName("n1", j2)

        assertThat(nt.names.toList(), containsInAnyOrder(n1, n2, n1b))
        assertThat(j1.numNames(), equalTo(2))
        assertThat(j2.numNames(), equalTo(1))

        nt.clearNames()
        assertThat(nt.names.toList(), empty())
        assertThat(j1.numNames(), equalTo(0))
        assertThat(j2.numNames(), equalTo(0))
    }

}
