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
import org.hamcrest.CoreMatchers.sameInstance
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class NameTypeTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    fun constructorCoverage() {
        assertThat(NameType("type").name, equalTo("type"))
    }

    @Test
    fun nameAndDescription() {
        val nameType = NameType("type")

        assertThat(nameType.name, equalTo("type"))
        assertThat(nameType.description, equalTo(""))

        nameType.name = "name"
        nameType.description = "description"

        assertThat(nameType.name, equalTo("name"))
        assertThat(nameType.description, equalTo("description"))
    }

    @Test
    fun getsAddsNames() {
        val nameType = NameType("type")
        val idObj1 = Junction()
        val name1 = nameType.getOrAddName("name1", idObj1)
        assertThat(name1, sameInstance(nameType.getOrAddName("name1", idObj1)))
        assertThat(nameType.hasName("name1"), equalTo(true))
    }

    @Test
    fun getNamesReturnsAllInstances() {
        val nameType = NameType("type")
        val idObj1 = Junction()
        val idObj2 = Junction()
        val idObj3 = Junction()
        val name1 = nameType.getOrAddName("name1", idObj1)
        val name2 = nameType.getOrAddName("name1", idObj2)
        val name3 = nameType.getOrAddName("name1", idObj3)
        val names = nameType.getNames("name1")
        assertThat(names, containsInAnyOrder(name1, name2, name3))
    }

    @Test
    fun returnsAllNames() {
        val nameType = NameType("type")
        val idObj1 = Junction()
        val idObj2 = Junction()
        val name1 = nameType.getOrAddName("name1", idObj1)
        val name2 = nameType.getOrAddName("name1", idObj2)
        val name3 = nameType.getOrAddName("name2", idObj2)
        val names = nameType.names.toList()
        assertThat(names, containsInAnyOrder(name1, name2, name3))
    }

    @Test
    fun removesNames() {
        val nameType = NameType("type")
        val idObj1 = Junction()
        val idObj2 = Junction()
        val name1a = nameType.getOrAddName("name1", idObj1)
        val name1b = nameType.getOrAddName("name1", idObj2)
        val name2 = nameType.getOrAddName("name2", idObj2)
        assertThat(nameType.getNames("name1"), containsInAnyOrder(name1a, name1b))
        assertThat(nameType.getNames("name2"), containsInAnyOrder(name2))

        assertThat(nameType.removeName(name1b), equalTo(true))
        assertThat(nameType.getNames("name1"), containsInAnyOrder(name1a))
        assertThat(nameType.getNames("name2"), containsInAnyOrder(name2))

        assertThat(nameType.removeName(name1a), equalTo(true))
        assertThat(nameType.getNames("name1"), empty())
        assertThat(nameType.getNames("name2"), containsInAnyOrder(name2))
    }

    @Test
    internal fun removesNameInstance() {
        val nameType = NameType("type")
        val idObj1 = Junction()
        val idObj2 = Junction()
        val name1a = nameType.getOrAddName("name1", idObj1)
        val name1b = nameType.getOrAddName("name1", idObj2)
        assertThat(nameType.getNames("name1"), containsInAnyOrder(name1a, name1b))

        assertThat(nameType.removeName(name1a), equalTo(true))
        assertThat(nameType.getNames("name1"), containsInAnyOrder(name1b))
    }

    @Test
    fun clearsNames() {
        val nameType = NameType("type")
        val idObj1 = Junction()
        val idObj2 = Junction()
        val name1 = nameType.getOrAddName("name1", idObj1)
        val name2 = nameType.getOrAddName("name2", idObj2)
        assertThat(nameType.getNames("name1"), containsInAnyOrder(name1))
        assertThat(nameType.getNames("name2"), containsInAnyOrder(name2))

        nameType.clearNames()
        assertThat(nameType.names.toList(), empty())
    }
}