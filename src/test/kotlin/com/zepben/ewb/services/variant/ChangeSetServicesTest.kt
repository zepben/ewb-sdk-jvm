/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant

import com.zepben.ewb.cim.iec61970.base.wires.AcLineSegment
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectCreation
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectDeletion
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectModification
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.isA
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ChangeSetServicesTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    fun testAddCreation() {
        val cs = ChangeSet("cs")
        val services = ChangeSetServices(cs)
        val variantService = VariantService()
        val io = AcLineSegment("test")

        services.addCreation(variantService, cs, io)

        val obj = cs.getMember("test")!!
        assertThat(obj, isA(ObjectCreation::class.java))
        assertThat(obj.targetObjectMRID, equalTo(io.mRID))
        assertThat(obj.changeSet, equalTo(cs))
        assertThat(services.get(obj), equalTo(io))
    }

    @Test
    fun testAddModification() {
        val cs = ChangeSet("cs")
        val services = ChangeSetServices(cs)
        val variantService = VariantService()
        val io = AcLineSegment("test")
        val ioOriginal = AcLineSegment("test")

        //Required Check for addModification has been disabled as that should be picked up later with conflict detection with different handling.

        services.addModification(variantService, cs, io, ioOriginal)

        val obj = cs.getMember("test")!!
        assertThat(obj, isA(ObjectModification::class.java))
        assertThat(obj.targetObjectMRID, equalTo(io.mRID))
        assertThat(obj.changeSet, equalTo(cs))
        assertThat(services.get(obj), equalTo(io))
        assertThat(services.getReverseModification(obj as ObjectModification), equalTo(ioOriginal))
    }

    @Test
    fun testAddDeletion() {
        val cs = ChangeSet("cs")
        val services = ChangeSetServices(cs)
        val variantService = VariantService()
        val io = AcLineSegment("test")

        services.addDeletion(variantService, cs, io)

        val obj = cs.getMember("test")!!
        assertThat(obj, isA(ObjectDeletion::class.java))
        assertThat(obj.targetObjectMRID, equalTo(io.mRID))
        assertThat(obj.changeSet, equalTo(cs))
        assertThat(services.get(obj), equalTo(io))
    }
}
