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
package com.zepben.cimbend.network;

import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind;
import com.zepben.cimbend.network.model.PhaseDirection;
import com.zepben.cimbend.network.model.TracedPhases;
import org.junit.jupiter.api.Test;

import static com.zepben.test.util.ExpectException.expect;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;

public class TracedPhasesTest {

    private TracedPhases newTestPhaseObject() {
        TracedPhases tracedPhases = new TracedPhases();

        /* Normal */
        tracedPhases.setNormal(SinglePhaseKind.A, PhaseDirection.IN, SinglePhaseKind.A);
        tracedPhases.setNormal(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.B);
        tracedPhases.setNormal(SinglePhaseKind.C, PhaseDirection.BOTH, SinglePhaseKind.C);
        tracedPhases.setNormal(SinglePhaseKind.N, PhaseDirection.BOTH, SinglePhaseKind.N);

        /* Current */
        tracedPhases.setCurrent(SinglePhaseKind.N, PhaseDirection.BOTH, SinglePhaseKind.A);
        tracedPhases.setCurrent(SinglePhaseKind.C, PhaseDirection.BOTH, SinglePhaseKind.B);
        tracedPhases.setCurrent(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.C);
        tracedPhases.setCurrent(SinglePhaseKind.A, PhaseDirection.IN, SinglePhaseKind.N);

        return tracedPhases;
    }

    @Test
    public void testGetPhase() {
        TracedPhases tracedPhases = newTestPhaseObject();

        /* Normal */
        assertEquals(SinglePhaseKind.A, tracedPhases.phaseNormal(SinglePhaseKind.A));
        assertEquals(SinglePhaseKind.B, tracedPhases.phaseNormal(SinglePhaseKind.B));
        assertEquals(SinglePhaseKind.C, tracedPhases.phaseNormal(SinglePhaseKind.C));
        assertEquals(SinglePhaseKind.N, tracedPhases.phaseNormal(SinglePhaseKind.N));

        /* Current */
        assertEquals(SinglePhaseKind.N, tracedPhases.phaseCurrent(SinglePhaseKind.A));
        assertEquals(SinglePhaseKind.C, tracedPhases.phaseCurrent(SinglePhaseKind.B));
        assertEquals(SinglePhaseKind.B, tracedPhases.phaseCurrent(SinglePhaseKind.C));
        assertEquals(SinglePhaseKind.A, tracedPhases.phaseCurrent(SinglePhaseKind.N));
    }

