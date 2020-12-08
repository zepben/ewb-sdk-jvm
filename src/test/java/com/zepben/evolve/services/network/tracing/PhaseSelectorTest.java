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
import com.zepben.evolve.services.network.model.PhaseDirection;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;

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

            assertEquals(SinglePhaseKind.NONE, ps.phase());

            // Can't add a phase with no direction
            ps.add(SinglePhaseKind.A, PhaseDirection.NONE);
            assertEquals(SinglePhaseKind.NONE, ps.phase());

            // Add an A phase IN
            assertTrue(ps.add(SinglePhaseKind.A, PhaseDirection.IN));
            assertFalse(ps.add(SinglePhaseKind.A, PhaseDirection.IN));
            assertEquals(SinglePhaseKind.A, ps.phase());
            assertTrue(ps.direction().has(PhaseDirection.IN));
            assertFalse(ps.direction().has(PhaseDirection.BOTH));
            assertFalse(ps.direction().has(PhaseDirection.OUT));

            // Adding NONE phase returns false
            assertFalse(ps.add(SinglePhaseKind.NONE, PhaseDirection.NONE));
            assertEquals(SinglePhaseKind.A, ps.phase());
            assertTrue(ps.direction().has(PhaseDirection.IN));
            assertFalse(ps.direction().has(PhaseDirection.BOTH));
            assertFalse(ps.direction().has(PhaseDirection.OUT));

            // Add OUT to the A phase
            assertTrue(ps.add(SinglePhaseKind.A, PhaseDirection.OUT));
            assertFalse(ps.add(SinglePhaseKind.A, PhaseDirection.OUT));
            assertEquals(SinglePhaseKind.A, ps.phase());
            assertTrue(ps.direction().has(PhaseDirection.IN));
            assertTrue(ps.direction().has(PhaseDirection.BOTH));
            assertTrue(ps.direction().has(PhaseDirection.OUT));

            // Remove IN from the A Phase
            assertTrue(ps.remove(SinglePhaseKind.A, PhaseDirection.IN));
            assertFalse(ps.remove(SinglePhaseKind.A, PhaseDirection.IN));
            assertEquals(SinglePhaseKind.A, ps.phase());
            assertFalse(ps.direction().has(PhaseDirection.IN));
            assertFalse(ps.direction().has(PhaseDirection.BOTH));
            assertTrue(ps.direction().has(PhaseDirection.OUT));

            // Remove A Phase
            assertTrue(ps.remove(SinglePhaseKind.A));
            assertFalse(ps.remove(SinglePhaseKind.A));
            assertEquals(SinglePhaseKind.NONE, ps.phase());
            assertEquals(PhaseDirection.NONE, ps.direction());

            // Add a B phase BOTH
            assertTrue(ps.add(SinglePhaseKind.B, PhaseDirection.BOTH));
            assertEquals(SinglePhaseKind.B, ps.phase());
            assertTrue(ps.direction().has(PhaseDirection.IN));
            assertTrue(ps.direction().has(PhaseDirection.BOTH));
            assertTrue(ps.direction().has(PhaseDirection.OUT));

            //Set a N phase BOTH
            assertTrue(ps.set(SinglePhaseKind.N, PhaseDirection.BOTH));
            assertFalse(ps.set(SinglePhaseKind.N, PhaseDirection.BOTH));
            assertEquals(SinglePhaseKind.N, ps.phase());
            assertEquals(PhaseDirection.BOTH, ps.direction());

            // Setting NONE to the direction clears the whole phase
            ps.set(SinglePhaseKind.N, PhaseDirection.NONE);
            assertEquals(PhaseDirection.NONE, ps.direction());
            assertEquals(SinglePhaseKind.NONE, ps.phase());

            //Set a A phase IN
            assertTrue(ps.set(SinglePhaseKind.A, PhaseDirection.IN));
            assertFalse(ps.set(SinglePhaseKind.A, PhaseDirection.IN));
            assertEquals(SinglePhaseKind.A, ps.phase());
            assertEquals(PhaseDirection.IN, ps.direction());

            // Setting NONE to the phase clears the whole phase
            assertTrue(ps.set(SinglePhaseKind.NONE, PhaseDirection.BOTH));
            assertEquals(PhaseDirection.NONE, ps.direction());
            assertEquals(SinglePhaseKind.NONE, ps.phase());

            //Set a N phase BOTH
            assertTrue(ps.set(SinglePhaseKind.N, PhaseDirection.OUT));
            assertFalse(ps.set(SinglePhaseKind.N, PhaseDirection.OUT));
            assertEquals(SinglePhaseKind.N, ps.phase());
            assertEquals(PhaseDirection.OUT, ps.direction());

            try {
                ps.add(SinglePhaseKind.B, PhaseDirection.BOTH);
                fail("Crossing phases should have thrown.");
            } catch (UnsupportedOperationException ignored) {
            }
        }
    }

}
