/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.testdata

import com.zepben.ewb.services.BaseFillFieldsTest
import com.zepben.ewb.services.network.NetworkService


class NetworkFillFieldsTest : BaseFillFieldsTest(NetworkService(), Class.forName("${NetworkFillFieldsTest::class.java.packageName}.FillFieldsKt")) {

    override val ignoredProperties: Set<String> = setOf(
        "maxTerminals",         // read-only overridden by implementation
        "baseVoltageValue",     // calculated non-nullable. baseVoltage is checked anyway
        "inService",            // default is true
        "normallyInService",    // default is true
        "t3",

        // t2 - maxTerminals=1
        "BusbarSection.t2",
        "Clamp.t2",

        // assetInfo
        "EnergyConsumer.assetInfo",
        "EnergySource.assetInfo",
        "PowerElectronicsConnection.assetInfo",
        "SeriesCompensator.assetInfo",
        "Junction.assetInfo",
        "GroundingImpedance.assetInfo",
        "SynchronousMachine.assetInfo",
        "PetersenCoil.assetInfo",
        "StaticVarCompensator.assetInfo",
        "Ground.assetInfo",
        "EquivalentBranch.assetInfo",
        "EvChargingUnit.assetInfo",
        "BatteryUnit.assetInfo",
        "PhotoVoltaicUnit.assetInfo",
        "PowerElectronicsWindUnit.assetInfo",
        "FaultIndicator.assetInfo",
        "ProtectionRelaySystem.assetInfo",
        "EnergyConsumerPhase.assetInfo",
        "EnergySourcePhase.assetInfo",
        "PowerElectronicsConnectionPhase.assetInfo",
        "TapChangerControl.assetInfo",
        "RatioTapChanger.assetInfo",
        "BatteryControl.assetInfo",
        "Circuit.assetInfo",
        "Feeder.assetInfo",
        "LvFeeder.assetInfo",
        "LvSubstation.assetInfo",
        "Substation.assetInfo",
        "HvCustomer.assetInfo",
        "Site.assetInfo",
        "Clamp.assetInfo",
        "BusbarSection.assetInfo",

        // AcLineSegment
        "AcLineSegment.perLengthPhaseImpedance", // perLengthSequenceImpedance was set

    )
}
