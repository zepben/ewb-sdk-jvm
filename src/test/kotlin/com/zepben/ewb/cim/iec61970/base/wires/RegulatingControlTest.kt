/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class RegulatingControlTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : RegulatingControl("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val regulatingControl = object : RegulatingControl(generateId()) {}

        assertThat(regulatingControl.discrete, nullValue())
        assertThat(regulatingControl.enabled, nullValue())
        assertThat(regulatingControl.mode, equalTo(RegulatingControlModeKind.UNKNOWN))
        assertThat(regulatingControl.monitoredPhase, equalTo(PhaseCode.NONE))
        assertThat(regulatingControl.targetDeadband, nullValue())
        assertThat(regulatingControl.targetValue, nullValue())
        assertThat(regulatingControl.enabled, nullValue())
        assertThat(regulatingControl.maxAllowedTargetValue, nullValue())
        assertThat(regulatingControl.minAllowedTargetValue, nullValue())
        assertThat(regulatingControl.ratedCurrent, nullValue())
        assertThat(regulatingControl.terminal, nullValue())
        assertThat(regulatingControl.ctPrimary, nullValue())
        assertThat(regulatingControl.minTargetDeadband, nullValue())

        regulatingControl.fillFields(NetworkService())

        assertThat(regulatingControl.discrete, equalTo(false))
        assertThat(regulatingControl.mode, equalTo(RegulatingControlModeKind.voltage))
        assertThat(regulatingControl.monitoredPhase, equalTo(PhaseCode.ABC))
        assertThat(regulatingControl.targetDeadband, equalTo(2.0f))
        assertThat(regulatingControl.targetValue, equalTo(100.0))
        assertThat(regulatingControl.enabled, equalTo(true))
        assertThat(regulatingControl.maxAllowedTargetValue, equalTo(200.0))
        assertThat(regulatingControl.minAllowedTargetValue, equalTo(50.0))
        assertThat(regulatingControl.ratedCurrent, equalTo(10.0))
        assertThat(regulatingControl.terminal, notNullValue())
        assertThat(regulatingControl.ctPrimary, equalTo(1.0))
        assertThat(regulatingControl.minTargetDeadband, equalTo(2.0))
    }

    @Test
    internal fun regulatingCondEqs() {
        PrivateCollectionValidator.validateUnordered(
            { id -> object : RegulatingControl(id) {} },
            { id -> object : RegulatingCondEq(id) {} },
            RegulatingControl::regulatingCondEqs,
            RegulatingControl::numRegulatingCondEqs,
            RegulatingControl::getRegulatingCondEq,
            RegulatingControl::addRegulatingCondEq,
            RegulatingControl::removeRegulatingCondEq,
            RegulatingControl::clearRegulatingCondEqs
        )
    }

}
