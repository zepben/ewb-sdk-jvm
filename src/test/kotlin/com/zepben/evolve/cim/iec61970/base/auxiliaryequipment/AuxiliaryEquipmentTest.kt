/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.auxiliaryequipment

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AuxiliaryEquipmentTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : AuxiliaryEquipment() {}.mRID, not(equalTo("")))
        assertThat(object : AuxiliaryEquipment("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val auxiliaryEquipment = object : AuxiliaryEquipment() {}
        val terminal = Terminal()

        assertThat(auxiliaryEquipment.terminal, nullValue())

        auxiliaryEquipment.terminal = terminal

        assertThat(auxiliaryEquipment.terminal, equalTo(terminal))
    }
}
