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
import com.zepben.cimbend.cim.iec61970.base.core.Terminal;
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind;
import com.zepben.cimbend.network.model.PhaseDirection;

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
