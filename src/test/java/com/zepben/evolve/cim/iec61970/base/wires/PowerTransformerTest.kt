/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61970.base.core.BaseVoltage
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
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
        assertThat(powerTransformer.transformerUtilisation, equalTo(Double.NaN))

        powerTransformer.vectorGroup = VectorGroup.DYN11
        powerTransformer.transformerUtilisation = 1.0

        assertThat(powerTransformer.vectorGroup, equalTo(VectorGroup.DYN11))
        assertThat(powerTransformer.transformerUtilisation, equalTo(1.0))
    }

    @Test
    internal fun powerTransformerEnds() {
        PrivateCollectionValidator.validate(
            { PowerTransformer() },
            { id, pt, en -> PowerTransformerEnd(id).apply { powerTransformer = pt; en?.let { endNumber = it } } },
            PowerTransformer::numEnds,
            { pt, mRID -> pt.getEnd(mRID) },
            { pt, endNum -> pt.getEnd(endNum) },
            PowerTransformer::ends,
            PowerTransformer::addEnd,
            PowerTransformer::removeEnd,
            PowerTransformer::clearEnds
        )
    }

    @Test
    internal fun `test addEnd`() {
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
        ExpectException.expect { pt.addEnd(e1) }.toThrow(IllegalArgumentException::class.java)
            .withMessage("${e1.typeNameAndMRID()} references another PowerTransformer ${e1.powerTransformer}, expected $pt.")

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

    @Test
    internal fun `test primaryVoltage`() {
        val pt = PowerTransformer()
        val e1 = PowerTransformerEnd().apply {
            powerTransformer = pt
        }
        val e2 = PowerTransformerEnd().apply {
            powerTransformer = pt
            baseVoltage = BaseVoltage().apply { nominalVoltage = 20 }
            ratedU = 25
        }

        assertThat(pt.primaryVoltage, equalTo(0))

        pt.baseVoltage = BaseVoltage().apply { nominalVoltage = 5 }
        assertThat(pt.primaryVoltage, equalTo(5))

        pt.addEnd(e1).addEnd(e2)
        assertThat(pt.primaryVoltage, equalTo(0))

        e1.ratedU = 15
        assertThat(pt.primaryVoltage, equalTo(15))

        e1.baseVoltage = BaseVoltage().apply { nominalVoltage = 10 }
        assertThat(pt.primaryVoltage, equalTo(10))
    }

}
