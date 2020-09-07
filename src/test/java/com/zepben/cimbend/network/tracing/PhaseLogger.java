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
