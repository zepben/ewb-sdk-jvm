/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.infiec61970.feeder

import com.zepben.evolve.cim.iec61970.base.core.Substation
import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class LoopTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun circuitAssociations() {
        PrivateCollectionValidator.validate(
            ::Loop,
            { id, _ -> Circuit(id) },
            Loop::numCircuits,
            Loop::getCircuit,
            Loop::circuits,
            Loop::addCircuit,
            Loop::removeCircuit,
            Loop::clearCircuits
        )
    }

    @Test
    internal fun substationAssociations() {
        PrivateCollectionValidator.validate(
            ::Loop,
            { id, _ -> Substation(id) },
            Loop::numSubstations,
            Loop::getSubstation,
            Loop::substations,
            Loop::addSubstation,
            Loop::removeSubstation,
            Loop::clearSubstations
        )
    }

    @Test
    internal fun energizingSubstationAssociations() {
        PrivateCollectionValidator.validate(
            ::Loop,
            { id, _ -> Substation(id) },
            Loop::numEnergizingSubstations,
            Loop::getEnergizingSubstation,
            Loop::energizingSubstations,
            Loop::addEnergizingSubstation,
            Loop::removeEnergizingSubstation,
            Loop::clearEnergizingSubstations
        )
    }
}
