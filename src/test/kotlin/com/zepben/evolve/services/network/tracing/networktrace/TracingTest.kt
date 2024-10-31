package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.Switch
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.downstream
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.limitEquipmentSteps
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.upstream
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.TraversalQueue
import org.junit.jupiter.api.Test

class TracingTest {

    @Test
    fun playground() {
        Tracing.connectedEquipmentTrace()
            .run(Terminal(), canStopOnStartItem = false)
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
        Tracing.connectedEquipmentTrace(queue = TraversalQueue.breadthFirst())

//        fun normalConnectedEquipmentTrace(): ConnectedEquipmentTraversal = ConnectedEquipmentTrace.newNormalConnectedEquipmentTrace()
        Tracing.connectedEquipmentTrace()
            .addNetworkCondition { stopAtOpen() }

//        fun currentConnectedEquipmentTrace(): ConnectedEquipmentTraversal = ConnectedEquipmentTrace.newCurrentConnectedEquipmentTrace()
        Tracing.connectedEquipmentTrace(networkStateOperators = NetworkStateOperators.CURRENT)
            .addNetworkCondition { stopAtOpen() }

        /*
         TODO: Investigate more how the limited trace work. I don't think what I've done matches what is there,
               but I'm hoping we can do something like what I've done here
         */
//        fun normalLimitedConnectedEquipmentTrace(): LimitedConnectedEquipmentTrace = ConnectedEquipmentTrace.newNormalLimitedConnectedEquipmentTrace()
        Tracing.connectedEquipmentTrace()
            .addNetworkCondition { stopAtOpen() }
            .addCondition(limitEquipmentSteps(10))


//        fun currentLimitedConnectedEquipmentTrace(): LimitedConnectedEquipmentTrace = ConnectedEquipmentTrace.newCurrentLimitedConnectedEquipmentTrace()
        Tracing.connectedEquipmentTrace(networkStateOperators = NetworkStateOperators.CURRENT)
            .addNetworkCondition { stopAtOpen() }
            .addCondition(limitEquipmentSteps(10, Switch::class)) // If you want to limit to 10 switches

//        fun normalDownstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = TraversalQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
//            ConnectedEquipmentTrace.newNormalDownstreamEquipmentTrace(queue)
        Tracing.connectedEquipmentTrace()
            .addNetworkCondition { downstream() }

//        fun currentDownstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = TraversalQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
//            ConnectedEquipmentTrace.newCurrentDownstreamEquipmentTrace(queue)
        Tracing.connectedEquipmentTrace(networkStateOperators = NetworkStateOperators.CURRENT)
            .addNetworkCondition { downstream() }

//        fun normalUpstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = TraversalQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
//            ConnectedEquipmentTrace.newNormalUpstreamEquipmentTrace(queue)
        Tracing.connectedEquipmentTrace()
            .addNetworkCondition { upstream() }

//        fun currentUpstreamEquipmentTrace(queue: TraversalQueue<ConductingEquipment> = TraversalQueue.depthFirst()): BasicTraversal<ConductingEquipment> =
//            ConnectedEquipmentTrace.newCurrentUpstreamEquipmentTrace(queue)
        Tracing.connectedEquipmentTrace(networkStateOperators = NetworkStateOperators.CURRENT)
            .addNetworkCondition { upstream() }

        // NOTE: The new phase tracing doesn't map 1 to 1 to what was previously there. When we reviewed the
        //       difference between 'connectivity trace' and 'phase trace' and their use cases we felt they
        //       were not needed and just made things confusing.
        //       We are hoping the new phase tracing covers existing use cases as it is easier to understand.

//        fun connectivityTrace(): BasicTraversal<ConnectivityResult> = ConnectivityTrace.newConnectivityTrace()
//        fun phaseTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newTrace()
        Tracing.connectedEquipmentTrace()
//            .addCondition(withPhases(PhaseCode.ABCN))
            .addStepAction { step, _ ->
                val phasePaths = step.path.nominalPhasePaths
            }
            .run(Terminal(), PhaseCode.ABC)

//        fun connectivityBreadthTrace(): BasicTraversal<ConnectivityResult> = ConnectivityTrace.newConnectivityBreadthTrace()
        Tracing.connectedEquipmentTrace(queue = TraversalQueue.breadthFirst())
            .run(Terminal(), PhaseCode.ABC)

//        fun normalConnectivityTrace(): BasicTraversal<ConnectivityResult> = ConnectivityTrace.newNormalConnectivityTrace()
//        fun normalPhaseTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newNormalTrace()
        Tracing.connectedEquipmentTrace()
            .addNetworkCondition { stopAtOpen() }
            .run(Terminal(), PhaseCode.ABC)

//        fun currentConnectivityTrace(): BasicTraversal<ConnectivityResult> = ConnectivityTrace.newCurrentConnectivityTrace()
//        fun currentPhaseTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newCurrentTrace()
        Tracing.connectedEquipmentTrace(networkStateOperators = NetworkStateOperators.CURRENT)
            .addNetworkCondition { stopAtOpen() }
            .run(Terminal(), PhaseCode.ABC)

//        fun normalDownstreamTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newNormalDownstreamTrace()
        Tracing.connectedEquipmentTrace()
            .addNetworkCondition { downstream() }
            .run(Terminal(), PhaseCode.ABC)

//        fun currentDownstreamTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newCurrentDownstreamTrace()
        Tracing.connectedEquipmentTrace(networkStateOperators = NetworkStateOperators.CURRENT)
            .addNetworkCondition { downstream() }
            .run(Terminal(), PhaseCode.ABC)

//        fun normalUpstreamTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newNormalUpstreamTrace()
        Tracing.connectedEquipmentTrace()
            .addNetworkCondition { upstream() }
            .run(Terminal(), PhaseCode.ABC)

//        fun currentUpstreamTrace(): BasicTraversal<PhaseStep> = PhaseTrace.newCurrentUpstreamTrace()
        Tracing.connectedEquipmentTrace(networkStateOperators = NetworkStateOperators.CURRENT)
            .addNetworkCondition { upstream() }
            .run(Terminal(), PhaseCode.ABC)

//        fun setDirection(): SetDirection = SetDirection()
        Tracing.normalSetDirection()
        Tracing.currentSetDirection()

        // fun normalDownstreamTree(): DownstreamTree = DownstreamTree(OpenTest.NORMALLY_OPEN, DirectionSelector.NORMAL_DIRECTION)
        Tracing.normalDownstreamTree()

        // fun currentDownstreamTree(): DownstreamTree = DownstreamTree(OpenTest.CURRENTLY_OPEN, DirectionSelector.CURRENT_DIRECTION)
        Tracing.currentDownstreamTree()

//        fun removeDirection(): RemoveDirection = RemoveDirection()
        Tracing.normalRemoveDirection()
        Tracing.currentRemoveDirection()

//        fun assignEquipmentToFeeders(): AssignToFeeders = AssignToFeeders()
        Tracing.normalAssignEquipmentToFeeders()
        Tracing.currentAssignEquipmentToFeeders()

//        fun assignEquipmentToLvFeeders(): AssignToLvFeeders = AssignToLvFeeders()
        Tracing.normalAssignEquipmentToLvFeeders()
        Tracing.currentAssignEquipmentToLvFeeders()

//        fun setPhases(): SetPhases = SetPhases()
        Tracing.normalSetPhases()
        Tracing.currentSetPhases()

//        fun phaseInferrer(): PhaseInferrer = PhaseInferrer()
        Tracing.normalPhaseInferrer()
        Tracing.currentPhaseInferrer()

//        fun removePhases(): RemovePhases = RemovePhases()
        Tracing.normalRemovePhases()
        Tracing.currentRemovePhases()

        /**
         * TODO: Where do we want to define these now?
         * Need to look at the rest of these.
         */

//        fun findWithUsagePoints(): FindWithUsagePoints = FindWithUsagePoints()

    }
}
