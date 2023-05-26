/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.cim.iec61970.infiec61970.protection.PowerDirectionKind
import com.zepben.evolve.cim.iec61970.infiec61970.protection.ProtectionKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.evolve.utils.PrivateCollectionValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ProtectionEquipmentTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(object : ProtectionEquipment() {}.mRID, not(equalTo("")))
        assertThat(object : ProtectionEquipment("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val protectionEquipment = object : ProtectionEquipment() {}

        assertThat(protectionEquipment.relayDelayTime, nullValue())
        assertThat(protectionEquipment.protectionKind, equalTo(ProtectionKind.UNKNOWN))
        assertThat(protectionEquipment.directable, nullValue())
        assertThat(protectionEquipment.powerDirection, equalTo(PowerDirectionKind.UNKNOWN_DIRECTION))

        protectionEquipment.fillFields(NetworkService())

        assertThat(protectionEquipment.relayDelayTime, equalTo(1.1))
        assertThat(protectionEquipment.protectionKind, equalTo(ProtectionKind.IEF))
        assertThat(protectionEquipment.directable, equalTo(true))
        assertThat(protectionEquipment.powerDirection, equalTo(PowerDirectionKind.FORWARD))
    }

    @Test
    internal fun protectedSwitches() {
        PrivateCollectionValidator.validate(
            { object : ProtectionEquipment() {} },
            { id, _ -> object : ProtectedSwitch(id) {} },
            ProtectionEquipment::numProtectedSwitches,
            ProtectionEquipment::getProtectedSwitch,
            ProtectionEquipment::protectedSwitches,
            ProtectionEquipment::addProtectedSwitch,
            ProtectionEquipment::removeProtectedSwitch,
            ProtectionEquipment::clearProtectedSwitches
        )
    }

}
