/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind;
import com.zepben.evolve.services.network.tracing.traversals.Tracker;

import javax.annotation.Nullable;
import java.util.*;

/**
 * <p>A specialised tracker that tracks the cores that have been visited on a piece of conducting equipment. When attempting to visit
 * for the second time, this tracker will return false if the cores being tracked are a subset of those already visited.
 * For example, if you visit A1 on cores 0, 1, 2 and later attempt to visit A1 on core 0, 1, visit will return false,
 * but an attempt to visit on cores 2, 3 would return true as 3 has not been visited before.</p>
 * <p>This tracker does not support null items.</p>
 */
@SuppressWarnings("WeakerAccess")
@EverythingIsNonnullByDefault
public class PhaseStepTracker implements Tracker<PhaseStep> {

    private Map<ConductingEquipment, Set<SinglePhaseKind>> visited;

    public PhaseStepTracker() {
        visited = new HashMap<>();
    }

    @Override
    public boolean hasVisited(@Nullable PhaseStep item) {
        Objects.requireNonNull(item);
        Set<SinglePhaseKind> phases = visited.getOrDefault(item.conductingEquipment(), Collections.emptySet());
        return isSubset(item.phases(), phases);
    }

    @Override
    public boolean visit(@Nullable PhaseStep item) {
        Objects.requireNonNull(item);
        Set<SinglePhaseKind> phases = visited.computeIfAbsent(item.conductingEquipment(), i -> new HashSet<>());
        if (isSubset(item.phases(), phases)) {
            return false;
        }

        phases.addAll(item.phases());
        return true;
    }

    @Override
    public void clear() {
        visited.clear();
    }

    private boolean isSubset(Set<SinglePhaseKind> set1, Set<SinglePhaseKind> set2) {
        return set2.containsAll(set1);
    }

}
