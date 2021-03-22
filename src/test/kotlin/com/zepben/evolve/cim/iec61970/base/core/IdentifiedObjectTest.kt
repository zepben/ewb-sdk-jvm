/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class IdentifiedObjectTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : IdentifiedObject() {}.mRID, not(equalTo("")))
        assertThat(object : IdentifiedObject() {}.mRID, not(equalTo(object : IdentifiedObject() {}.mRID)))
        assertThat(object : IdentifiedObject("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val identifiedObject = object : IdentifiedObject("id") {}

        assertThat(identifiedObject.mRID, equalTo("id"))
        assertThat(identifiedObject.name, equalTo(""))
        assertThat(identifiedObject.description, equalTo(""))
        assertThat(identifiedObject.numDiagramObjects, equalTo(0))

        identifiedObject.name = "name"
        identifiedObject.description = "description"
        identifiedObject.numDiagramObjects = 7

        assertThat(identifiedObject.name, equalTo("name"))
        assertThat(identifiedObject.description, equalTo("description"))
        assertThat(identifiedObject.numDiagramObjects, equalTo(7))
    }

    @Test
    internal fun names() {
        val identifiedObject = object : IdentifiedObject("id") {}
        val name1 = Name("1", NameType("type"), identifiedObject)
        val name2 = Name("2", NameType("type"), identifiedObject)
        val name3 = Name("3", NameType("type"), identifiedObject)
        val duplicate1 = Name("1", NameType("type"), identifiedObject)

        assertThat(name1, not(equalTo(name2)))
        assertThat(name1, not(equalTo(name3)))
        assertThat(name2, not(equalTo(name3)))

        assertThat(identifiedObject.numNames(), equalTo(0))

        identifiedObject.addName(name1)
        identifiedObject.addName(name2)
        identifiedObject.addName(name3)
        assertThat(identifiedObject.numNames(), equalTo(3))
        identifiedObject.addName(duplicate1)

        assertThat(identifiedObject.numNames(), equalTo(3))

        assertThat(identifiedObject.removeName(name1), equalTo(true))
        assertThat(identifiedObject.removeName(name1), equalTo(false))
        assertThat(identifiedObject.removeName(null), equalTo(false))
        assertThat(identifiedObject.numNames(), equalTo(2))

        assertThat(identifiedObject.getName("type", name2.name), equalTo(name2))

        assertThat(identifiedObject.names, containsInAnyOrder(name2, name3))

        identifiedObject.clearNames()
        assertThat(identifiedObject.numNames(), equalTo(0))

        // Make sure you can add an item back after it has been removed
        identifiedObject.addName(name1)
        assertThat(identifiedObject.numNames(), equalTo(1))

        identifiedObject.removeName(name1)
        assertThat(identifiedObject.numNames(), equalTo(0))

        // Make sure you can call remove on an empty list.
        identifiedObject.removeName(name2)
        assertThat(identifiedObject.numNames(), equalTo(0))
    }
}
