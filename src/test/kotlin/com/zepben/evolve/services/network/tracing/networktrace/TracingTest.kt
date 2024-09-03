package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.Switch
import com.zepben.evolve.services.network.tracing.networktrace.conditions.terminalConnectivity
import com.zepben.evolve.services.network.tracing.traversals.BasicQueue
import org.junit.jupiter.api.Test

class TracingTest {

    @Test
    fun playground() {
        Tracing.connectedEquipmentTrace()
            .run(Terminal(), false)
    }

    @Test
    fun `tracing replacements`() {
        /**
         * Here are the functions from the original Tracing.kt to create tracing functions.
         * I am trying to prove if we can replace most of these with just the `NetworkTrace` class
         */

        // fun connectedEquipmentTrace(): ConnectedEquipmentTraversal = ConnectedEquipmentTrace.newConnectedEquipmentTrace()
        Tracing.connectedEquipmentTrace()

//        fun connectedEquipmentBreadthTrace(): ConnectedEquipmentTraversal = ConnectedEquipmentTrace.newConnectedEquipmentBreadthTrace()
        Tracing.connectedEquipmentTrace(queueFactory = { BasicQueue.breadthFirst() })

//        fun normalConnectedEquipmentTrace(): ConnectedEquipmentTraversal = ConnectedEquipmentTrace.newNormalConnectedEquipmentTrace()
        Tracing.connectedEquipmentTrace()
            .normallyUpstream()
            .stopAtNormallyOpen()

//        fun currentConnectedEquipmentTrace(): ConnectedEquipmentTraversal = ConnectedEquipmentTrace.newCurrentConnectedEquipmentTrace()
        Tracing.connectedEquipmentTrace()
            .stopAtCurrentlyOpen()

        /*
         TODO: Investigate more how the limited trace work. I don't think what I've done matches what is there,
               but I'm hoping we can do something like what I've done here
         */
//        fun normalLimitedConnectedEquipmentTrace(): LimitedConnectedEquipmentTrace = ConnectedEquipmentTrace.newNormalLimitedConnectedEquipmentTrace()
        Tracing.connectedEquipmentTrace()
            .stopAtNormallyOpen()
            .limitEquipmentSteps(10)


//        fun currentLimitedConnectedEquipmentTrace(): LimitedConnectedEquipmentTrace = ConnectedEquipmentTrace.newCurrentLimitedConnectedEquipmentTrace()
        Tracing.connectedEquipmentTrace()
            .stopAtCurrentlyOpen()
            .limitEquipmentSteps(10, Switch::class.java) // If you want to limit to 10 switches

//        fun normalDownstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = BasicQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
//            ConnectedEquipmentTrace.newNormalDownstreamEquipmentTrace(queue)
        Tracing.connectedEquipmentTrace()
            .normallyDownstream()

//        fun currentDownstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = BasicQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
//            ConnectedEquipmentTrace.newCurrentDownstreamEquipmentTrace(queue)
        Tracing.connectedEquipmentTrace()
            .currentlyDownstream()

//        fun normalUpstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = BasicQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
//            ConnectedEquipmentTrace.newNormalUpstreamEquipmentTrace(queue)
        Tracing.connectedEquipmentTrace()
            .normallyUpstream()

//        fun currentUpstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = BasicQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
//            ConnectedEquipmentTrace.newCurrentUpstreamEquipmentTrace(queue)
        Tracing.connectedEquipmentTrace()
            .currentlyUpstream()

        // NOTE: The new phase tracing doesn't map 1 to 1 to what was previously there. When we reviewed the
        //       difference between 'connectivity trace' and 'phase trace' and their use cases we felt they
        //       were not needed and just made things confusing.
        //       We are hoping the new phase tracing covers existing use cases as it is easier to understand.

//        fun connectivityTrace(): BasicTraversal<ConnectivityResult> = ConnectivityTrace.newConnectivityTrace()
//        fun phaseTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newTrace()
        Tracing.connectedEquipmentTrace()
            .withPhases(PhaseCode.ABCN)
            .addStepAction { step, ctx ->
                // This is the ConnectivityResult you used to get as the step item in the ConnectivityTrace
                val connectivityResult = ctx.terminalConnectivity()
            }

//        fun connectivityBreadthTrace(): BasicTraversal<ConnectivityResult> = ConnectivityTrace.newConnectivityBreadthTrace()
        Tracing.connectedEquipmentTrace({ BasicQueue.breadthFirst() })
            .withPhases(PhaseCode.ABCN)

//        fun normalConnectivityTrace(): BasicTraversal<ConnectivityResult> = ConnectivityTrace.newNormalConnectivityTrace()
//        fun normalPhaseTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newNormalTrace()
        Tracing.connectedEquipmentTrace()
            .stopAtNormallyOpen()
            .withPhases(PhaseCode.ABCN)

//        fun currentConnectivityTrace(): BasicTraversal<ConnectivityResult> = ConnectivityTrace.newCurrentConnectivityTrace()
//        fun currentPhaseTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newCurrentTrace()
        Tracing.connectedEquipmentTrace()
            .stopAtCurrentlyOpen()
            .withPhases(PhaseCode.ABCN)

//        fun normalDownstreamTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newNormalDownstreamTrace()
        Tracing.connectedEquipmentTrace()
            .withPhases(PhaseCode.ABCN)
            .normallyDownstream()

//        fun currentDownstreamTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newCurrentDownstreamTrace()
        Tracing.connectedEquipmentTrace()
            .withPhases(PhaseCode.ABCN)
            .currentlyDownstream()

//        fun normalUpstreamTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newNormalUpstreamTrace()
        Tracing.connectedEquipmentTrace()
            .withPhases(PhaseCode.ABCN)
            .normallyUpstream()

//        fun currentUpstreamTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newCurrentUpstreamTrace()
        Tracing.connectedEquipmentTrace()
            .withPhases(PhaseCode.ABCN)
            .currentlyUpstream()

        /**
         * Need to look at the rest of these.
         * Have not yet looked at BranchRecursiveTraversal which will be needed for some of these.
         */
//        fun setPhases(): SetPhases = SetPhases()

//        fun setDirection(): SetDirection = SetDirection()

//        fun phaseInferrer(): PhaseInferrer = PhaseInferrer()

//        fun removePhases(): RemovePhases = RemovePhases()

//        fun removeDirection(): RemoveDirection = RemoveDirection()

//        fun assignEquipmentToFeeders(): AssignToFeeders = AssignToFeeders()

//        fun assignEquipmentToLvFeeders(): AssignToLvFeeders = AssignToLvFeeders()

//        fun normalDownstreamTree(): DownstreamTree = DownstreamTree(OpenTest.NORMALLY_OPEN, DirectionSelector.NORMAL_DIRECTION)

//        fun currentDownstreamTree(): DownstreamTree = DownstreamTree(OpenTest.CURRENTLY_OPEN, DirectionSelector.CURRENT_DIRECTION)

//        fun findWithUsagePoints(): FindWithUsagePoints = FindWithUsagePoints()
    }
}
