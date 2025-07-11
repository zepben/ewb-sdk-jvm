/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.operators

import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.services.network.tracing.phases.PhaseStatus

/**
 * Interface for accessing the phase status of a terminal.
 */
interface PhaseStateOperators {
    /**
     * Retrieves the phase status of the specified terminal.
     *
     * @param terminal The terminal for which to retrieve the phase status.
     * @return The phase status associated with the specified terminal.
     */
    fun phaseStatus(terminal: Terminal): PhaseStatus

    companion object {
        /**
         * Instance for accessing the normal phase status of terminals.
         */
        val NORMAL: PhaseStateOperators = NormalPhaseStateOperators()

        /**
         * Instance for accessing the current phase status of terminals.
         */
        val CURRENT: PhaseStateOperators = CurrentPhaseStateOperators()
    }
}

private class NormalPhaseStateOperators : PhaseStateOperators {
    override fun phaseStatus(terminal: Terminal): PhaseStatus = terminal.normalPhases
}

private class CurrentPhaseStateOperators : PhaseStateOperators {
    override fun phaseStatus(terminal: Terminal): PhaseStatus = terminal.currentPhases
}
