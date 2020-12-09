/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing;

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode;
import com.zepben.evolve.cim.iec61970.base.core.Terminal;
import com.zepben.evolve.cim.iec61970.base.wires.Junction;
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind;
import com.zepben.evolve.services.network.tracing.phases.PhaseDirection;
import com.zepben.evolve.services.network.tracing.phases.PhaseSelector;
import com.zepben.evolve.services.network.tracing.phases.PhaseStatus;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

public class PhaseSelectorTest {

    @Test
    public void testPhaseSelectors() {
        Junction j = new Junction("test");
        Terminal t = new Terminal();
        t.setConductingEquipment(j);
        testPhaseSelector(t, Terminal::normalPhases);
        testPhaseSelector(t, Terminal::currentPhases);
    }

    private void testPhaseSelector(Terminal t, PhaseSelector phaseSelector) {
        for (SinglePhaseKind phase : PhaseCode.ABCN.singlePhases()) {
            PhaseStatus ps = phaseSelector.status(t, phase);

            assertThat(ps.phase(), equalTo(SinglePhaseKind.NONE));

            // Can't add a phase with no direction
            ps.add(SinglePhaseKind.A, PhaseDirection.NONE);
            assertThat(ps.phase(), equalTo(SinglePhaseKind.NONE));

            // Add an A phase IN
            assertThat(ps.add(SinglePhaseKind.A, PhaseDirection.IN), equalTo(true));
            assertThat(ps.add(SinglePhaseKind.A, PhaseDirection.IN), equalTo(false));
            assertThat(ps.phase(), equalTo(SinglePhaseKind.A));
            assertThat(ps.direction().has(PhaseDirection.IN), equalTo(true));
            assertThat(ps.direction().has(PhaseDirection.BOTH), equalTo(false));
            assertThat(ps.direction().has(PhaseDirection.OUT), equalTo(false));

            // Adding NONE phase returns false
            assertThat(ps.add(SinglePhaseKind.NONE, PhaseDirection.NONE), equalTo(false));
            assertThat(ps.phase(), equalTo(SinglePhaseKind.A));
            assertThat(ps.direction().has(PhaseDirection.IN), equalTo(true));
            assertThat(ps.direction().has(PhaseDirection.BOTH), equalTo(false));
            assertThat(ps.direction().has(PhaseDirection.OUT), equalTo(false));

            // Add OUT to the A phase
            assertThat(ps.add(SinglePhaseKind.A, PhaseDirection.OUT), equalTo(true));
            assertThat(ps.add(SinglePhaseKind.A, PhaseDirection.OUT), equalTo(false));
            assertThat(ps.phase(), equalTo(SinglePhaseKind.A));
            assertThat(ps.direction().has(PhaseDirection.IN), equalTo(true));
            assertThat(ps.direction().has(PhaseDirection.BOTH), equalTo(true));
            assertThat(ps.direction().has(PhaseDirection.OUT), equalTo(true));

            // Remove IN from the A Phase
            assertThat(ps.remove(SinglePhaseKind.A, PhaseDirection.IN), equalTo(true));
            assertThat(ps.remove(SinglePhaseKind.A, PhaseDirection.IN), equalTo(false));
            assertThat(ps.phase(), equalTo(SinglePhaseKind.A));
            assertThat(ps.direction().has(PhaseDirection.IN), equalTo(false));
            assertThat(ps.direction().has(PhaseDirection.BOTH), equalTo(false));
            assertThat(ps.direction().has(PhaseDirection.OUT), equalTo(true));

            // Remove A Phase
            assertThat(ps.remove(SinglePhaseKind.A), equalTo(true));
            assertThat(ps.remove(SinglePhaseKind.A), equalTo(false));
            assertThat(ps.phase(), equalTo(SinglePhaseKind.NONE));
            assertThat(ps.direction(), equalTo(PhaseDirection.NONE));

            // Add a B phase BOTH
            assertThat(ps.add(SinglePhaseKind.B, PhaseDirection.BOTH), equalTo(true));
            assertThat(ps.phase(), equalTo(SinglePhaseKind.B));
            assertThat(ps.direction().has(PhaseDirection.IN), equalTo(true));
            assertThat(ps.direction().has(PhaseDirection.BOTH), equalTo(true));
            assertThat(ps.direction().has(PhaseDirection.OUT), equalTo(true));

            //Set a N phase BOTH
            assertThat(ps.set(SinglePhaseKind.N, PhaseDirection.BOTH), equalTo(true));
            assertThat(ps.set(SinglePhaseKind.N, PhaseDirection.BOTH), equalTo(false));
            assertThat(ps.phase(), equalTo(SinglePhaseKind.N));
            assertThat(ps.direction(), equalTo(PhaseDirection.BOTH));

            // Setting NONE to the direction clears the whole phase
            ps.set(SinglePhaseKind.N, PhaseDirection.NONE);
            assertThat(ps.direction(), equalTo(PhaseDirection.NONE));
            assertThat(ps.phase(), equalTo(SinglePhaseKind.NONE));

            //Set a A phase IN
            assertThat(ps.set(SinglePhaseKind.A, PhaseDirection.IN), equalTo(true));
            assertThat(ps.set(SinglePhaseKind.A, PhaseDirection.IN), equalTo(false));
            assertThat(ps.phase(), equalTo(SinglePhaseKind.A));
            assertThat(ps.direction(), equalTo(PhaseDirection.IN));

            // Setting NONE to the phase clears the whole phase
            assertThat(ps.set(SinglePhaseKind.NONE, PhaseDirection.BOTH), equalTo(true));
            assertThat(ps.direction(), equalTo(PhaseDirection.NONE));
            assertThat(ps.phase(), equalTo(SinglePhaseKind.NONE));

            //Set a N phase BOTH
            assertThat(ps.set(SinglePhaseKind.N, PhaseDirection.OUT), equalTo(true));
            assertThat(ps.set(SinglePhaseKind.N, PhaseDirection.OUT), equalTo(false));
            assertThat(ps.phase(), equalTo(SinglePhaseKind.N));
            assertThat(ps.direction(), equalTo(PhaseDirection.OUT));

            try {
                ps.add(SinglePhaseKind.B, PhaseDirection.BOTH);
                fail("Crossing phases should have thrown.");
            } catch (UnsupportedOperationException ignored) {
            }
        }
    }

}
