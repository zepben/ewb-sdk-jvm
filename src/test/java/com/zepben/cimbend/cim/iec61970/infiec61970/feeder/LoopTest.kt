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
package com.zepben.cimbend.cim.iec61970.infiec61970.feeder

import com.zepben.cimbend.cim.iec61970.base.core.Substation
import com.zepben.cimbend.utils.PrivateCollectionValidator
import com.zepben.test.util.junit.SystemLogExtension
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
