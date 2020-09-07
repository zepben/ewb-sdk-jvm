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
package com.zepben.cimbend.network.tracing;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.cimbend.cim.iec61970.base.core.Terminal;
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind;
import com.zepben.cimbend.network.NetworkService;
import com.zepben.cimbend.network.model.PhaseDirection;
import com.zepben.traversals.BasicTraversal;
import com.zepben.traversals.WeightedPriorityQueue;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

                if (!outPhases.isEmpty()) {
                    List<ConnectivityResult> inTerminals = NetworkService.connectedTerminals(terminal, outPhases);
                    inTerminals.forEach(cr -> traversal.queue().add(PhaseStep.continueAt(cr.to(), cr.toNominalPhases(), cr.from())));
                }
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

                if (!outPhases.isEmpty()) {
                    List<ConnectivityResult> inTerminals = NetworkService.connectedTerminals(terminal, outPhases);
                    inTerminals.forEach(cr -> traversal.queue().add(PhaseStep.continueAt(cr.to(), cr.toNominalPhases(), cr.from())));
                }
            });
        };
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
                        Set<SinglePhaseKind> outPhases = cr.toNominalPhases()
                            .stream()
                            .filter(phase -> activePhases.status(cr.toTerminal(), phase).direction().has(PhaseDirection.OUT))
                            .collect(Collectors.toSet());

                        if (!outPhases.isEmpty()) {
                            traversal.queue().add(PhaseStep.continueAt(cr.to(), outPhases, cr.from()));
                        }
                    });
                }
            });
        };
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
