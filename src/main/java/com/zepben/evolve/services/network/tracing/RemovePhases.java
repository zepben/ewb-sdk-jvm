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
import com.zepben.evolve.services.network.model.NominalPhasePath;
import com.zepben.evolve.services.network.model.PhaseDirection;
import com.zepben.evolve.services.network.tracing.traversals.BasicTracker;
import com.zepben.evolve.services.network.tracing.traversals.BranchRecursiveTraversal;
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue;

import javax.annotation.Nullable;
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

        Map<SinglePhaseKind, Set<ConnectivityResult>> terminalsByPhase = new HashMap<>();
        Map<SinglePhaseKind, Set<ConnectivityResult>> otherFeedsByPhase = new HashMap<>();
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
            Set<ConnectivityResult> feedTerminals = otherFeedsByPhase.getOrDefault(phase, Collections.emptySet());
            if (feedTerminals.isEmpty())
                addTerminalPhases(terminalsByPhase.get(phase), phase, phasesByTerminalsToEbbAndQueue);
            else if (feedTerminals.size() == 1)
                addTerminalPhases(feedTerminals, phase, phasesByTerminalsToEbbAndQueue);
        });

        phasesByTerminalsToEbbAndQueue.forEach((terminal, phases) -> {
            Set<SinglePhaseKind> hadInPhases = ebbPhases(terminal, phases, PhaseDirection.IN, phaseSelector);

            Objects.requireNonNull(terminal.getConductingEquipment()).getTerminals().forEach(t -> {
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
                                      Map<SinglePhaseKind, Set<ConnectivityResult>> terminalsByPhase,
                                      Map<SinglePhaseKind, Set<ConnectivityResult>> otherFeedsByPhase,
                                      PhaseSelector phaseSelector) {
        connectedTerminals.forEach(cr ->
            cr.getNominalPhasePaths()
                .forEach(nominalPhasePath -> {
                    terminalsByPhase.computeIfAbsent(nominalPhasePath.getFrom(), k -> new HashSet<>()).add(cr);
                    if (phaseSelector.status(cr.getToTerminal(), nominalPhasePath.getTo()).direction().has(PhaseDirection.BOTH))
                        otherFeedsByPhase.computeIfAbsent(nominalPhasePath.getFrom(), k -> new HashSet<>()).add(cr);
                })
        );
    }

    private void addTerminalPhases(@Nullable Set<ConnectivityResult> terminals, SinglePhaseKind phase, Map<Terminal, Set<SinglePhaseKind>> phasesByTerminalsToEbbAndQueue) {
        if (terminals == null)
            return;

        terminals.forEach(terminal ->
            phasesByTerminalsToEbbAndQueue.computeIfAbsent(terminal.getToTerminal(), k -> new HashSet<>())
                .add(terminal.getNominalPhasePaths()
                    .stream()
                    .filter(nominalPhasePath -> nominalPhasePath.getFrom() == phase)
                    .map(NominalPhasePath::getTo)
                    .findFirst()
                    .orElse(SinglePhaseKind.NONE)
                )
        );
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
