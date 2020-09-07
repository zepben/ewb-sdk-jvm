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
package com.zepben.cimbend.testdata;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.cimbend.cim.iec61970.base.core.Feeder;
import com.zepben.cimbend.cim.iec61970.base.core.PhaseCode;
import com.zepben.cimbend.cim.iec61970.base.core.Terminal;
import com.zepben.cimbend.cim.iec61970.base.wires.Junction;
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind;
import com.zepben.cimbend.network.NetworkService;
import com.zepben.cimbend.network.model.PhaseDirection;
import com.zepben.cimbend.network.tracing.PhaseStatus;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import static com.zepben.cimbend.testdata.TestDataCreators.createAcLineSegmentForConnecting;
import static com.zepben.cimbend.testdata.TestDataCreators.createNodeForConnecting;

@EverythingIsNonnullByDefault
public class DifferenceNetworks {

    public static NetworkService createSourceNetwork() {
        NetworkService network = new NetworkService();

        createAcLineSegmentForConnecting(network, "4", PhaseCode.A);
        createNodeForConnecting(network, "5", 1);
        createAcLineSegmentForConnecting(network, "6", PhaseCode.A);
        createNodeForConnecting(network, "7", 1);
        createAcLineSegmentForConnecting(network, "8", PhaseCode.A);
        createNodeForConnecting(network, "9", 1);
        createAcLineSegmentForConnecting(network, "10", PhaseCode.A);
        createNodeForConnecting(network, "11", 1);

        Junction n12 = createNodeForConnecting(network, "12", 1);
        Junction n13 = createNodeForConnecting(network, "13", 1);
        createNodeForConnecting(network, "14", 1);

        Junction n15 = createNodeForConnecting(network, "15", 1);
        Junction n16 = createNodeForConnecting(network, "16", 1);
        Junction n17 = createNodeForConnecting(network, "17", 1);
        Junction n18 = createNodeForConnecting(network, "18", 1);
        Junction n19 = createNodeForConnecting(network, "19", 1);
        Junction n20 = createNodeForConnecting(network, "20", 1);

        createFeeder(network, "f001", n12, Collections.singletonList(n13), Collections.singletonList(n13));
        createFeeder(network, "f002", n12, Collections.singletonList(n13), Collections.singletonList(n13));
        createFeeder(network, "f003", n12, Collections.singletonList(n13), Collections.singletonList(n13));
        createFeeder(network, "f004", n12, Collections.emptyList(), Collections.emptyList());
        createFeeder(network, "f006", n12, Collections.singletonList(n13), Collections.singletonList(n13));
        createFeeder(network, "f007", n12, Collections.singletonList(n13), Collections.singletonList(n13));

        setPhases(n15, Terminal::normalPhases, SinglePhaseKind.A, PhaseDirection.IN);
        setPhases(n16, Terminal::normalPhases, SinglePhaseKind.A, PhaseDirection.IN);
        setPhases(n17, Terminal::normalPhases, SinglePhaseKind.A, PhaseDirection.IN);
        setPhases(n18, Terminal::currentPhases, SinglePhaseKind.A, PhaseDirection.IN);
        setPhases(n19, Terminal::currentPhases, SinglePhaseKind.A, PhaseDirection.IN);
        setPhases(n20, Terminal::currentPhases, SinglePhaseKind.A, PhaseDirection.IN);

        return network;
    }

    private static void setPhases(Junction node, BiFunction<Terminal, SinglePhaseKind, PhaseStatus> phaseExtractor, SinglePhaseKind singlePhaseKind, PhaseDirection direction) {
        phaseExtractor.apply(node.getTerminal(1), SinglePhaseKind.A).set(singlePhaseKind, direction);
    }

    public static NetworkService createTargetNetwork() {
        NetworkService network = new NetworkService();

        createNodeForConnecting(network, "1", 1);
        createAcLineSegmentForConnecting(network, "2", PhaseCode.A);
        createNodeForConnecting(network, "3", 1);
        createAcLineSegmentForConnecting(network, "6", PhaseCode.AB);
        createNodeForConnecting(network, "7", 2);
        createAcLineSegmentForConnecting(network, "8", PhaseCode.AB);
        createNodeForConnecting(network, "9", 2);
        createAcLineSegmentForConnecting(network, "10", PhaseCode.A);
        createNodeForConnecting(network, "11", 1);

        Junction n12 = createNodeForConnecting(network, "12", 1);
        Junction n13 = createNodeForConnecting(network, "13", 1);
        Junction n14 = createNodeForConnecting(network, "14", 1);

        Junction n15 = createNodeForConnecting(network, "15", 1);
        Junction n16 = createNodeForConnecting(network, "16", 1);
        Junction n17 = createNodeForConnecting(network, "17", 1);
        Junction n18 = createNodeForConnecting(network, "18", 1);
        Junction n19 = createNodeForConnecting(network, "19", 1);
        Junction n20 = createNodeForConnecting(network, "20", 1);

        createFeeder(network, "f001", n12, Collections.singletonList(n13), Collections.singletonList(n13));
        createFeeder(network, "f002", n12, Collections.emptyList(), Collections.singletonList(n13));
        createFeeder(network, "f003", n12, Collections.singletonList(n13), Collections.emptyList());
        createFeeder(network, "f005", n12, Collections.emptyList(), Collections.emptyList());
        createFeeder(network, "f006", n12, Collections.singletonList(n14), Collections.singletonList(n13));
        createFeeder(network, "f007", n12, Collections.singletonList(n13), Collections.singletonList(n14));

        setPhases(n15, Terminal::normalPhases, SinglePhaseKind.A, PhaseDirection.IN);
        setPhases(n16, Terminal::normalPhases, SinglePhaseKind.B, PhaseDirection.IN);
        setPhases(n17, Terminal::normalPhases, SinglePhaseKind.A, PhaseDirection.OUT);
        setPhases(n18, Terminal::currentPhases, SinglePhaseKind.A, PhaseDirection.IN);
        setPhases(n19, Terminal::currentPhases, SinglePhaseKind.B, PhaseDirection.IN);
        setPhases(n20, Terminal::currentPhases, SinglePhaseKind.A, PhaseDirection.OUT);

        return network;
    }

    private static void createFeeder(NetworkService network, String feederName, ConductingEquipment startAsset, List<ConductingEquipment> normalAssets, List<ConductingEquipment> currentAssets) {
        Feeder feeder = new Feeder(feederName);
        feeder.setNormalHeadTerminal(startAsset.getTerminal(1));
        feeder.setName(feederName);

        normalAssets.forEach(feeder::addEquipment);
        currentAssets.forEach(feeder::addCurrentEquipment);

        network.add(feeder);
    }

}
