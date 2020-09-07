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
import com.zepben.traversals.BasicTracker;
import com.zepben.traversals.BranchRecursiveTraversal;
import com.zepben.traversals.WeightedPriorityQueue;

import java.util.*;

/**
 * Convenience class that provides methods for removing phases on a {@link NetworkService}
 * This class is backed by a {@link BranchRecursiveTraversal}.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@EverythingIsNonnullByDefault
public class RemovePhases {

    BranchRecursiveTraversal<EbbPhases> normalTraversal;
    BranchRecursiveTraversal<EbbPhases> currentTraversal;

    public RemovePhases() {
        normalTraversal = new BranchRecursiveTraversal<>(
            this::removeNormalPhasesAndQueueNext,
            () -> WeightedPriorityQueue.processQueue(EbbPhases::numPhases),
            BasicTracker::new,
            () -> WeightedPriorityQueue.branchQueue(EbbPhases::numPhases));

        currentTraversal = new BranchRecursiveTraversal<>(
            this::removeCurrentPhasesAndQueueNext,
            () -> WeightedPriorityQueue.processQueue(EbbPhases::numPhases),
            BasicTracker::new,
            () -> WeightedPriorityQueue.branchQueue(EbbPhases::numPhases));
    }

    BranchRecursiveTraversal<EbbPhases> normalTraversal() {
        return normalTraversal;
    }

    BranchRecursiveTraversal<EbbPhases> currentTraversal() {
        return currentTraversal;
    }

    public void run(ConductingEquipment conductingEquipment) {
        conductingEquipment.getTerminals().forEach(this::run);
    }

    public void run(Terminal terminal) {
        run(terminal, new HashSet<>(terminal.getPhases().singlePhases()));
    }

    public void run(ConductingEquipment start, Set<SinglePhaseKind> nominalPhasesToEbb) {
        start.getTerminals().forEach(terminal -> run(terminal, nominalPhasesToEbb));
    }

    public void run(Terminal terminal, Set<SinglePhaseKind> nominalPhasesToEbb) {
        EbbPhases start = new EbbPhases(terminal, nominalPhasesToEbb);

        runFromOutTerminal(normalTraversal, start);
        runFromOutTerminal(currentTraversal, start);
    }

    private void runFromOutTerminal(BranchRecursiveTraversal<EbbPhases> traversal, EbbPhases start) {
        traversal.reset()
            .run(start);
    }

    private void removeNormalPhasesAndQueueNext(EbbPhases current, BranchRecursiveTraversal<EbbPhases> traversal) {
        ebbOutAndQueue(traversal, current, PhaseSelector.NORMAL_PHASES);
    }

    private void removeCurrentPhasesAndQueueNext(EbbPhases current, BranchRecursiveTraversal<EbbPhases> traversal) {
        ebbOutAndQueue(traversal, current, PhaseSelector.CURRENT_PHASES);
    }

    private void ebbOutAndQueue(BranchRecursiveTraversal<EbbPhases> traversal, EbbPhases current, PhaseSelector phaseSelector) {
        Set<SinglePhaseKind> processedNominalPhases = ebbPhases(current.terminal, current.nominalPhasesToEbb, PhaseDirection.OUT, phaseSelector);
        List<ConnectivityResult> connectedTerminals = NetworkService.connectedTerminals(current.terminal, processedNominalPhases);

        if (connectedTerminals.isEmpty())
            return;

        Map<SinglePhaseKind, Set<Terminal>> terminalsByPhase = new HashMap<>();
        Map<SinglePhaseKind, Set<Terminal>> otherFeedsByPhase = new HashMap<>();
        sortTerminalsByPhase(connectedTerminals, terminalsByPhase, otherFeedsByPhase, phaseSelector);

        //
        // For each nominal phase check the number of other feeds:
        //    0:  remove in phase from all connected terminals.
        //    1:  remove in phase only from the other feed.
        //    2+: do not queue or remove anything else as everything is still being fed.
        //
        // To do this we collect the phases back into a set to avoid multi tracing the network.
        //
        Map<Terminal, Set<SinglePhaseKind>> phasesByTerminalsToEbbAndQueue = new HashMap<>();
        processedNominalPhases.forEach(phase -> {
            // Check if any of the connected terminals are also feeding the connectivity node.
            Set<Terminal> feedTerminals = otherFeedsByPhase.getOrDefault(phase, Collections.emptySet());
            if (feedTerminals.isEmpty())
                addTerminalPhases(terminalsByPhase.get(phase), phase, phasesByTerminalsToEbbAndQueue);
            else if (feedTerminals.size() == 1)
                addTerminalPhases(feedTerminals, phase, phasesByTerminalsToEbbAndQueue);
        });

        phasesByTerminalsToEbbAndQueue.forEach((terminal, phases) -> {
            Set<SinglePhaseKind> hadInPhases = ebbPhases(terminal, phases, PhaseDirection.IN, phaseSelector);

            terminal.getConductingEquipment().getTerminals().forEach(t -> {
                if (t != terminal)
                    traversal.queue().add(new EbbPhases(t, hadInPhases));
            });
        });
    }

    private Set<SinglePhaseKind> ebbPhases(Terminal terminal, Set<SinglePhaseKind> phases, PhaseDirection direction, PhaseSelector phaseSelector) {
        Set<SinglePhaseKind> hadPhases = new HashSet<>();
        phases.forEach(phase -> {
            PhaseStatus status = phaseSelector.status(terminal, phase);
            if (status.remove(status.phase(), direction))
                hadPhases.add(phase);
        });
        return hadPhases;
    }

    private void sortTerminalsByPhase(List<ConnectivityResult> connectedTerminals,
                                      Map<SinglePhaseKind, Set<Terminal>> terminalsByPhase,
                                      Map<SinglePhaseKind, Set<Terminal>> otherFeedsByPhase,
                                      PhaseSelector phaseSelector) {
        connectedTerminals.forEach(cr -> cr.toNominalPhases()
            .forEach(phase -> {
                terminalsByPhase.computeIfAbsent(phase, k -> new HashSet<>()).add(cr.toTerminal());
                if (phaseSelector.status(cr.toTerminal(), phase).direction().has(PhaseDirection.BOTH))
                    otherFeedsByPhase.computeIfAbsent(phase, k -> new HashSet<>()).add(cr.toTerminal());
            })
        );
    }

    private void addTerminalPhases(Set<Terminal> terminals, SinglePhaseKind phase, Map<Terminal, Set<SinglePhaseKind>> phasesByTerminalsToEbbAndQueue) {
        terminals.forEach(terminal -> phasesByTerminalsToEbbAndQueue.computeIfAbsent(terminal, k -> new HashSet<>()).add(phase));
    }

    private static class EbbPhases {

        private final Terminal terminal;
        private final Set<SinglePhaseKind> nominalPhasesToEbb;

        private EbbPhases(Terminal terminal, Set<SinglePhaseKind> nominalPhasesToEbb) {
            this.terminal = terminal;
            this.nominalPhasesToEbb = nominalPhasesToEbb;
        }

        public int numPhases() {
            return nominalPhasesToEbb.size();
        }

    }

}
