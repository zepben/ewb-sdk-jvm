/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant

import com.zepben.ewb.cim.iec61970.base.wires.AcLineSegment
import com.zepben.ewb.cim.iec61970.base.wires.Junction
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectCreation
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectDeletion
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectModification
import com.zepben.testutils.exception.ExpectException.Companion.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.isA
import org.junit.jupiter.api.Test

class ChangeSetServicesTest {


    @Test
    fun testAddCreation() {
        val services = ChangeSetServices()
        val cs = ChangeSet("cs").also { services.addChangeSet(it) }
        val io = AcLineSegment("test")

        services.addCreation(cs, io)

        val obj = cs.getMember("test")!!
        assertThat(obj, isA(ObjectCreation::class.java))
        assertThat(obj.targetObjectMRID, equalTo(io.mRID))
        assertThat(obj.changeSet, equalTo(cs))
        assertThat(services.get(obj), equalTo(io))
    }

    @Test
    fun testAddModification() {
        val services = ChangeSetServices()
        val cs = ChangeSet("cs").also { services.addChangeSet(it) }
        val io = AcLineSegment("test")
        val ioOriginal = AcLineSegment("test")
        val ioThrowsMRID = AcLineSegment("test3")
        val ioThrowsType = Junction("test")

        expect {
            services.addModification(cs, io, ioThrowsMRID)
        }.toThrow<IllegalArgumentException>()
            .withMessage("newObject (${io.mRID}) and originalObject (${ioThrowsMRID.mRID}) must share the same mRID.")

        expect {
            services.addModification(cs, io, ioThrowsType)
        }.toThrow<IllegalArgumentException>()
            .withMessage("newObject (${io.typeNameAndMRID()}) and originalObject (${ioThrowsType.typeNameAndMRID()}) must be of the same type.")

        services.addModification(cs, io, ioOriginal)

        val obj = cs.getMember("test")!!
        assertThat(obj, isA(ObjectModification::class.java))
        assertThat(obj.targetObjectMRID, equalTo(io.mRID))
        assertThat(obj.changeSet, equalTo(cs))
        assertThat(services.get(obj), equalTo(io))
        assertThat(services.getReverseModification(obj as ObjectModification), equalTo(ioOriginal))
    }

    @Test
    fun testAddDeletion() {
        val services = ChangeSetServices()
        val cs = ChangeSet("cs").also { services.addChangeSet(it) }
        val io = AcLineSegment("test")

        services.addDeletion(cs, io)

        val obj = cs.getMember("test")!!
        assertThat(obj, isA(ObjectDeletion::class.java))
        assertThat(obj.targetObjectMRID, equalTo(io.mRID))
        assertThat(obj.changeSet, equalTo(cs))
        assertThat(services.get(obj), equalTo(io))
    }
}