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
