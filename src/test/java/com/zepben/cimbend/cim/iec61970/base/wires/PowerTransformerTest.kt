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
package com.zepben.cimbend.cim.iec61970.base.wires

import com.zepben.cimbend.common.extensions.typeNameAndMRID
import com.zepben.cimbend.utils.PrivateCollectionValidator
import com.zepben.test.util.ExpectException
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PowerTransformerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PowerTransformer().mRID, not(equalTo("")))
        assertThat(PowerTransformer("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val powerTransformer = PowerTransformer()

        assertThat(powerTransformer.vectorGroup, equalTo(VectorGroup.UNKNOWN))

        powerTransformer.vectorGroup = VectorGroup.DYN11

        assertThat(powerTransformer.vectorGroup, equalTo(VectorGroup.DYN11))
    }

    private var _powerTransformerEnds: MutableList<PowerTransformerEnd>? = null

    @Test
    internal fun powerTransformerEnds() {
        PrivateCollectionValidator.validate(
            { PowerTransformer() },
            { id, pt, en -> PowerTransformerEnd(id).apply { powerTransformer = pt ; en?.let { endNumber = it }} },
            PowerTransformer::numEnds,
            { pt, mRID -> pt.getEnd(mRID) },
            { pt, endNum-> pt.getEnd(endNum) },
            PowerTransformer::ends,
            PowerTransformer::addEnd,
            PowerTransformer::removeEnd,
            PowerTransformer::clearEnds
        )
    }

    @Test
    internal fun `test addEnd`(){
        val pt = PowerTransformer()
        val e1 = PowerTransformerEnd()
        val e2 = PowerTransformerEnd().apply {
            powerTransformer = pt
        }
        val e3 = PowerTransformerEnd().apply {
            powerTransformer = pt
            endNumber = 4
        }

        // Test throws if missing ConductingEquipment
        ExpectException.expect { pt.addEnd(e1) }.toThrow(IllegalArgumentException::class.java).withMessage("${e1.typeNameAndMRID()} references another PowerTransformer ${e1.powerTransformer}, expected $pt.")

        e1.apply {
            powerTransformer = pt
        }
        pt.addEnd(e1)
        pt.addEnd(e2)
        // Test order
        assertThat(pt.ends, Matchers.containsInRelativeOrder(e1, e2))

        // Test order maintained and sequenceNumber was set appropriately
        pt.addEnd(e3)
        assertThat(pt.ends, Matchers.containsInRelativeOrder(e1, e2, e3))
        assertThat(pt.getEnd(1), equalTo(e1))
        assertThat(pt.getEnd(2), equalTo(e2))
        assertThat(pt.getEnd(4), equalTo(e3))

        // Test try to add terminal with same sequence number fails
        val duplicatePowerTransformerEnd = PowerTransformerEnd().apply { powerTransformer = pt; endNumber = 1 }
        ExpectException.expect {
            pt.addEnd(duplicatePowerTransformerEnd)
        }.toThrow(IllegalArgumentException::class.java)
            .withMessage("Unable to add ${duplicatePowerTransformerEnd.typeNameAndMRID()} to ${pt.typeNameAndMRID()}. A ${e1.typeNameAndMRID()} already exists with endNumber 1.")
    }

}
