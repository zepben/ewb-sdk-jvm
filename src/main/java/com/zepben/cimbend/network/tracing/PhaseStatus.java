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
 * Interface to query or set the phase for a core on a {@link Terminal}.
 */
@EverythingIsNonnullByDefault
public interface PhaseStatus {

    /**
     * @return The phase added to this status.
     */
    SinglePhaseKind phase();

    /**
     * @return The direction added to this status.
     */
    PhaseDirection direction();

    /**
     * Clears the phase and sets it to the specified phase and direction.
     * If the passed in phase is NONE or the passed in direction is NONE, this will clear the phase status.
     *
     * @param singlePhaseKind The new phase to be set.
     * @param direction       The direction of the phase.
     */
    boolean set(SinglePhaseKind singlePhaseKind, PhaseDirection direction);

    /**
     * Adds a phase to the status with the given direction.
     *
     * @param singlePhaseKind The phase to be added.
     * @param direction       The direction of the phase.
     * @return true if the phase or direction has been updated.
     */
    boolean add(SinglePhaseKind singlePhaseKind, PhaseDirection direction);

    /**
     * Removes a phase from the status matching a specific direction.
     *
     * @param singlePhaseKind The phase to be removed.
     * @param direction       The direction to match with the phase being removed.
     * @return true if the phase or direction has been updated.
     */
    boolean remove(SinglePhaseKind singlePhaseKind, PhaseDirection direction);

    /**
     * Removes a phase from the status in any direction.
     *
     * @param singlePhaseKind The phase to be removed.
     * @return true if the phase or direction has been updated.
     */
    boolean remove(SinglePhaseKind singlePhaseKind);

}
