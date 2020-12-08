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
import com.zepben.evolve.services.network.tracing.traversals.BranchRecursiveTraversal;
import com.zepben.evolve.services.network.tracing.traversals.Tracker;
import com.zepben.evolve.services.network.tracing.traversals.WeightedPriorityQueue;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@EverythingIsNonnullByDefault
public class DownstreamTree {

    @SuppressWarnings("WeakerAccess")
    public static class TreeNode {

        private final ConductingEquipment conductingEquipment;
        private WeakReference<TreeNode> parent;
        private final List<TreeNode> children = new ArrayList<>();

        TreeNode(ConductingEquipment conductingEquipment) {
            this.conductingEquipment = conductingEquipment;
            this.parent = new WeakReference<>(null);
        }

        @Nullable
        public TreeNode parent() {
            return parent.get();
        }

        public List<TreeNode> children() {
            return Collections.unmodifiableList(children);
        }

        public ConductingEquipment conductingEquipment() {
            return conductingEquipment;
        }

        public TreeNode setParent(@Nullable TreeNode parent) {
            TreeNode parentNode = this.parent.get();
            if (parentNode != null)
                parentNode.children.remove(this);

            if (parent != null) {
                this.parent = new WeakReference<>(parent);
                parent.children.add(this);
            } else
                this.parent = new WeakReference<>(null);

            return this;
        }

        private Integer sortWeight() {
            AtomicInteger weight = new AtomicInteger(1);
            conductingEquipment.getTerminals().forEach(terminal -> weight.set(Math.max(weight.get(), terminal.getPhases().singlePhases().size())));
            return weight.get();
        }

        @Override
        public String toString() {
            TreeNode parent = this.parent.get();
            return "{conductingEquipment: " + conductingEquipment.getMRID() + ", parent: " + (parent != null ? parent.conductingEquipment().getMRID() : "") + ", num children: " + children.size() + "}";
        }

    }

    /**
     * Simple tracker for traversals that just tracks the items visited and the order visited.
     */
    public static class TreeNodeTracker implements Tracker<TreeNode> {

        private final Set<ConductingEquipment> visited;

        TreeNodeTracker() {
            visited = new HashSet<>();
        }

        @Override
        public boolean hasVisited(@Nullable TreeNode item) {
            return item == null || visited.contains(item.conductingEquipment);
        }

        @Override
        public boolean visit(@Nullable TreeNode item) {
            if (item == null)
                return false;

            return visited.add(item.conductingEquipment());
        }

        @Override
        public void clear() {
            visited.clear();
        }

    }

    private final OpenTest openTest;
    private final PhaseSelector phaseSelector;
    private final BranchRecursiveTraversal<TreeNode> traversal;

    DownstreamTree(OpenTest openTest, PhaseSelector phaseSelector) {
        this.openTest = openTest;
        this.phaseSelector = phaseSelector;
        traversal = new BranchRecursiveTraversal<>(
            this::addAndQueueNext,
            () -> WeightedPriorityQueue.processQueue(TreeNode::sortWeight),
            TreeNodeTracker::new,
            () -> WeightedPriorityQueue.branchQueue(TreeNode::sortWeight));
    }

    public TreeNode run(ConductingEquipment start) {
        TreeNode root = new TreeNode(start);
        traversal.run(root);
        return root;
    }

    private void addAndQueueNext(TreeNode current, BranchRecursiveTraversal<TreeNode> traversal) {
        // Loop through each of the terminals on the current conducting equipment
        Set<SinglePhaseKind> outPhases = new HashSet<>();
        current.conductingEquipment().getTerminals().forEach(outTerminal -> {
            // Find all the nominal phases which are going out
            getOutPhases(outTerminal, outPhases);
            if (outPhases.size() > 0)
                queueConnectedTerminals(traversal, current, outTerminal, outPhases);
        });
    }

    private void getOutPhases(Terminal terminal, Set<SinglePhaseKind> outPhases) {
        outPhases.clear();
        ConductingEquipment conductingEquipment = Objects.requireNonNull(terminal.getConductingEquipment());
        for (SinglePhaseKind phase : terminal.getPhases().singlePhases()) {
            if (!openTest.isOpen(conductingEquipment, phase)) {
                if (phaseSelector.status(terminal, phase).direction().has(PhaseDirection.OUT))
                    outPhases.add(phase);
            }
        }
    }

    private void queueConnectedTerminals(BranchRecursiveTraversal<TreeNode> traversal,
                                         TreeNode current,
                                         Terminal outTerminal,
                                         Set<SinglePhaseKind> outPhases) {
        // Get all the connected terminals with phases going out
        List<ConnectivityResult> inTerminals = NetworkService.connectedTerminals(outTerminal, outPhases);

        // Make sure we do not loop back out the incoming terminal if its direction is both.
        TreeNode previousNode = current.parent();
        if (previousNode != null) {
            for (ConnectivityResult cr : inTerminals) {
                if (cr.getTo() == previousNode.conductingEquipment())
                    return;
            }
        }

        if (inTerminals.size() > 1 || Objects.requireNonNull(outTerminal.getConductingEquipment()).numTerminals() > 2) {
            for (ConnectivityResult cr : inTerminals) {
                ConductingEquipment to = cr.getTo();
                if (to != null) {
                    TreeNode next = new TreeNode(to);

                    // Only branch to the next item if we have not already been there.
                    if (!traversal.hasVisited(next))
                        traversal.branchQueue().add(traversal.branchSupplier().get().setStart(next.setParent(current)));
                }
            }
        } else {
            for (ConnectivityResult cr : inTerminals) {
                ConductingEquipment to = cr.getTo();
                if (to != null) {
                    TreeNode next = new TreeNode(to);

                    // Only queue up the next item if we have not already been there.
                    if (!traversal.hasVisited(next))
                        traversal.queue().add(next.setParent(current));
                }
            }
        }
    }

}
