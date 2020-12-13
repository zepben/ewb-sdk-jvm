/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.evolve.cim.iec61970.base.core.Terminal;
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind;

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
