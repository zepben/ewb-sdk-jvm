/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

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
        assertThat(identifiedObject.name, nullValue())
        assertThat(identifiedObject.description, nullValue())
        assertThat(identifiedObject.numDiagramObjects, nullValue())

        identifiedObject.name = "name"
        identifiedObject.description = "description"
        identifiedObject.numDiagramObjects = 7

        assertThat(identifiedObject.name, equalTo("name"))
        assertThat(identifiedObject.description, equalTo("description"))
        assertThat(identifiedObject.numDiagramObjects, equalTo(7))
    }

    @Test
    internal fun `User can add names to identified object`() {

        val identifiedObject = object : IdentifiedObject("id") {}
        val nameType = NameType("type")
        assertThat(identifiedObject.numNames(), equalTo(0))

        identifiedObject.addName(nameType, "1")

        assertThat(identifiedObject.numNames(), equalTo(1))
    }

    @Test
    internal fun `Adding identical name to the same object doesn't change the list of names belonging to it`() {
        val (identifiedObject: IdentifiedObject, nameType: NameType) = createMultipleBaseNames()
        val originalNames = identifiedObject.names

        identifiedObject.addName(nameType, "1")
        val newNames = identifiedObject.names
        assertThat(originalNames, equalTo(newNames))
    }

    @Test
    internal fun `getName obtain expected Name object`() {
        val (identifiedObject: IdentifiedObject, _: NameType) = createMultipleBaseNames()

        val name1 = identifiedObject.getName("type", "1")!!
        val name2 = identifiedObject.getName("type", "2")!!
        val name3 = identifiedObject.getName("type", "3")!!

        assertThat(name2.name, equalTo("2"))
        assertThat(name2.type.name, equalTo("type"))

        // Make sure item obtained are different
        assertThat(name1, not(equalTo(name2)))
        assertThat(name1, not(equalTo(name3)))
        assertThat(name2, not(equalTo(name3)))
    }

    @Test
    internal fun `getName(String, String) and getName(NameType, String) grabs the same object`() {
        val (identifiedObject: IdentifiedObject, nameType: NameType) = createMultipleBaseNames()

        val name2 = identifiedObject.getName("type", "2")!!
        val dupeName2 = identifiedObject.getName(nameType, "2")
        assertThat(name2, sameInstance(dupeName2))
    }

    @Test
    internal fun `nameType contains the names added to the identified object`() {
        val (_: IdentifiedObject, nameType: NameType) = createMultipleBaseNames()

        assertThat("expected to have name 1", nameType.hasName("1"))
        assertThat("expected to have name 2", nameType.hasName("2"))
        assertThat("expected to have name 3", nameType.hasName("3"))
    }

    @Test
    internal fun `getNames obtains all names of a identified object with a given nameType`() {
        val (identifiedObject: IdentifiedObject, nameType: NameType) = createMultipleBaseNames()

        val nameType2 = NameType("type2")
        val nameType3 = NameType("type3")
        identifiedObject.addName(nameType2, "1")

        assertThat(identifiedObject.getNames(nameType), hasSize(3))
        assertThat(identifiedObject.getNames(nameType2), hasSize(1))
        assertThat(identifiedObject.getNames(nameType3), equalTo(null))
    }

    @Test
    internal fun `removeName removes name from the identified object and the nameType`() {
        val (identifiedObject: IdentifiedObject, nameType: NameType) = createMultipleBaseNames()
        val name1 = identifiedObject.getName("type", "1")!!
        val name2 = identifiedObject.getName("type", "2")!!
        val name3 = identifiedObject.getName("type", "3")!!

        assertThat("name1 successfully removed from nameType", identifiedObject.removeName(name1))
        assertThat("name1 can not be removed from nameType", !identifiedObject.removeName(name1))
        assertThat(identifiedObject.numNames(), equalTo(2))
        assertThat("should not have had name 1", !nameType.hasName("1"))

        assertThat(identifiedObject.names, containsInAnyOrder(name2, name3))
    }

    @Test
    internal fun `clearNames removes all names from the identified object and the nameType`() {
        val (identifiedObject: IdentifiedObject, nameType: NameType) = createMultipleBaseNames()

        assertThat(identifiedObject.numNames(), equalTo(3))
        assertThat("expected to have name 1", nameType.hasName("1"))
        assertThat("expected to have name 2", nameType.hasName("2"))
        assertThat("expected to have name 3", nameType.hasName("3"))

        identifiedObject.clearNames()
        assertThat(identifiedObject.numNames(), equalTo(0))
        assertThat("should not have had name 1", !nameType.hasName("1"))
        assertThat("should not have had name 2", !nameType.hasName("2"))
        assertThat("should not have had name 3", !nameType.hasName("3"))
    }

    @Test
    internal fun `user can add the same name back after it has been removed`() {
        val identifiedObject = object : IdentifiedObject("id") {}
        val nameType = NameType("type")

        identifiedObject.addName(nameType, "1")
        val name1 = identifiedObject.getName("type", "1")!!
        identifiedObject.clearNames()

        identifiedObject.removeName(name1)
        assertThat(identifiedObject.numNames(), equalTo(0))
    }

    @Test
    internal fun `removing name from empty name list does not cause any issue`() {
        val identifiedObject = object : IdentifiedObject("id") {}
        val nameType = NameType("type")

        identifiedObject.addName(nameType, "1")
        val name1 = identifiedObject.getName("type", "1")!!
        assertThat(identifiedObject.numNames(), equalTo(1))

        identifiedObject.removeName(name1)
        assertThat(identifiedObject.numNames(), equalTo(0))

        identifiedObject.addName(nameType, "1")
        val dupeName1 = identifiedObject.getName(nameType, "1")
        assertThat(name1, not(sameInstance(dupeName1)))
        assertThat(identifiedObject.numNames(), equalTo(1))
    }

    private fun createMultipleBaseNames(): Pair<IdentifiedObject, NameType> {
        val identifiedObject = object : IdentifiedObject("id") {}
        val nameType = NameType("type")

        identifiedObject.addName(nameType, "1")
        identifiedObject.addName(nameType, "2")
        identifiedObject.addName(nameType, "3")

        return identifiedObject to nameType
    }
}
