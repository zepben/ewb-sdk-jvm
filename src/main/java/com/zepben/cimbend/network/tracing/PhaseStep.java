/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network.tracing;

import com.google.common.collect.ImmutableSet;
import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.cimbend.cim.iec61970.base.core.PhaseCode;
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * Class that records which cores were traced to get to a given conducting equipment during a trace.
 * Allows a trace to continue only on the cores used to get to the current step in the trace.
 */
@SuppressWarnings("WeakerAccess")
@EverythingIsNonnullByDefault
public class PhaseStep {

    private final ConductingEquipment conductingEquipment;
    private final Set<SinglePhaseKind> phases;
    @Nullable private final ConductingEquipment previous;

    public static PhaseStep startAt(ConductingEquipment conductingEquipment, Collection<SinglePhaseKind> phases) {
        return new PhaseStep(conductingEquipment, phases, null);
    }

    public static PhaseStep startAt(ConductingEquipment conductingEquipment, PhaseCode phaseCode) {
        return startAt(conductingEquipment, phaseCode.singlePhases());
    }

    public static PhaseStep continueAt(ConductingEquipment conductingEquipment, Collection<SinglePhaseKind> phases, ConductingEquipment previous) {
        return new PhaseStep(conductingEquipment, phases, previous);
    }

    public static PhaseStep continueAt(ConductingEquipment conductingEquipment, PhaseCode phaseCode, ConductingEquipment previous) {
        return continueAt(conductingEquipment, phaseCode.singlePhases(), previous);
    }

    public ConductingEquipment conductingEquipment() {
        return conductingEquipment;
    }

    public Set<SinglePhaseKind> phases() {
        return phases;
    }

    @Nullable
    public ConductingEquipment previous() {
        return previous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhaseStep)) return false;
        PhaseStep that = (PhaseStep) o;
        return Objects.equals(conductingEquipment, that.conductingEquipment) &&
            Objects.equals(phases, that.phases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conductingEquipment, phases);
    }

    @Override
    public String toString() {
        return "PhaseStep{" +
            "current = " + conductingEquipment.getMRID() +
            ", phases = " + phases +
            ", previous = " + previous +
            '}';
    }

    private PhaseStep(ConductingEquipment conductingEquipment, Collection<SinglePhaseKind> phases, @Nullable ConductingEquipment previous) {
        this.conductingEquipment = conductingEquipment;
        this.phases = ImmutableSet.copyOf(phases);
        this.previous = previous;
    }

}
