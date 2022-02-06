/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.SingleLineSinglePhaseNetwork
import com.zepben.evolve.services.network.testdata.SingleLineSwerNetwork
import com.zepben.evolve.services.network.testdata.SinglePhaseJunctionNetwork
import com.zepben.evolve.services.network.testdata.UngangedSwitchLongNetwork
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.services.network.tracing.phases.FeederDirection.*
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class RemovePhasesTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun removesAllCoreByDefault() {
        val n = UngangedSwitchLongNetwork.create()

        Tracing.setPhases().run(n)
        PhaseLogger.trace(n["node0"])
        validateDirectionsAll(n, DOWNSTREAM, BOTH, UPSTREAM, BOTH)

        Tracing.removePhases().run(n.get<ConductingEquipment>("node1")!!)
        PhaseLogger.trace(n["node0"])
        validateDirectionsAll(n, NONE, DOWNSTREAM, NONE, UPSTREAM)

        Tracing.setPhases().run(n.get<ConductingEquipment>("node1")!!, n.listOf())
        PhaseLogger.trace(n["node0"])
        validateDirectionsAll(n, DOWNSTREAM, BOTH, UPSTREAM, BOTH)
    }

    @Test
    internal fun canRemoveOnlySelectedCores() {
        val n = UngangedSwitchLongNetwork.create()

        Tracing.setPhases().run(n)
        PhaseLogger.trace(n["node0"])
        validateDirectionsSelected(n, DOWNSTREAM, BOTH, UPSTREAM, BOTH)

        Tracing.removePhases().run(n.get<ConductingEquipment>("node1")!!, PhaseCode.AB.singlePhases.toSet())
        PhaseLogger.trace(n["node0"])
        validateDirectionsSelected(n, NONE, DOWNSTREAM, NONE, UPSTREAM)
    }

    @Test
    internal fun removePhasesFromSwer() {
        val n = SingleLineSwerNetwork.create()

        Tracing.setPhases().run(n)
        PhaseLogger.trace(n["source"])
        validateDirection(n, "source", 1, listOf(DOWNSTREAM, DOWNSTREAM, DOWNSTREAM))
        validateDirection(n, "line1", 1, listOf(UPSTREAM, UPSTREAM, UPSTREAM))
        validateDirection(n, "line1", 2, listOf(DOWNSTREAM, DOWNSTREAM, DOWNSTREAM))
        validateDirection(n, "isoTx", 1, listOf(UPSTREAM, UPSTREAM, UPSTREAM))
        validateDirection(n, "isoTx", 2, listOf(DOWNSTREAM, DOWNSTREAM, DOWNSTREAM))
        validateDirection(n, "line2", 1, listOf(UPSTREAM))
        validateDirection(n, "line2", 2, listOf(DOWNSTREAM))

        Tracing.removePhases().run(n.get<ConductingEquipment>("line1")!!)
        PhaseLogger.trace(n["source"])
        validateDirection(n, "source", 1, listOf(DOWNSTREAM, DOWNSTREAM, DOWNSTREAM))
        validateDirection(n, "line1", 1, listOf(UPSTREAM, UPSTREAM, UPSTREAM))
        validateDirection(n, "line1", 2, listOf(NONE, NONE, NONE))
        validateDirection(n, "isoTx", 1, listOf(NONE, NONE, NONE))
        validateDirection(n, "isoTx", 2, listOf(NONE, NONE, NONE))
        validateDirection(n, "line2", 1, listOf(NONE))
        validateDirection(n, "line2", 2, listOf(NONE))
    }

    @Test
    internal fun removePhasesFromSinglePhase() {
        val n = SingleLineSinglePhaseNetwork.create()

        Tracing.setPhases().run(n)
        PhaseLogger.trace(n["source"])
        validateDirection(n, "source", 1, listOf(DOWNSTREAM, DOWNSTREAM, DOWNSTREAM))
        validateDirection(n, "line1", 1, listOf(UPSTREAM, UPSTREAM, UPSTREAM))
        validateDirection(n, "line1", 2, listOf(DOWNSTREAM, DOWNSTREAM, DOWNSTREAM))
        validateDirection(n, "line2", 1, listOf(UPSTREAM, UPSTREAM))
        validateDirection(n, "line2", 2, listOf(DOWNSTREAM, DOWNSTREAM))

        Tracing.removePhases().run(n.get<ConductingEquipment>("line1")!!)
        PhaseLogger.trace(n["source"])
        validateDirection(n, "source", 1, listOf(DOWNSTREAM, DOWNSTREAM, DOWNSTREAM))
        validateDirection(n, "line1", 1, listOf(UPSTREAM, UPSTREAM, UPSTREAM))
        validateDirection(n, "line1", 2, listOf(NONE, NONE, NONE))
        validateDirection(n, "line2", 1, listOf(NONE, NONE))
        validateDirection(n, "line2", 2, listOf(NONE, NONE))
    }

    @Test
    internal fun respectsMultiFeeds() {
        var n = SinglePhaseJunctionNetwork.create()
        PhaseLogger.trace(n["n1"])

        val both = listOf(BOTH)
        val up = listOf(UPSTREAM)
        val down = listOf(DOWNSTREAM)
        val none = listOf(NONE)
        val upAndUp = listOf(UPSTREAM, UPSTREAM)
        val downAndDown = listOf(DOWNSTREAM, DOWNSTREAM)
        val bothAndUp = listOf(BOTH, UPSTREAM)
        val bothAndDown = listOf(BOTH, DOWNSTREAM)
        val upAndNone = listOf(UPSTREAM, NONE)
        val downAndNone = listOf(DOWNSTREAM, NONE)
        val noneAndNone = listOf(NONE, NONE)

        Tracing.removePhases().run(n.get<ConductingEquipment>("n1")!!, PhaseCode.AB.singlePhases.toSet())
        PhaseLogger.trace(n["n1"])

        validateDirection(n, "n1", 1, upAndUp)
        validateDirection(n, "n2", 1, bothAndDown)
        validateDirection(n, "n3", 1, both)
        validateDirection(n, "n4", 1, upAndUp)
        validateDirection(n, "n4", 2, downAndDown)
        validateDirection(n, "c1", 1, downAndDown)
        validateDirection(n, "c1", 2, upAndUp)
        validateDirection(n, "c2", 1, bothAndDown)
        validateDirection(n, "c2", 2, bothAndUp)
        validateDirection(n, "c3", 1, both)
        validateDirection(n, "c3", 2, both)
        validateDirection(n, "c4", 1, upAndUp)
        validateDirection(n, "c4", 2, downAndDown)

        Tracing.removePhases().run(n.get<ConductingEquipment>("n2")!!, PhaseCode.AB.singlePhases.toSet())
        PhaseLogger.trace(n["n1"])

        validateDirection(n, "n1", 1, upAndNone)
        validateDirection(n, "n2", 1, upAndNone)
        validateDirection(n, "n3", 1, down)
        validateDirection(n, "n4", 1, upAndNone)
        validateDirection(n, "n4", 2, downAndNone)
        validateDirection(n, "c1", 1, downAndNone)
        validateDirection(n, "c1", 2, upAndNone)
        validateDirection(n, "c2", 1, upAndNone)
        validateDirection(n, "c2", 2, downAndNone)
        validateDirection(n, "c3", 1, up)
        validateDirection(n, "c3", 2, down)
        validateDirection(n, "c4", 1, upAndNone)
        validateDirection(n, "c4", 2, downAndNone)

        Tracing.removePhases().run(n.get<ConductingEquipment>("n3")!!, PhaseCode.A.singlePhases.toSet())
        PhaseLogger.trace(n["n1"])

        validateDirection(n, "n1", 1, noneAndNone)
        validateDirection(n, "n2", 1, noneAndNone)
        validateDirection(n, "n3", 1, none)
        validateDirection(n, "n4", 1, noneAndNone)
        validateDirection(n, "n4", 2, noneAndNone)
        validateDirection(n, "c1", 1, noneAndNone)
        validateDirection(n, "c1", 2, noneAndNone)
        validateDirection(n, "c2", 1, noneAndNone)
        validateDirection(n, "c2", 2, noneAndNone)
        validateDirection(n, "c3", 1, none)
        validateDirection(n, "c3", 2, none)
        validateDirection(n, "c4", 1, noneAndNone)
        validateDirection(n, "c4", 2, noneAndNone)

        n = SinglePhaseJunctionNetwork.create()

        Tracing.removePhases().run(n.get<ConductingEquipment>("c1")!!, PhaseCode.AB.singlePhases.toSet())
        PhaseLogger.trace(n["n1"])

        validateDirection(n, "n1", 1, downAndDown)
        validateDirection(n, "n2", 1, bothAndDown)
        validateDirection(n, "n3", 1, both)
        validateDirection(n, "n4", 1, upAndUp)
        validateDirection(n, "n4", 2, downAndDown)
        validateDirection(n, "c1", 1, upAndUp)
        validateDirection(n, "c1", 2, upAndUp)
        validateDirection(n, "c2", 1, bothAndDown)
        validateDirection(n, "c2", 2, bothAndUp)
        validateDirection(n, "c3", 1, both)
        validateDirection(n, "c3", 2, both)
        validateDirection(n, "c4", 1, upAndUp)
        validateDirection(n, "c4", 2, downAndDown)

        Tracing.removePhases().run(n.get<ConductingEquipment>("c2")!!, PhaseCode.AB.singlePhases.toSet())
        PhaseLogger.trace(n["n1"])

        validateDirection(n, "n1", 1, downAndDown)
        validateDirection(n, "n2", 1, downAndDown)
        validateDirection(n, "n3", 1, down)
        validateDirection(n, "n4", 1, upAndNone)
        validateDirection(n, "n4", 2, downAndNone)
        validateDirection(n, "c1", 1, upAndUp)
        validateDirection(n, "c1", 2, upAndNone)
        validateDirection(n, "c2", 1, upAndNone)
        validateDirection(n, "c2", 2, upAndUp)
        validateDirection(n, "c3", 1, up)
        validateDirection(n, "c3", 2, down)
        validateDirection(n, "c4", 1, upAndNone)
        validateDirection(n, "c4", 2, downAndNone)

        Tracing.removePhases().run(n.get<ConductingEquipment>("c3")!!, PhaseCode.A.singlePhases.toSet())
        PhaseLogger.trace(n["n1"])

        validateDirection(n, "n1", 1, downAndDown)
        validateDirection(n, "n2", 1, downAndDown)
        validateDirection(n, "n3", 1, down)
        validateDirection(n, "n4", 1, noneAndNone)
        validateDirection(n, "n4", 2, noneAndNone)
        validateDirection(n, "c1", 1, upAndUp)
        validateDirection(n, "c1", 2, noneAndNone)
        validateDirection(n, "c2", 1, noneAndNone)
        validateDirection(n, "c2", 2, upAndUp)
        validateDirection(n, "c3", 1, up)
        validateDirection(n, "c3", 2, none)
        validateDirection(n, "c4", 1, noneAndNone)
        validateDirection(n, "c4", 2, noneAndNone)
    }

    private fun validateDirectionsAll(network: NetworkService, d11: FeederDirection, d12: FeederDirection, d21: FeederDirection, d22: FeederDirection) {
        val d11D22D11D22 = listOf(d11, d22, d11, d22)
        val d21D12D21D12 = listOf(d21, d12, d21, d12)
        val downD12DownD12 = listOf(DOWNSTREAM, d12, DOWNSTREAM, d12)
        val upD22UpD22 = listOf(UPSTREAM, d22, UPSTREAM, d22)

        validateDirection(network, "node0", 1, downD12DownD12)
        validateDirection(network, "acLineSegment0", 1, upD22UpD22)
        validateDirection(network, "acLineSegment0", 2, downD12DownD12)
        validateDirection(network, "node1", 1, upD22UpD22)
        validateDirection(network, "node1", 2, d11D22D11D22)
        validateDirection(network, "acLineSegment1", 1, d21D12D21D12)
        validateDirection(network, "acLineSegment1", 2, d11D22D11D22)
        validateDirection(network, "node2", 1, d21D12D21D12)
        validateDirection(network, "node2", 2, upD22UpD22)
        validateDirection(network, "acLineSegment2", 1, downD12DownD12)
        validateDirection(network, "acLineSegment2", 2, upD22UpD22)
        validateDirection(network, "node3", 1, downD12DownD12)
        validateDirection(network, "node3", 2, upD22UpD22)
        validateDirection(network, "acLineSegment3", 1, downD12DownD12)
        validateDirection(network, "acLineSegment3", 2, upD22UpD22)
        validateDirection(network, "node4", 1, downD12DownD12)
    }

    private fun validateDirectionsSelected(network: NetworkService, d11: FeederDirection, d12: FeederDirection, d21: FeederDirection, d22: FeederDirection) {
        val d11d22DownBoth = listOf(d11, d22, DOWNSTREAM, BOTH)
        val d21d12UpBoth = listOf(d21, d12, UPSTREAM, BOTH)
        val downD12DownBoth = listOf(DOWNSTREAM, d12, DOWNSTREAM, BOTH)
        val upD22UpD22 = listOf(UPSTREAM, d22, UPSTREAM, BOTH)

        validateDirection(network, "node0", 1, downD12DownBoth)
        validateDirection(network, "acLineSegment0", 1, upD22UpD22)
        validateDirection(network, "acLineSegment0", 2, downD12DownBoth)
        validateDirection(network, "node1", 1, upD22UpD22)
        validateDirection(network, "node1", 2, d11d22DownBoth)
        validateDirection(network, "acLineSegment1", 1, d21d12UpBoth)
        validateDirection(network, "acLineSegment1", 2, d11d22DownBoth)
        validateDirection(network, "node2", 1, d21d12UpBoth)
        validateDirection(network, "node2", 2, upD22UpD22)
        validateDirection(network, "acLineSegment2", 1, downD12DownBoth)
        validateDirection(network, "acLineSegment2", 2, upD22UpD22)
        validateDirection(network, "node3", 1, downD12DownBoth)
        validateDirection(network, "node3", 2, upD22UpD22)
        validateDirection(network, "acLineSegment3", 1, downD12DownBoth)
        validateDirection(network, "acLineSegment3", 2, upD22UpD22)
        validateDirection(network, "node4", 1, downD12DownBoth)
    }

    private fun validateDirection(network: NetworkService, id: String, terminalNo: Int, expectedDirections: List<FeederDirection>) {
        val terminal: Terminal = network.get<ConductingEquipment>(id)!!.getTerminal(terminalNo)!!
        for (index in expectedDirections.indices) MatcherAssert.assertThat(
            terminal.normalPhases(terminal.phases.singlePhases[index]).direction,
            Matchers.equalTo(expectedDirections[index])
        )
    }

}
