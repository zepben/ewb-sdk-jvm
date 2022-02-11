/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind

/**
 * Interface to query or set the phase for a core on a [Terminal].
 */
interface PhaseStatus {

    /**
     * Get the traced phase for the specified [nominalPhase].
     *
     * @param nominalPhase The nominal phase you are interested in querying.
     */
    operator fun get(nominalPhase: SinglePhaseKind): SinglePhaseKind

    /**
     * Set the traced phase for the specified [nominalPhase].
     *
     * @param nominalPhase The nominal phase you are interested in updating.
     * @param singlePhaseKind The phase you wish to set for this [nominalPhase]. Specify [SinglePhaseKind.NONE] to clear the phase.
     *
     * @return True if the phase is updated, otherwise false.
     */
    operator fun set(nominalPhase: SinglePhaseKind, singlePhaseKind: SinglePhaseKind): Boolean

}
