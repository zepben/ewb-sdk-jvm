/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.evolve.cim.iec61970.base.core.Terminal;
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind;
import com.zepben.evolve.services.network.NetworkService;
import com.zepben.evolve.services.network.model.PhaseDirection;
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal;
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class that creates commonly used phase based traces. You can add custom step actions and stop conditions
 * to the returned traversal.
 */
@SuppressWarnings("WeakerAccess")
@EverythingIsNonnullByDefault
class PhaseTrace {

    /**
     * @return a traversal that traces along phases in all directions through all open points.
     */
    public static BasicTraversal<PhaseStep> newTrace() {
        return newTrace(OpenTest.IGNORE_OPEN);
    }

    /**
     * @return a traversal that traces along phases in all directions stopping at normally open points.
     */
    public static BasicTraversal<PhaseStep> newNormalTrace() {
        return newTrace(OpenTest.NORMALLY_OPEN);
    }

    /**
     * @return a traversal that traces along phases in all directions stopping at currently open points.
     */
    public static BasicTraversal<PhaseStep> newCurrentTrace() {
        return newTrace(OpenTest.CURRENTLY_OPEN);
    }

    /**
     * @return a traversal that traces along phases in a downstream direction stopping at normally open points.
     */
    public static BasicTraversal<PhaseStep> newNormalDownstreamTrace() {
        return newDownstreamTrace(OpenTest.NORMALLY_OPEN, PhaseSelector.NORMAL_PHASES);
    }

    /**
     * @return a traversal that traces along phases in a downstream direction stopping at currently open points.
     */
    public static BasicTraversal<PhaseStep> newCurrentDownstreamTrace() {
        return newDownstreamTrace(OpenTest.CURRENTLY_OPEN, PhaseSelector.CURRENT_PHASES);
    }

    /**
     * @return a traversal that traces along phases in a upstream direction stopping at normally open points.
     */
    public static BasicTraversal<PhaseStep> newNormalUpstreamTrace() {
        return newUpstreamTrace(OpenTest.NORMALLY_OPEN, PhaseSelector.NORMAL_PHASES);
    }

    /**
     * @return a traversal that traces along phases in a upstream direction stopping at currently open points.
     */
    public static BasicTraversal<PhaseStep> newCurrentUpstreamTrace() {
        return newUpstreamTrace(OpenTest.CURRENTLY_OPEN, PhaseSelector.CURRENT_PHASES);
    }

    private static BasicTraversal<PhaseStep> newTrace(OpenTest isOpenTest) {
        return new BasicTraversal<>(queueNext(isOpenTest), WeightedPriorityQueue.processQueue(ps -> ps.phases().size()), new PhaseStepTracker());
    }

    private static BasicTraversal<PhaseStep> newDownstreamTrace(OpenTest isOpenTest, PhaseSelector activePhases) {
        return new BasicTraversal<>(queueNextDownstream(isOpenTest, activePhases),
            WeightedPriorityQueue.processQueue(ps -> ps.phases().size()),
            new PhaseStepTracker());
    }

    private static BasicTraversal<PhaseStep> newUpstreamTrace(OpenTest isOpenTest, PhaseSelector activePhases) {
        return new BasicTraversal<>(queueNextUpstream(isOpenTest, activePhases),
            WeightedPriorityQueue.processQueue(ps -> ps.phases().size()),
            new PhaseStepTracker());
    }

    private static BasicTraversal.QueueNext<PhaseStep> queueNext(final OpenTest openTest) {
        return (phaseStep, traversal) -> {
            if (phaseStep == null)
                return;

            Set<SinglePhaseKind> outPhases = new HashSet<>();
            phaseStep.conductingEquipment().getTerminals().forEach(terminal -> {
                outPhases.clear();
                for (SinglePhaseKind phase : phaseStep.phases()) {
                    if (!openTest.isOpen(phaseStep.conductingEquipment(), phase)) {
                        outPhases.add(phase);
                    }
                }

                queueConnected(traversal, terminal, outPhases);
            });
        };
    }

    private static BasicTraversal.QueueNext<PhaseStep> queueNextDownstream(final OpenTest openTest,
                                                                           PhaseSelector activePhases) {
        return (phaseStep, traversal) -> {
            if (phaseStep == null)
                return;

            Set<SinglePhaseKind> outPhases = new HashSet<>();
            phaseStep.conductingEquipment().getTerminals().forEach(terminal -> {
                outPhases.clear();
                getPhasesWithDirection(openTest, activePhases, terminal, phaseStep.phases(), PhaseDirection.OUT, outPhases);

                queueConnected(traversal, terminal, outPhases);
            });
        };
    }

    private static void queueConnected(BasicTraversal<PhaseStep> traversal, Terminal terminal, Set<SinglePhaseKind> outPhases) {
        if (!outPhases.isEmpty()) {
            NetworkService.connectedTerminals(terminal, outPhases).forEach(
                cr -> tryQueue(traversal, cr, cr.getToNominalPhases())
            );
        }
    }

    private static BasicTraversal.QueueNext<PhaseStep> queueNextUpstream(final OpenTest openTest,
                                                                         PhaseSelector activePhases) {
        return (phaseStep, traversal) -> {
            if (phaseStep == null)
                return;

            Set<SinglePhaseKind> inPhases = new HashSet<>();
            phaseStep.conductingEquipment().getTerminals().forEach(terminal -> {
                inPhases.clear();
                getPhasesWithDirection(openTest, activePhases, terminal, phaseStep.phases(), PhaseDirection.IN, inPhases);

                if (!inPhases.isEmpty()) {
                    List<ConnectivityResult> inTerminals = NetworkService.connectedTerminals(terminal, inPhases);
                    inTerminals.forEach(cr -> {
                        // When going upstream, we only want to traverse to connected terminals that have an out direction
                        Set<SinglePhaseKind> outPhases = cr.getToNominalPhases()
                            .stream()
                            .filter(phase -> activePhases.status(cr.getToTerminal(), phase).direction().has(PhaseDirection.OUT))
                            .collect(Collectors.toSet());

                        if (!outPhases.isEmpty()) {
                            tryQueue(traversal, cr, outPhases);
                        }
                    });
                }
            });
        };
    }

    private static void tryQueue(BasicTraversal<PhaseStep> traversal, ConnectivityResult cr, Collection<SinglePhaseKind> outPhases) {
        ConductingEquipment to = cr.getTo();
        if (to != null)
            traversal.queue().add(PhaseStep.continueAt(to, outPhases, cr.getFrom()));
    }

    private static void getPhasesWithDirection(OpenTest openTest,
                                               PhaseSelector activePhases,
                                               Terminal terminal,
                                               Set<SinglePhaseKind> candidatePhases,
                                               PhaseDirection direction,
                                               Set<SinglePhaseKind> matchedPhases) {
        ConductingEquipment conductingEquipment = Objects.requireNonNull(terminal.getConductingEquipment());
        for (SinglePhaseKind phase : candidatePhases) {
            if (terminal.getPhases().singlePhases().contains(phase) && !openTest.isOpen(conductingEquipment, phase)) {
                if (activePhases.status(terminal, phase).direction().has(direction)) {
                    matchedPhases.add(phase);
                }
            }
        }
    }

    // Should not be able to instantiate this class.
    private PhaseTrace() {
    }

}