    @Test
    public void testGetDirection() {
        TracedPhases tracedPhases = newTestPhaseObject();

        /* Normal */
        assertEquals(PhaseDirection.IN, tracedPhases.directionNormal(SinglePhaseKind.A));
        assertEquals(PhaseDirection.OUT, tracedPhases.directionNormal(SinglePhaseKind.B));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionNormal(SinglePhaseKind.C));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionNormal(SinglePhaseKind.N));

        /* Current */
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionCurrent(SinglePhaseKind.A));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionCurrent(SinglePhaseKind.B));
        assertEquals(PhaseDirection.OUT, tracedPhases.directionCurrent(SinglePhaseKind.C));
        assertEquals(PhaseDirection.IN, tracedPhases.directionCurrent(SinglePhaseKind.N));
    }

    @Test
    public void testSet() {
        TracedPhases tracedPhases = newTestPhaseObject();

        /* -- Setting -- */
        /* Normal */
        assertTrue(tracedPhases.setNormal(SinglePhaseKind.N, PhaseDirection.BOTH, SinglePhaseKind.A));
        assertTrue(tracedPhases.setNormal(SinglePhaseKind.C, PhaseDirection.BOTH, SinglePhaseKind.B));
        assertTrue(tracedPhases.setNormal(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.C));
        assertTrue(tracedPhases.setNormal(SinglePhaseKind.A, PhaseDirection.IN, SinglePhaseKind.N));
        // Returns false if no changes were done.
        assertFalse(tracedPhases.setNormal(SinglePhaseKind.N, PhaseDirection.BOTH, SinglePhaseKind.A));
        assertFalse(tracedPhases.setNormal(SinglePhaseKind.C, PhaseDirection.BOTH, SinglePhaseKind.B));
        assertFalse(tracedPhases.setNormal(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.C));
        assertFalse(tracedPhases.setNormal(SinglePhaseKind.A, PhaseDirection.IN, SinglePhaseKind.N));

        /* Current */
        assertTrue(tracedPhases.setCurrent(SinglePhaseKind.A, PhaseDirection.IN, SinglePhaseKind.A));
        assertTrue(tracedPhases.setCurrent(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.B));
        assertTrue(tracedPhases.setCurrent(SinglePhaseKind.C, PhaseDirection.BOTH, SinglePhaseKind.C));
        assertTrue(tracedPhases.setCurrent(SinglePhaseKind.N, PhaseDirection.BOTH, SinglePhaseKind.N));
        // Returns false if no changes were done.
        assertFalse(tracedPhases.setCurrent(SinglePhaseKind.A, PhaseDirection.IN, SinglePhaseKind.A));
        assertFalse(tracedPhases.setCurrent(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.B));
        assertFalse(tracedPhases.setCurrent(SinglePhaseKind.C, PhaseDirection.BOTH, SinglePhaseKind.C));
        assertFalse(tracedPhases.setCurrent(SinglePhaseKind.N, PhaseDirection.BOTH, SinglePhaseKind.N));

        /* -- Getting Phase-- */
        /* Normal */
        assertEquals(SinglePhaseKind.N, tracedPhases.phaseNormal(SinglePhaseKind.A));
        assertEquals(SinglePhaseKind.C, tracedPhases.phaseNormal(SinglePhaseKind.B));
        assertEquals(SinglePhaseKind.B, tracedPhases.phaseNormal(SinglePhaseKind.C));
        assertEquals(SinglePhaseKind.A, tracedPhases.phaseNormal(SinglePhaseKind.N));

        /* Current */
        assertEquals(SinglePhaseKind.A, tracedPhases.phaseCurrent(SinglePhaseKind.A));
        assertEquals(SinglePhaseKind.B, tracedPhases.phaseCurrent(SinglePhaseKind.B));
        assertEquals(SinglePhaseKind.C, tracedPhases.phaseCurrent(SinglePhaseKind.C));
        assertEquals(SinglePhaseKind.N, tracedPhases.phaseCurrent(SinglePhaseKind.N));

        /* -- Getting Direction-- */
        /* Normal */
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionNormal(SinglePhaseKind.A));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionNormal(SinglePhaseKind.B));
        assertEquals(PhaseDirection.OUT, tracedPhases.directionNormal(SinglePhaseKind.C));
        assertEquals(PhaseDirection.IN, tracedPhases.directionNormal(SinglePhaseKind.N));

        /* Current */
        assertEquals(PhaseDirection.IN, tracedPhases.directionCurrent(SinglePhaseKind.A));
        assertEquals(PhaseDirection.OUT, tracedPhases.directionCurrent(SinglePhaseKind.B));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionCurrent(SinglePhaseKind.C));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionCurrent(SinglePhaseKind.N));

        /* -- Setting -- */
        /* Normal */
        assertTrue(tracedPhases.setNormal(SinglePhaseKind.N, PhaseDirection.IN, SinglePhaseKind.A));
        assertTrue(tracedPhases.setNormal(SinglePhaseKind.C, PhaseDirection.OUT, SinglePhaseKind.B));
        assertTrue(tracedPhases.setNormal(SinglePhaseKind.B, PhaseDirection.BOTH, SinglePhaseKind.C));
        assertTrue(tracedPhases.setNormal(SinglePhaseKind.A, PhaseDirection.BOTH, SinglePhaseKind.N));

        /* Current */
        assertTrue(tracedPhases.setCurrent(SinglePhaseKind.A, PhaseDirection.BOTH, SinglePhaseKind.A));
        assertTrue(tracedPhases.setCurrent(SinglePhaseKind.B, PhaseDirection.BOTH, SinglePhaseKind.B));
        assertTrue(tracedPhases.setCurrent(SinglePhaseKind.C, PhaseDirection.OUT, SinglePhaseKind.C));
        assertTrue(tracedPhases.setCurrent(SinglePhaseKind.N, PhaseDirection.IN, SinglePhaseKind.N));

        /* -- Getting Phase-- */
        /* Normal */
        assertEquals(SinglePhaseKind.N, tracedPhases.phaseNormal(SinglePhaseKind.A));
        assertEquals(SinglePhaseKind.C, tracedPhases.phaseNormal(SinglePhaseKind.B));
        assertEquals(SinglePhaseKind.B, tracedPhases.phaseNormal(SinglePhaseKind.C));
        assertEquals(SinglePhaseKind.A, tracedPhases.phaseNormal(SinglePhaseKind.N));

        /* Current */
        assertEquals(SinglePhaseKind.A, tracedPhases.phaseCurrent(SinglePhaseKind.A));
        assertEquals(SinglePhaseKind.B, tracedPhases.phaseCurrent(SinglePhaseKind.B));
        assertEquals(SinglePhaseKind.C, tracedPhases.phaseCurrent(SinglePhaseKind.C));
        assertEquals(SinglePhaseKind.N, tracedPhases.phaseCurrent(SinglePhaseKind.N));

        /* -- Getting Direction-- */
        /* Normal */
        assertEquals(PhaseDirection.IN, tracedPhases.directionNormal(SinglePhaseKind.A));
        assertEquals(PhaseDirection.OUT, tracedPhases.directionNormal(SinglePhaseKind.B));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionNormal(SinglePhaseKind.C));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionNormal(SinglePhaseKind.N));

        /* Current */
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionCurrent(SinglePhaseKind.A));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionCurrent(SinglePhaseKind.B));
        assertEquals(PhaseDirection.OUT, tracedPhases.directionCurrent(SinglePhaseKind.C));
        assertEquals(PhaseDirection.IN, tracedPhases.directionCurrent(SinglePhaseKind.N));

        // Setting NONE to the direction clears the whole phase
        tracedPhases.setNormal(SinglePhaseKind.N, PhaseDirection.NONE, SinglePhaseKind.A);
        tracedPhases.setCurrent(SinglePhaseKind.A, PhaseDirection.NONE, SinglePhaseKind.A);
        assertEquals(PhaseDirection.NONE, tracedPhases.directionNormal(SinglePhaseKind.A));
        assertEquals(PhaseDirection.NONE, tracedPhases.directionCurrent(SinglePhaseKind.A));
        assertEquals(SinglePhaseKind.NONE, tracedPhases.phaseNormal(SinglePhaseKind.A));
        assertEquals(SinglePhaseKind.NONE, tracedPhases.phaseCurrent(SinglePhaseKind.A));

        // Setting NONE to the phase clears the whole phase
        assertTrue(tracedPhases.setNormal(SinglePhaseKind.NONE, PhaseDirection.NONE, SinglePhaseKind.B));
        assertTrue(tracedPhases.setCurrent(SinglePhaseKind.NONE, PhaseDirection.NONE, SinglePhaseKind.B));
        assertEquals(PhaseDirection.NONE, tracedPhases.directionNormal(SinglePhaseKind.B));
        assertEquals(PhaseDirection.NONE, tracedPhases.directionCurrent(SinglePhaseKind.B));
        assertEquals(SinglePhaseKind.NONE, tracedPhases.phaseNormal(SinglePhaseKind.B));
        assertEquals(SinglePhaseKind.NONE, tracedPhases.phaseCurrent(SinglePhaseKind.B));
    }

    @Test
    public void testAdd() {
        TracedPhases tracedPhases = new TracedPhases();

        /* -- Adding -- */
        /* Normal */
        assertTrue(tracedPhases.addNormal(SinglePhaseKind.N, PhaseDirection.BOTH, SinglePhaseKind.A));
        assertTrue(tracedPhases.addNormal(SinglePhaseKind.C, PhaseDirection.BOTH, SinglePhaseKind.B));
        assertTrue(tracedPhases.addNormal(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.C));
        assertTrue(tracedPhases.addNormal(SinglePhaseKind.A, PhaseDirection.IN, SinglePhaseKind.N));
        // Returns false if no changes were done.
        assertFalse(tracedPhases.addNormal(SinglePhaseKind.N, PhaseDirection.BOTH, SinglePhaseKind.A));
        assertFalse(tracedPhases.addNormal(SinglePhaseKind.C, PhaseDirection.BOTH, SinglePhaseKind.B));
        assertFalse(tracedPhases.addNormal(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.C));
        assertFalse(tracedPhases.addNormal(SinglePhaseKind.A, PhaseDirection.IN, SinglePhaseKind.N));

        /* Current */
        assertTrue(tracedPhases.addCurrent(SinglePhaseKind.A, PhaseDirection.IN, SinglePhaseKind.A));
        assertTrue(tracedPhases.addCurrent(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.B));
        assertTrue(tracedPhases.addCurrent(SinglePhaseKind.C, PhaseDirection.BOTH, SinglePhaseKind.C));
        assertTrue(tracedPhases.addCurrent(SinglePhaseKind.N, PhaseDirection.BOTH, SinglePhaseKind.N));
        // Returns false if no changes were done.
        assertFalse(tracedPhases.addCurrent(SinglePhaseKind.A, PhaseDirection.IN, SinglePhaseKind.A));
        assertFalse(tracedPhases.addCurrent(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.B));
        assertFalse(tracedPhases.addCurrent(SinglePhaseKind.C, PhaseDirection.BOTH, SinglePhaseKind.C));
        assertFalse(tracedPhases.addCurrent(SinglePhaseKind.N, PhaseDirection.BOTH, SinglePhaseKind.N));

        /* -- Getting Phase-- */
        /* Normal */
        assertEquals(SinglePhaseKind.N, tracedPhases.phaseNormal(SinglePhaseKind.A));
        assertEquals(SinglePhaseKind.C, tracedPhases.phaseNormal(SinglePhaseKind.B));
        assertEquals(SinglePhaseKind.B, tracedPhases.phaseNormal(SinglePhaseKind.C));
        assertEquals(SinglePhaseKind.A, tracedPhases.phaseNormal(SinglePhaseKind.N));

        /* Current */
        assertEquals(SinglePhaseKind.A, tracedPhases.phaseCurrent(SinglePhaseKind.A));
        assertEquals(SinglePhaseKind.B, tracedPhases.phaseCurrent(SinglePhaseKind.B));
        assertEquals(SinglePhaseKind.C, tracedPhases.phaseCurrent(SinglePhaseKind.C));
        assertEquals(SinglePhaseKind.N, tracedPhases.phaseCurrent(SinglePhaseKind.N));

        /* -- Getting Direction-- */
        /* Normal */
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionNormal(SinglePhaseKind.A));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionNormal(SinglePhaseKind.B));
        assertEquals(PhaseDirection.OUT, tracedPhases.directionNormal(SinglePhaseKind.C));
        assertEquals(PhaseDirection.IN, tracedPhases.directionNormal(SinglePhaseKind.N));

        /* Current */
        assertEquals(PhaseDirection.IN, tracedPhases.directionCurrent(SinglePhaseKind.A));
        assertEquals(PhaseDirection.OUT, tracedPhases.directionCurrent(SinglePhaseKind.B));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionCurrent(SinglePhaseKind.C));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionCurrent(SinglePhaseKind.N));

        /* -- Adding -- */
        /* Normal */
        assertFalse(tracedPhases.addNormal(SinglePhaseKind.N, PhaseDirection.NONE, SinglePhaseKind.A));
        assertFalse(tracedPhases.addNormal(SinglePhaseKind.C, PhaseDirection.IN, SinglePhaseKind.B));
        assertTrue(tracedPhases.addNormal(SinglePhaseKind.B, PhaseDirection.IN, SinglePhaseKind.C));
        assertTrue(tracedPhases.addNormal(SinglePhaseKind.A, PhaseDirection.OUT, SinglePhaseKind.N));

        /* Current */
        assertTrue(tracedPhases.addCurrent(SinglePhaseKind.A, PhaseDirection.OUT, SinglePhaseKind.A));
        assertTrue(tracedPhases.addCurrent(SinglePhaseKind.B, PhaseDirection.IN, SinglePhaseKind.B));
        assertFalse(tracedPhases.addCurrent(SinglePhaseKind.C, PhaseDirection.NONE, SinglePhaseKind.C));
        assertFalse(tracedPhases.addCurrent(SinglePhaseKind.N, PhaseDirection.OUT, SinglePhaseKind.N));

        /* -- Getting Phase-- */
        /* Normal */
        assertEquals(SinglePhaseKind.N, tracedPhases.phaseNormal(SinglePhaseKind.A));
        assertEquals(SinglePhaseKind.C, tracedPhases.phaseNormal(SinglePhaseKind.B));
        assertEquals(SinglePhaseKind.B, tracedPhases.phaseNormal(SinglePhaseKind.C));
        assertEquals(SinglePhaseKind.A, tracedPhases.phaseNormal(SinglePhaseKind.N));

        /* Current */
        assertEquals(SinglePhaseKind.A, tracedPhases.phaseCurrent(SinglePhaseKind.A));
        assertEquals(SinglePhaseKind.B, tracedPhases.phaseCurrent(SinglePhaseKind.B));
        assertEquals(SinglePhaseKind.C, tracedPhases.phaseCurrent(SinglePhaseKind.C));
        assertEquals(SinglePhaseKind.N, tracedPhases.phaseCurrent(SinglePhaseKind.N));

        /* -- Getting Direction-- */
        /* Normal */
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionNormal(SinglePhaseKind.A));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionNormal(SinglePhaseKind.B));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionNormal(SinglePhaseKind.C));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionNormal(SinglePhaseKind.N));

        /* Current */
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionCurrent(SinglePhaseKind.A));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionCurrent(SinglePhaseKind.B));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionCurrent(SinglePhaseKind.C));
        assertEquals(PhaseDirection.BOTH, tracedPhases.directionCurrent(SinglePhaseKind.N));

        try {
            assertTrue(tracedPhases.addNormal(SinglePhaseKind.A, PhaseDirection.BOTH, SinglePhaseKind.A));
            fail();
        } catch (UnsupportedOperationException ignored) {
        }
    }

    @Test
    public void testRemoveDirection() {
        TracedPhases tracedPhases = newTestPhaseObject();

        tracedPhases.addNormal(SinglePhaseKind.A, PhaseDirection.OUT, SinglePhaseKind.A);
        tracedPhases.addNormal(SinglePhaseKind.B, PhaseDirection.IN, SinglePhaseKind.B);

        tracedPhases.addCurrent(SinglePhaseKind.B, PhaseDirection.IN, SinglePhaseKind.C);
        tracedPhases.addCurrent(SinglePhaseKind.A, PhaseDirection.OUT, SinglePhaseKind.N);

        tracedPhases.removeNormal(SinglePhaseKind.A, PhaseDirection.IN, SinglePhaseKind.A);
        tracedPhases.removeNormal(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.B);
        tracedPhases.removeNormal(SinglePhaseKind.C, PhaseDirection.BOTH, SinglePhaseKind.C);
        tracedPhases.removeNormal(SinglePhaseKind.N, PhaseDirection.BOTH, SinglePhaseKind.N);

        tracedPhases.removeCurrent(SinglePhaseKind.N, PhaseDirection.BOTH, SinglePhaseKind.A);
        tracedPhases.removeCurrent(SinglePhaseKind.C, PhaseDirection.BOTH, SinglePhaseKind.B);
        tracedPhases.removeCurrent(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.C);
        tracedPhases.removeCurrent(SinglePhaseKind.A, PhaseDirection.IN, SinglePhaseKind.N);

        assertEquals(PhaseDirection.OUT, tracedPhases.directionNormal(SinglePhaseKind.A));
        assertEquals(PhaseDirection.IN, tracedPhases.directionNormal(SinglePhaseKind.B));
        assertEquals(PhaseDirection.NONE, tracedPhases.directionNormal(SinglePhaseKind.C));
        assertEquals(PhaseDirection.NONE, tracedPhases.directionNormal(SinglePhaseKind.N));

        assertEquals(PhaseDirection.NONE, tracedPhases.directionCurrent(SinglePhaseKind.A));
        assertEquals(PhaseDirection.NONE, tracedPhases.directionCurrent(SinglePhaseKind.B));
        assertEquals(PhaseDirection.IN, tracedPhases.directionCurrent(SinglePhaseKind.C));
        assertEquals(PhaseDirection.OUT, tracedPhases.directionCurrent(SinglePhaseKind.N));

        assertTrue(tracedPhases.removeNormal(SinglePhaseKind.A, SinglePhaseKind.A));
        assertTrue(tracedPhases.removeNormal(SinglePhaseKind.B, SinglePhaseKind.B));
        assertFalse(tracedPhases.removeNormal(SinglePhaseKind.C, SinglePhaseKind.C));
        assertFalse(tracedPhases.removeNormal(SinglePhaseKind.N, SinglePhaseKind.N));

        assertFalse(tracedPhases.removeCurrent(SinglePhaseKind.N, SinglePhaseKind.A));
        assertFalse(tracedPhases.removeCurrent(SinglePhaseKind.C, SinglePhaseKind.B));
        assertTrue(tracedPhases.removeCurrent(SinglePhaseKind.B, SinglePhaseKind.C));
        assertTrue(tracedPhases.removeCurrent(SinglePhaseKind.A, SinglePhaseKind.N));

        assertEquals(PhaseDirection.NONE, tracedPhases.directionNormal(SinglePhaseKind.A));
        assertEquals(PhaseDirection.NONE, tracedPhases.directionNormal(SinglePhaseKind.B));
        assertEquals(PhaseDirection.NONE, tracedPhases.directionNormal(SinglePhaseKind.C));
        assertEquals(PhaseDirection.NONE, tracedPhases.directionNormal(SinglePhaseKind.N));

        assertEquals(PhaseDirection.NONE, tracedPhases.directionCurrent(SinglePhaseKind.A));
        assertEquals(PhaseDirection.NONE, tracedPhases.directionCurrent(SinglePhaseKind.B));
        assertEquals(PhaseDirection.NONE, tracedPhases.directionCurrent(SinglePhaseKind.C));
        assertEquals(PhaseDirection.NONE, tracedPhases.directionCurrent(SinglePhaseKind.N));
    }

    @Test
    public void testInvalidCoreNormal() {
        expect(() -> newTestPhaseObject().phaseNormal(SinglePhaseKind.INVALID))
            .toThrow(IllegalArgumentException.class);
    }

    @Test
    public void testCrossingPhasesExceptionNormal() {
        expect(() -> newTestPhaseObject().addNormal(SinglePhaseKind.B, PhaseDirection.BOTH, SinglePhaseKind.A))
            .toThrow(UnsupportedOperationException.class);
    }

    @Test
    public void testInvalidCoreCurrent() {
        expect(() -> newTestPhaseObject().phaseCurrent(SinglePhaseKind.INVALID))
            .toThrow(IllegalArgumentException.class);
    }

    @Test
    public void testCrossingPhasesExceptionCurrent() {
        expect(() -> newTestPhaseObject().addCurrent(SinglePhaseKind.B, PhaseDirection.BOTH, SinglePhaseKind.A))
            .toThrow(UnsupportedOperationException.class);
    }

    @Test
    public void removingSomethingNotPresentNormal() {
        TracedPhases tracedPhases = newTestPhaseObject();
        assertFalse(tracedPhases.removeNormal(SinglePhaseKind.B, PhaseDirection.OUT, SinglePhaseKind.A));
    }

    @Test
    public void removingSomethingNotPresentCurrent() {
        TracedPhases tracedPhases = newTestPhaseObject();
        assertFalse(tracedPhases.removeCurrent(SinglePhaseKind.A, PhaseDirection.OUT, SinglePhaseKind.N));
    }

}
