/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61970.base.core

import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
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
}
