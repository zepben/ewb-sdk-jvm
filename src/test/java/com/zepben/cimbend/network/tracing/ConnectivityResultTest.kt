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
package com.zepben.cimbend.network.tracing

import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment
import com.zepben.cimbend.cim.iec61970.base.core.PhaseCode
import com.zepben.cimbend.cim.iec61970.base.core.Terminal
import com.zepben.cimbend.cim.iec61970.base.wires.Junction
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.cimbend.network.model.NominalPhasePath
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ConnectivityResultTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val asset1: ConductingEquipment = Junction("asset1").apply {
        name = "asset 1"
        addTerminal(Terminal().also { it.conductingEquipment = this; it.phases = PhaseCode.A })
        addTerminal(Terminal().also { it.conductingEquipment = this; it.phases = PhaseCode.A })
    }

    private val asset2: ConductingEquipment = Junction("asset2").apply {
        name = "asset 2"
        addTerminal(Terminal().also { it.conductingEquipment = this; it.phases = PhaseCode.A })
        addTerminal(Terminal().also { it.conductingEquipment = this; it.phases = PhaseCode.A })
    }

    private val terminal11 = asset1.getTerminal(1)!!
    private val terminal12 = asset1.getTerminal(2)!!
    private val terminal21 = asset2.getTerminal(1)!!

    @Test
    internal fun accessors() {
        val expectedPhaseMap =
            setOf(NominalPhasePath.between(SinglePhaseKind.A, SinglePhaseKind.A), NominalPhasePath.between(SinglePhaseKind.B, SinglePhaseKind.X))
        val cr = ConnectivityResult.between(terminal11, terminal21).addNominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.A)
            .addNominalPhasePath(SinglePhaseKind.B, SinglePhaseKind.X)

        assertThat(cr.from(), equalTo(asset1))
        assertThat(cr.fromTerminal(), equalTo(terminal11))
        assertThat(cr.to(), equalTo(asset2))
        assertThat(cr.toTerminal(), equalTo(terminal21))
        assertThat(cr.fromNominalPhases(), containsInAnyOrder(SinglePhaseKind.A, SinglePhaseKind.B))
        assertThat(cr.toNominalPhases(), containsInAnyOrder(SinglePhaseKind.A, SinglePhaseKind.X))
        assertThat(cr.nominalPhasePaths(), containsInAnyOrder(*expectedPhaseMap.toTypedArray()))
    }

    @Test
    internal fun coverage() {
        val cr1 = ConnectivityResult.between(terminal11, terminal21).addNominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.A)
        val cr1Dup = ConnectivityResult.between(terminal11, terminal21).addNominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.A)
        val cr2 = ConnectivityResult.between(terminal11, terminal21).addNominalPhasePath(SinglePhaseKind.B, SinglePhaseKind.B)
        val cr3 = ConnectivityResult.between(terminal11, terminal12).addNominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.A)
        val cr4 = ConnectivityResult.between(terminal21, terminal11).addNominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.A)

        assertThat(cr1, equalTo(cr1))
        assertThat(cr1, equalTo(cr1Dup))
        assertThat(cr1, equalTo(ConnectivityResult.between(terminal11, terminal21).addNominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.A)))
        assertThat(cr1, not(equalTo<ConnectivityResult?>(null)))
        assertThat(cr1, not(equalTo(cr2)))
        assertThat(cr1, not(equalTo(cr3)))
        assertThat(cr1, not(equalTo(cr4)))
        assertThat(cr1.hashCode(), equalTo(cr1Dup.hashCode()))
        assertThat(cr1.toString(), not(emptyString()))
    }
}
