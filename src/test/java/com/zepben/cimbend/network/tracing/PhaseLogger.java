/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network.tracing;

import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind;
import com.zepben.traversals.Traversal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BiConsumer;

// Logs all the phases of assets, terminal and cores. Useful for debugging.
class PhaseLogger implements BiConsumer<ConductingEquipment, Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(PhaseLogger.class);

    static void trace(ConductingEquipment asset) {
        trace(Collections.singletonList(asset));
    }

    static void trace(Collection<? extends ConductingEquipment> assets) {
        assets.forEach(asset -> {
            PhaseLogger pl = new PhaseLogger(asset);

            Traversal<ConductingEquipment> trace = Tracing.connectedEquipmentTrace();
            trace.addStepAction(pl);

            trace.run(asset);
            pl.log();
        });
    }

    private final StringBuilder b;

    @Override
    public void accept(ConductingEquipment a, Boolean isStopping) {
        a.getTerminals().forEach((t) -> {
            b.append(a.getMRID()).append("-T").append(t.getSequenceNumber()).append(": ");

            for (SinglePhaseKind phase : t.getPhases().singlePhases()) {
                b.append("{").append(phase).append(": n:");

                PhaseStatus ps = t.normalPhases(phase);
                b.append(ps.phase().toString()).append(":").append(ps.direction().toString());
                b.append(", c:");

                ps = t.currentPhases(phase);
                b.append(ps.phase().toString()).append(":").append(ps.direction().toString());
                b.append("}, ");
            }
            clearLastComma(b);
            b.append("\n");
        });
    }

    private void clearLastComma(StringBuilder b) {
        int index = b.lastIndexOf(",");
        if (index != -1)
            b.delete(index, b.length());
    }

    private void log() {
        logger.info(b.toString());
    }

    private PhaseLogger(ConductingEquipment asset) {
        b = new StringBuilder()
            .append("\n###############################")
            .append("\nTracing from: ").append(asset.getName()).append(" [").append(asset.getMRID()).append("]")
            .append("\n")
            .append("\n");
    }

}
