/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network.tracing;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.cimbend.network.NetworkService;
import com.zepben.traversals.BasicQueue;
import com.zepben.traversals.BasicTracker;
import com.zepben.traversals.BasicTraversal;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A utility class intended to be a one stop shop to instantiate all your common network traces!
 */
@SuppressWarnings("WeakerAccess")
@EverythingIsNonnullByDefault
public class Tracing {

    public static <T> BasicTraversal<T> createBasicDepthTrace(BasicTraversal.QueueNext<T> queueNext) {
        return new BasicTraversal<>(queueNext, BasicQueue.depthFirst(), new BasicTracker<>());
    }

    public static <T> BasicTraversal<T> createBasicBreadthTrace(BasicTraversal.QueueNext<T> queueNext) {
        return new BasicTraversal<>(queueNext, BasicQueue.breadthFirst(), new BasicTracker<>());
    }

    /**
     * Creates a new traversal that traces equipment that are connected. This ignores phases, open status etc.
     * It is purely to trace equipment that are connected in any way.
     *
     * @return The new traversal instance.
     */
    public static BasicTraversal<ConductingEquipment> connectedEquipmentTrace() {
        return createBasicDepthTrace(Tracing::conductingEquipmentQueueNext);
    }

    /**
     * Creates a new traversal that traces equipment that are connected. This ignores phases, open status etc.
     * It is purely to trace equipment that are connected in any way.
     *
     * @return The new traversal instance.
     */
    public static BasicTraversal<ConductingEquipment> connectedEquipmentBreadthTrace() {
        return createBasicBreadthTrace(Tracing::conductingEquipmentQueueNext);
    }

    /**
     * Creates a new phase based trace ignoring the state of open phases
     *
     * @return The new traversal instance.
     */
    public static BasicTraversal<PhaseStep> phaseTrace() {
        return PhaseTrace.newTrace();
    }

    /**
     * Creates a new phase based trace stopping on normally open phases
     *
     * @return The new traversal instance.
     */
    public static BasicTraversal<PhaseStep> normalPhaseTrace() {
        return PhaseTrace.newNormalTrace();
    }

    /**
     * Creates a new phase based trace stopping at currently open phases
     *
     * @return The new traversal instance.
     */
    public static BasicTraversal<PhaseStep> currentPhaseTrace() {
        return PhaseTrace.newCurrentTrace();
    }

    /**
     * Creates a new downstream trace based on phases and the normal state of the network. Note that the phases
     * need to be set on the network before a concept of downstream is known.
     *
     * @return The new traversal instance.
     */
    public static BasicTraversal<PhaseStep> normalDownstreamTrace() {
        return PhaseTrace.newNormalDownstreamTrace();
    }

    /**
     * Creates a new downstream trace based on phases and the current state of the network. Note that the phases
     * need to be set on the network before a concept of downstream is known.
     *
     * @return The new traversal instance.
     */
    public static BasicTraversal<PhaseStep> currentDownstreamTrace() {
        return PhaseTrace.newCurrentDownstreamTrace();
    }

    /**
     * Creates a new upstream trace based on phases and the normal state of the network. Note that the phases
     * need to be set on the network before a concept of downstream is known.
     *
     * @return The new traversal instance.
     */
    public static BasicTraversal<PhaseStep> normalUpstreamTrace() {
        return PhaseTrace.newNormalUpstreamTrace();
    }

    /**
     * Creates a new upstream trace based on phases and the current state of the network. Note that the phases
     * need to be set on the network before a concept of downstream is known.
     *
     * @return The new traversal instance.
     */
    public static BasicTraversal<PhaseStep> currentUpstreamTrace() {
        return PhaseTrace.newCurrentUpstreamTrace();
    }

    /**
     * Returns an instance of {@link SetPhases} convenience class for setting phases on a network.
     *
     * @return A new traversal instance.
     */
    public static SetPhases setPhases() {
        return new SetPhases();
    }

    /**
     * Returns an instance of {@link PhaseInferrer} convenience class for inferring phases on a network.
     *
     * @return A new traversal instance.
     */
    public static PhaseInferrer phaseInferrer() {
        return new PhaseInferrer();
    }

    /**
     * Returns an instance of {@link RemovePhases} convenience class for removing phases from a network.
     *
     * @return A new traversal instance.
     */
    public static RemovePhases removePhases() {
        return new RemovePhases();
    }

    /**
     * Returns an instance of {@link AssignToFeeders} convenience class for assigning equipment containers to feeders on a network.
     *
     * @return A new traversal instance.
     */
    public static AssignToFeeders assignEquipmentContainersToFeeders() {
        return new AssignToFeeders();
    }

    /**
     * Returns an instance of {@link DownstreamTree} convenience class for tracing using the
     * normal state of a network
     * .
     *
     * @return A new traversal instance.
     */
    public static DownstreamTree normalDownstreamTree() {
        return new DownstreamTree(OpenTest.NORMALLY_OPEN, PhaseSelector.NORMAL_PHASES);
    }

    /**
     * Returns an instance of {@link DownstreamTree} convenience class for tracing using the
     * current state of a network
     *
     * @return A new traversal instance.
     */
    public static DownstreamTree currentDownstreamTree() {
        return new DownstreamTree(OpenTest.CURRENTLY_OPEN, PhaseSelector.CURRENT_PHASES);
    }

    /**
     * Returns an instance of {@link FindWithUsagePoints} convenience class for finding conducting equipment with attached usage points.
     *
     * @return A new traversal instance.
     */
    public static FindWithUsagePoints findWithUsagePoints() {
        return new FindWithUsagePoints();
    }

    private static void conductingEquipmentQueueNext(@Nullable ConductingEquipment conductingEquipment, BasicTraversal<ConductingEquipment> traversal) {
        if (conductingEquipment != null) {
            List<ConnectivityResult> connectivityResults = NetworkService.connectedEquipment(conductingEquipment);
            connectivityResults.forEach(cr -> {
                ConductingEquipment to = cr.getTo();
                if (to != null)
                    traversal.queue().add(to);
            });
        }
    }

    // Should not be able to instantiate this class.
    private Tracing() {
    }

}
