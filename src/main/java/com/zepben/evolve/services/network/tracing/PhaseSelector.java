/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.evolve.cim.iec61970.base.core.Terminal;
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind;
import com.zepben.evolve.services.network.model.PhaseDirection;

/**
 * Functional interface that can be used by traces to specify which phase status to use.
 * See {@link SetPhases} for example usage.
 */
@EverythingIsNonnullByDefault
public interface PhaseSelector {

    // Constant common implements of ActivePhaseSelector
    PhaseSelector NORMAL_PHASES = (terminal, nominalPhase) -> new PhaseStatus() {

        @Override
        public SinglePhaseKind phase() {
            return terminal.getTracedPhases().phaseNormal(nominalPhase);
        }

        @Override
        public PhaseDirection direction() {
            return terminal.getTracedPhases().directionNormal(nominalPhase);
        }

        @Override
        public boolean set(SinglePhaseKind singlePhaseKind, PhaseDirection direction) {
            return terminal.getTracedPhases().setNormal(singlePhaseKind, direction, nominalPhase);
        }

        @Override
        public boolean add(SinglePhaseKind singlePhaseKind, PhaseDirection direction) {
            return terminal.getTracedPhases().addNormal(singlePhaseKind, direction, nominalPhase);
        }

        @Override
        public boolean remove(SinglePhaseKind singlePhaseKind, PhaseDirection direction) {
            return terminal.getTracedPhases().removeNormal(singlePhaseKind, direction, nominalPhase);
        }

        @Override
        public boolean remove(SinglePhaseKind singlePhaseKind) {
            return terminal.getTracedPhases().removeNormal(singlePhaseKind, nominalPhase);
        }
    };

    PhaseSelector CURRENT_PHASES = (terminal, nominalPhase) -> new PhaseStatus() {

        @Override
        public SinglePhaseKind phase() {
            return terminal.getTracedPhases().phaseCurrent(nominalPhase);
        }

        @Override
        public PhaseDirection direction() {
            return terminal.getTracedPhases().directionCurrent(nominalPhase);
        }

        @Override
        public boolean set(SinglePhaseKind singlePhaseKind, PhaseDirection direction) {
            return terminal.getTracedPhases().setCurrent(singlePhaseKind, direction, nominalPhase);
        }

        @Override
        public boolean add(SinglePhaseKind singlePhaseKind, PhaseDirection direction) {
            return terminal.getTracedPhases().addCurrent(singlePhaseKind, direction, nominalPhase);
        }

        @Override
        public boolean remove(SinglePhaseKind singlePhaseKind, PhaseDirection direction) {
            return terminal.getTracedPhases().removeCurrent(singlePhaseKind, direction, nominalPhase);
        }

        @Override
        public boolean remove(SinglePhaseKind singlePhaseKind) {
            return terminal.getTracedPhases().removeCurrent(singlePhaseKind, nominalPhase);
        }
    };

    PhaseStatus status(Terminal terminal, SinglePhaseKind nominalPhase);

}
