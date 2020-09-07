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

import com.zepben.cimbend.cim.iec61970.base.core.*;
import com.zepben.cimbend.cim.iec61970.base.wires.*;
import com.zepben.cimbend.network.NetworkService;
import com.zepben.cimbend.network.tracing.Tracing;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static com.zepben.cimbend.testdata.TestDataCreators.*;

/**
 * Class that generates some networks for testing with.
 */
@SuppressWarnings({"WeakerAccess", "SameParameterValue", "ConstantConditions"})
public class TestNetworks {

    private static final Map<Integer, Supplier<NetworkService>> networksSupplier = new HashMap<>();

    static {
        networksSupplier.put(1, TestNetworks::testNetwork1);
        networksSupplier.put(2, TestNetworks::testNetwork2);
        networksSupplier.put(3, TestNetworks::testNetwork3);
        networksSupplier.put(4, TestNetworks::testNetwork4);
        networksSupplier.put(5, TestNetworks::testNetwork5);
        networksSupplier.put(6, TestNetworks::testNetwork6);
        networksSupplier.put(7, TestNetworks::feederBreakerAsHeadNetwork);
        networksSupplier.put(8, TestNetworks::testNetwork8);
        networksSupplier.put(9, TestNetworks::missingPhaseNetwork);
    }

    public static NetworkService getNetwork(int id) {
        Supplier<NetworkService> n = networksSupplier.get(id);
        if (n != null)
            return n.get();

        throw new IllegalArgumentException("Network with not found. ID:" + id);
    }

    //
    //  n0 ac0        ac1     n1   ac2        ac3   n2
    //  *==ABCN==+====ABCN====*====ABCN====+==ABCN==*
    //           |                         |
    //       ac4 AB                        BC ac9
    //           |                         |
    //        n3 *                         * n7
    //           |                         |
    //       ac5 XY                        XY ac8
    //           |            n5           |
    //        n4 *-----XY-----*-----XY-----* n6 (open)
    //           |     ac6    |     ac7
    //      ac10 X            Y ac11
    //           |            |
    //        n8 *            * n9
    //
    private static NetworkService testNetwork1() {
        NetworkService network = new NetworkService();
        EnergySource node0 = createSourceForConnecting(network, "node0", 1, PhaseCode.ABCN);
        Junction node1 = createNodeForConnecting(network, "node1", 2, PhaseCode.ABCN);
        Junction node2 = createNodeForConnecting(network, "node2", 1, PhaseCode.ABCN);
        Junction node3 = createNodeForConnecting(network, "node3", 2, PhaseCode.AB);
        Junction node4 = createNodeForConnecting(network, "node4", 3, PhaseCode.XY);
        Junction node5 = createNodeForConnecting(network, "node5", 3, PhaseCode.XY);
        Breaker node6 = createSwitchForConnecting(network, "node6", 2, PhaseCode.XY, true, true);
        Junction node7 = createNodeForConnecting(network, "node7", 2, PhaseCode.BC);
        Junction node8 = createNodeForConnecting(network, "node8", 1, PhaseCode.X);
        Junction node9 = createNodeForConnecting(network, "node9", 1, PhaseCode.Y);

        AcLineSegment acLineSegment0 = createAcLineSegmentForConnecting(network, "acLineSegment0", PhaseCode.ABCN);
        AcLineSegment acLineSegment1 = createAcLineSegmentForConnecting(network, "acLineSegment1", PhaseCode.ABCN);
        AcLineSegment acLineSegment2 = createAcLineSegmentForConnecting(network, "acLineSegment2", PhaseCode.ABCN);
        AcLineSegment acLineSegment3 = createAcLineSegmentForConnecting(network, "acLineSegment3", PhaseCode.ABCN);
        AcLineSegment acLineSegment4 = createAcLineSegmentForConnecting(network, "acLineSegment4", PhaseCode.AB);
        AcLineSegment acLineSegment5 = createAcLineSegmentForConnecting(network, "acLineSegment5", PhaseCode.XY);
        AcLineSegment acLineSegment6 = createAcLineSegmentForConnecting(network, "acLineSegment6", PhaseCode.XY);
        AcLineSegment acLineSegment7 = createAcLineSegmentForConnecting(network, "acLineSegment7", PhaseCode.XY);
        AcLineSegment acLineSegment8 = createAcLineSegmentForConnecting(network, "acLineSegment8", PhaseCode.XY);
        AcLineSegment acLineSegment9 = createAcLineSegmentForConnecting(network, "acLineSegment9", PhaseCode.BC);
        AcLineSegment acLineSegment10 = createAcLineSegmentForConnecting(network, "acLineSegment10", PhaseCode.X);
        AcLineSegment acLineSegment11 = createAcLineSegmentForConnecting(network, "acLineSegment11", PhaseCode.Y);

        // Connect up a network so we can check connectivity.
        network.connect(node0.getTerminal(1), "cn_0");
        network.connect(acLineSegment0.getTerminal(1), "cn_0");
        network.connect(acLineSegment0.getTerminal(2), "cn_1");
        network.connect(acLineSegment1.getTerminal(1), "cn_1");
        network.connect(acLineSegment1.getTerminal(2), "cn_2");
        network.connect(node1.getTerminal(1), "cn_2");
        network.connect(node1.getTerminal(2), "cn_3");
        network.connect(acLineSegment2.getTerminal(1), "cn_3");
        network.connect(acLineSegment2.getTerminal(2), "cn_4");
        network.connect(acLineSegment3.getTerminal(1), "cn_4");
        network.connect(acLineSegment3.getTerminal(2), "cn_5");
        network.connect(node2.getTerminal(1), "cn_5");
        network.connect(acLineSegment4.getTerminal(1), "cn_1");
        network.connect(acLineSegment4.getTerminal(2), "cn_6");
        network.connect(node3.getTerminal(1), "cn_6");
        network.connect(node3.getTerminal(2), "cn_7");
        network.connect(acLineSegment5.getTerminal(1), "cn_7");
        network.connect(acLineSegment5.getTerminal(2), "cn_8");
        network.connect(node4.getTerminal(1), "cn_8");
        network.connect(node4.getTerminal(2), "cn_9");
        network.connect(node4.getTerminal(3), "cn_16");
        network.connect(acLineSegment6.getTerminal(1), "cn_9");
        network.connect(acLineSegment6.getTerminal(2), "cn_10");
        network.connect(node5.getTerminal(1), "cn_10");
        network.connect(node5.getTerminal(2), "cn_11");
        network.connect(node5.getTerminal(3), "cn_18");
        network.connect(acLineSegment7.getTerminal(1), "cn_11");
        network.connect(acLineSegment7.getTerminal(2), "cn_12");
        network.connect(node6.getTerminal(1), "cn_12");
        network.connect(node6.getTerminal(2), "cn_13");
        network.connect(acLineSegment8.getTerminal(1), "cn_13");
        network.connect(acLineSegment8.getTerminal(2), "cn_14");
        network.connect(node7.getTerminal(1), "cn_14");
        network.connect(node7.getTerminal(2), "cn_15");
        network.connect(acLineSegment9.getTerminal(1), "cn_15");
        network.connect(acLineSegment9.getTerminal(2), "cn_4");
        network.connect(acLineSegment10.getTerminal(1), "cn_16");
        network.connect(acLineSegment10.getTerminal(2), "cn_17");
        network.connect(node8.getTerminal(1), "cn_17");
        network.connect(acLineSegment11.getTerminal(1), "cn_18");
        network.connect(acLineSegment11.getTerminal(2), "cn_19");
        network.connect(node9.getTerminal(1), "cn_19");

        return network;
    }

    //
    //          | ac2
    //          A
    //  ac1     |/---B--- ac3
    //  ==ABCN==*----N--- ac4
    //          | n1
    //          CN
    //          | ac5
    //
    private static NetworkService testNetwork2() {
        NetworkService network = new NetworkService();
        Junction node1 = createNodeForConnecting(network, "node1", 5, PhaseCode.ABCN);
        AcLineSegment acLineSegment1 = createAcLineSegmentForConnecting(network, "acLineSegment1", PhaseCode.ABCN);
        AcLineSegment acLineSegment2 = createAcLineSegmentForConnecting(network, "acLineSegment2", PhaseCode.A);
        AcLineSegment acLineSegment3 = createAcLineSegmentForConnecting(network, "acLineSegment3", PhaseCode.B);
        AcLineSegment acLineSegment4 = createAcLineSegmentForConnecting(network, "acLineSegment4", PhaseCode.N);
        AcLineSegment acLineSegment5 = createAcLineSegmentForConnecting(network, "acLineSegment5", PhaseCode.CN);

        network.connect(node1.getTerminal(1), "cn_1");
        network.connect(node1.getTerminal(2), "cn_2");
        network.connect(node1.getTerminal(3), "cn_3");
        network.connect(node1.getTerminal(4), "cn_4");
        network.connect(node1.getTerminal(5), "cn_5");

        network.connect(acLineSegment1.getTerminal(1), "cn_1");
        network.connect(acLineSegment2.getTerminal(1), "cn_2");
        network.connect(acLineSegment3.getTerminal(1), "cn_3");
        network.connect(acLineSegment4.getTerminal(1), "cn_4");
        network.connect(acLineSegment5.getTerminal(1), "cn_5");

        return network;
    }

    //
    //          | ac2
    //          AB
    //  ac1     |     ac3
    //  ==ABCN==+---AC---
    //          |
    //          BC
    //          | ac4
    //
    private static NetworkService testNetwork3() {
        NetworkService network = new NetworkService();
        AcLineSegment acLineSegment1 = createAcLineSegmentForConnecting(network, "acLineSegment1", PhaseCode.ABCN);
        AcLineSegment acLineSegment2 = createAcLineSegmentForConnecting(network, "acLineSegment2", PhaseCode.AB);
        AcLineSegment acLineSegment3 = createAcLineSegmentForConnecting(network, "acLineSegment3", PhaseCode.AC);
        AcLineSegment acLineSegment4 = createAcLineSegmentForConnecting(network, "acLineSegment4", PhaseCode.BC);

        network.connect(acLineSegment1.getTerminal(1), "cn_1");
        network.connect(acLineSegment2.getTerminal(1), "cn_1");
        network.connect(acLineSegment3.getTerminal(1), "cn_1");
        network.connect(acLineSegment4.getTerminal(1), "cn_1");

        return network;
    }

    //
    // n0  ac0  n1  ac1  n2  ac2  n3
    // *--------*--------*--------*
    //          |                 |
    //      ac3 |                 | ac4
    //          |  ac5            |
    //       n4 *--------* n5     * n6 (open)
    //          |                 |
    //      ac6 |                 | ac7
    //          |  ac8   n8  ac9  |
    //       n7 *--------*--------* n9
    //          |
    //          |  ac11
    //          |   /----* n11
    //     ac10 |  /     | ac13
    //          | /      |
    //          |/  ac12 |  ac14
    //      n10 *--------*--------* n13
    //           \       |n12
    //            \      |
    //             \     | ac16
    //              \----* n14
    //             ac15
    //
    private static NetworkService testNetwork4() {
        NetworkService network = new NetworkService();
        EnergySource node0 = createSourceForConnecting(network, "node0", 1, PhaseCode.ABCN);
        Junction node1 = createNodeForConnecting(network, "node1", 3, PhaseCode.ABCN);
        Junction node2 = createNodeForConnecting(network, "node2", 2, PhaseCode.ABCN);
        Junction node3 = createNodeForConnecting(network, "node3", 2, PhaseCode.ABCN);
        Junction node4 = createNodeForConnecting(network, "node4", 3, PhaseCode.ABCN);
        Junction node5 = createNodeForConnecting(network, "node5", 1, PhaseCode.ABCN);
        Breaker node6 = createSwitchForConnecting(network, "node6", 2, PhaseCode.ABCN, true, true, true, true);
        Junction node7 = createNodeForConnecting(network, "node7", 3, PhaseCode.ABCN);
        Junction node8 = createNodeForConnecting(network, "node8", 2, PhaseCode.ABCN);
        Junction node9 = createNodeForConnecting(network, "node9", 2, PhaseCode.ABCN);
        Junction node10 = createNodeForConnecting(network, "node10", 3, PhaseCode.ABCN);
        Junction node11 = createNodeForConnecting(network, "node11", 2, PhaseCode.ABCN);
        Junction node12 = createNodeForConnecting(network, "node12", 3, PhaseCode.ABCN);
        Junction node13 = createNodeForConnecting(network, "node13", 1, PhaseCode.ABCN);
        Junction node14 = createNodeForConnecting(network, "node14", 2, PhaseCode.ABCN);

        AcLineSegment acLineSegment0 = createAcLineSegmentForConnecting(network, "acLineSegment0", PhaseCode.ABCN, 10000.0, "abc");
        AcLineSegment acLineSegment1 = createAcLineSegmentForConnecting(network, "acLineSegment1", PhaseCode.ABCN, 2000.0, "abc");
        AcLineSegment acLineSegment2 = createAcLineSegmentForConnecting(network, "acLineSegment2", PhaseCode.ABCN, 500.0, "abc");
        AcLineSegment acLineSegment3 = createAcLineSegmentForConnecting(network, "acLineSegment3", PhaseCode.ABCN, 100.0, "abc");
        AcLineSegment acLineSegment4 = createAcLineSegmentForConnecting(network, "acLineSegment4", PhaseCode.ABCN, 10.0, "abc");
        AcLineSegment acLineSegment5 = createAcLineSegmentForConnecting(network, "acLineSegment5", PhaseCode.ABCN, 1.0, "abc");
        AcLineSegment acLineSegment6 = createAcLineSegmentForConnecting(network, "acLineSegment6", PhaseCode.ABCN, 1000.0, "abc");
        AcLineSegment acLineSegment7 = createAcLineSegmentForConnecting(network, "acLineSegment7", PhaseCode.ABCN, 10.0, "abc");
        AcLineSegment acLineSegment8 = createAcLineSegmentForConnecting(network, "acLineSegment8", PhaseCode.ABCN, 750.0, "abc");
        AcLineSegment acLineSegment9 = createAcLineSegmentForConnecting(network, "acLineSegment9", PhaseCode.ABCN, 3025.0, "abc");
        AcLineSegment acLineSegment10 = createAcLineSegmentForConnecting(network, "acLineSegment10", PhaseCode.ABCN, 100.0, "abc");
        AcLineSegment acLineSegment11 = createAcLineSegmentForConnecting(network, "acLineSegment11", PhaseCode.ABCN, 100.0, "abc");
        AcLineSegment acLineSegment12 = createAcLineSegmentForConnecting(network, "acLineSegment12", PhaseCode.ABCN, 100.0, "abc");
        AcLineSegment acLineSegment13 = createAcLineSegmentForConnecting(network, "acLineSegment13", PhaseCode.ABCN, 100.0, "abc");
        AcLineSegment acLineSegment14 = createAcLineSegmentForConnecting(network, "acLineSegment14", PhaseCode.ABCN, 100.0, "abc");
        AcLineSegment acLineSegment15 = createAcLineSegmentForConnecting(network, "acLineSegment15", PhaseCode.ABCN, 100.0, "abc");
        AcLineSegment acLineSegment16 = createAcLineSegmentForConnecting(network, "acLineSegment16", PhaseCode.ABCN, 100.0, "abc");

        // Connect up a network so we can check connectivity.
        network.connect(node0.getTerminal(1), acLineSegment0.getTerminal(1));
        network.connect(node1.getTerminal(1), acLineSegment0.getTerminal(2));
        network.connect(node1.getTerminal(2), acLineSegment1.getTerminal(1));
        network.connect(node1.getTerminal(3), acLineSegment3.getTerminal(1));
        network.connect(node2.getTerminal(1), acLineSegment1.getTerminal(2));
        network.connect(node2.getTerminal(2), acLineSegment2.getTerminal(1));
        network.connect(node3.getTerminal(1), acLineSegment2.getTerminal(2));
        network.connect(node3.getTerminal(2), acLineSegment4.getTerminal(1));
        network.connect(node4.getTerminal(1), acLineSegment3.getTerminal(2));
        network.connect(node4.getTerminal(2), acLineSegment5.getTerminal(1));
        network.connect(node4.getTerminal(3), acLineSegment6.getTerminal(1));
        network.connect(node5.getTerminal(1), acLineSegment5.getTerminal(2));
        network.connect(node6.getTerminal(1), acLineSegment4.getTerminal(2));
        network.connect(node6.getTerminal(2), acLineSegment7.getTerminal(1));
        network.connect(node7.getTerminal(1), acLineSegment6.getTerminal(2));
        network.connect(node7.getTerminal(2), acLineSegment8.getTerminal(1));
        network.connect(node7.getTerminal(3), acLineSegment10.getTerminal(1));
        network.connect(node8.getTerminal(1), acLineSegment8.getTerminal(2));
        network.connect(node8.getTerminal(2), acLineSegment9.getTerminal(1));
        network.connect(node9.getTerminal(1), acLineSegment9.getTerminal(2));
        network.connect(node9.getTerminal(2), acLineSegment7.getTerminal(2));
        network.connect(node10.getTerminal(1), acLineSegment10.getTerminal(2));
        network.connect(node10.getTerminal(2), acLineSegment11.getTerminal(1));
        network.connect(node10.getTerminal(3), acLineSegment12.getTerminal(1));
        network.connect(node10.getTerminal(3), acLineSegment15.getTerminal(1));
        network.connect(node11.getTerminal(1), acLineSegment11.getTerminal(2));
        network.connect(node11.getTerminal(2), acLineSegment13.getTerminal(1));
        network.connect(node12.getTerminal(1), acLineSegment12.getTerminal(2));
        network.connect(node12.getTerminal(1), acLineSegment16.getTerminal(2));
        network.connect(node12.getTerminal(2), acLineSegment13.getTerminal(2));
        network.connect(node12.getTerminal(3), acLineSegment14.getTerminal(1));
        network.connect(node13.getTerminal(1), acLineSegment14.getTerminal(2));
        network.connect(node14.getTerminal(1), acLineSegment15.getTerminal(2));
        network.connect(node14.getTerminal(2), acLineSegment16.getTerminal(1));

        return network;
    }

    //     n0     c0     n1     c1     n2
    // A  ----00======10-/ -10======10---- A
    // B  ----00======10----10======10---- B
    // C  ----00======10-/ -10======10---- C
    // N  ----00======10----10======10---- N
    //
    private static NetworkService testNetwork5() {
        NetworkService network = new NetworkService();
        EnergySource node0 = createSourceForConnecting(network, "node0", 1, PhaseCode.ABCN);
        Breaker node1 = createSwitchForConnecting(network, "node1", 2, PhaseCode.ABCN, true, false, true, false);
        EnergySource node2 = createSourceForConnecting(network, "node2", 1, PhaseCode.ABCN);

        AcLineSegment acLineSegment0 = createAcLineSegmentForConnecting(network, "acLineSegment0", PhaseCode.ABCN);
        AcLineSegment acLineSegment1 = createAcLineSegmentForConnecting(network, "acLineSegment1", PhaseCode.ABCN);

        // Connect up a network so we can check connectivity.
        network.connect(node0.getTerminal(1), acLineSegment0.getTerminal(1));
        network.connect(node1.getTerminal(1), acLineSegment0.getTerminal(2));
        network.connect(node1.getTerminal(2), acLineSegment1.getTerminal(1));
        network.connect(node2.getTerminal(1), acLineSegment1.getTerminal(2));

        return network;
    }

    //     n0     c0     n1     c1     n2     c2     n3     c3     n4
    // A  ----00======10----10======10-/ -10======10----10======10----
    // B  ----00======10----10======10----10======10----10======10----
    // C  ----00======10----10======10-/ -10======10----10======10----
    // N  ----00======10----10======10----10======10----10======10----
    //
    private static NetworkService testNetwork6() {
        NetworkService network = new NetworkService();
        EnergySource node0 = createSourceForConnecting(network, "node0", 1, PhaseCode.ABCN);
        Junction node1 = createNodeForConnecting(network, "node1", 2, PhaseCode.ABCN);
        Breaker node2 = createSwitchForConnecting(network, "node2", 2, PhaseCode.ABCN, true, false, true, false);
        Junction node3 = createNodeForConnecting(network, "node3", 2, PhaseCode.ABCN);
        EnergySource node4 = createSourceForConnecting(network, "node4", 1, PhaseCode.ABCN);

        AcLineSegment acLineSegment0 = createAcLineSegmentForConnecting(network, "acLineSegment0", PhaseCode.ABCN);
        AcLineSegment acLineSegment1 = createAcLineSegmentForConnecting(network, "acLineSegment1", PhaseCode.ABCN);
        AcLineSegment acLineSegment2 = createAcLineSegmentForConnecting(network, "acLineSegment2", PhaseCode.ABCN);
        AcLineSegment acLineSegment3 = createAcLineSegmentForConnecting(network, "acLineSegment3", PhaseCode.ABCN);

        // Connect up a network so we can check connectivity.
        network.connect(node0.getTerminal(1), acLineSegment0.getTerminal(1));
        network.connect(node1.getTerminal(1), acLineSegment0.getTerminal(2));
        network.connect(node1.getTerminal(2), acLineSegment1.getTerminal(1));
        network.connect(node2.getTerminal(1), acLineSegment1.getTerminal(2));
        network.connect(node2.getTerminal(2), acLineSegment2.getTerminal(1));
        network.connect(node3.getTerminal(1), acLineSegment2.getTerminal(2));
        network.connect(node3.getTerminal(2), acLineSegment3.getTerminal(1));
        network.connect(node4.getTerminal(1), acLineSegment3.getTerminal(2));

        return network;
    }

    //
    // -------fcb-------n1
    //    c1        c2
    //
    private static NetworkService feederBreakerAsHeadNetwork() {
        NetworkService network = new NetworkService();

        GeographicalRegion geographicalRegion = new GeographicalRegion("b");
        geographicalRegion.setName("b");
        SubGeographicalRegion subGeographicalRegion = new SubGeographicalRegion("r");
        subGeographicalRegion.setName("r");
        subGeographicalRegion.setGeographicalRegion(geographicalRegion);
        Substation substation = new Substation("z");
        substation.setName("z");
        substation.setSubGeographicalRegion(subGeographicalRegion);

        EnergySource energySource = createSourceForConnecting(network, "source", 1, PhaseCode.A);
        Breaker fcb = createSwitchForConnecting(network, "fcb", 2, PhaseCode.A, false);
        Junction n1 = createNodeForConnecting(network, "n1", 1, PhaseCode.A);

        AcLineSegment c1 = createAcLineSegmentForConnecting(network, "c1", PhaseCode.A);
        AcLineSegment c2 = createAcLineSegmentForConnecting(network, "c2", PhaseCode.A);

        fcb.addContainer(substation);
        substation.addEquipment(fcb);

        // Connect up a network so we can check connectivity.
        network.connect(energySource.getTerminal(1), c1.getTerminal(1));
        network.connect(fcb.getTerminal(1), c1.getTerminal(2));
        network.connect(fcb.getTerminal(2), c2.getTerminal(1));
        network.connect(n1.getTerminal(1), c2.getTerminal(2));

        Feeder feeder = new Feeder("f");
        feeder.setNormalHeadTerminal(fcb.getTerminal(2));
        feeder.setName("f");
        feeder.setNormalEnergizingSubstation(substation);

        geographicalRegion.addSubGeographicalRegion(subGeographicalRegion);
        subGeographicalRegion.addSubstation(substation);
        substation.addFeeder(feeder);

        network.add(geographicalRegion);
        network.add(subGeographicalRegion);
        network.add(substation);
        network.add(feeder);

        return network;
    }

    //
    //        n3
    //        |
    //        A c3
    //        |
    // n1--AB-+--AB-n2
    //    c1  |  c2
    //        |
    //        AB c4
    //        |
    //        n4
    //
    private static NetworkService testNetwork8() {
        NetworkService network = new NetworkService();
        EnergySource n1 = createSourceForConnecting(network, "n1", 1, PhaseCode.AB);
        EnergySource n2 = createSourceForConnecting(network, "n2", 1, PhaseCode.AB);
        EnergySource n3 = createSourceForConnecting(network, "n3", 1, PhaseCode.A);
        Junction n4 = createNodeForConnecting(network, "n4", 2, PhaseCode.AB);
        AcLineSegment c1 = createAcLineSegmentForConnecting(network, "c1", PhaseCode.AB);
        AcLineSegment c2 = createAcLineSegmentForConnecting(network, "c2", PhaseCode.AB);
        AcLineSegment c3 = createAcLineSegmentForConnecting(network, "c3", PhaseCode.A);
        AcLineSegment c4 = createAcLineSegmentForConnecting(network, "c4", PhaseCode.AB);

        network.connect(n1.getTerminal(1), c1.getTerminal(1));
        network.connect(c2.getTerminal(1), c1.getTerminal(2));
        network.connect(n2.getTerminal(1), c2.getTerminal(2));
        network.connect(n3.getTerminal(1), c3.getTerminal(1));
        network.connect(c1.getTerminal(2), c3.getTerminal(2));
        network.connect(c1.getTerminal(2), c4.getTerminal(1));
        network.connect(n4.getTerminal(1), c4.getTerminal(2));

        Tracing.setPhases().run(network);

        return network;
    }

    //     c0   c1   c2   c3   c4   c5   c6   c7   c8
    // A  ----*----*----*----*----*----*----*----*----
    // B  ----*----*----*----*         *----*    *----
    // C  ----*    *----*----*----*              *----
    private static NetworkService missingPhaseNetwork() {
        NetworkService network = new NetworkService();

        EnergySource source = createSourceForConnecting(network, "source", 1, PhaseCode.ABC);
        AcLineSegment c0 = createAcLineSegmentForConnecting(network, "c0", PhaseCode.ABC);
        AcLineSegment c1 = createAcLineSegmentForConnecting(network, "c1", PhaseCode.AB);
        AcLineSegment c2 = createAcLineSegmentForConnecting(network, "c2", PhaseCode.ABC);
        AcLineSegment c3 = createAcLineSegmentForConnecting(network, "c3", PhaseCode.ABC);
        AcLineSegment c4 = createAcLineSegmentForConnecting(network, "c4", PhaseCode.AC);
        AcLineSegment c5 = createAcLineSegmentForConnecting(network, "c5", PhaseCode.A);
        AcLineSegment c6 = createAcLineSegmentForConnecting(network, "c6", PhaseCode.AB);
        AcLineSegment c7 = createAcLineSegmentForConnecting(network, "c7", PhaseCode.A);
        AcLineSegment c8 = createAcLineSegmentForConnecting(network, "c8", PhaseCode.ABC);

        createTerminals(network, source, 1, PhaseCode.ABC);

        network.connect(source.getTerminal(1), c0.getTerminal(1));
        network.connect(c0.getTerminal(2), c1.getTerminal(1));
        network.connect(c1.getTerminal(2), c2.getTerminal(1));
        network.connect(c2.getTerminal(2), c3.getTerminal(1));
        network.connect(c3.getTerminal(2), c4.getTerminal(1));
        network.connect(c4.getTerminal(2), c5.getTerminal(1));
        network.connect(c5.getTerminal(2), c6.getTerminal(1));
        network.connect(c6.getTerminal(2), c7.getTerminal(1));
        network.connect(c7.getTerminal(2), c8.getTerminal(1));

        Tracing.setPhases().run(network);

        return network;
    }

    //
    //      c0   c1   c2             c3   c4   c5
    //  es ----+----+----+- sw1 iso ----+----+---- tx1
    //         |    |    |              |    |
    //         |c6  |c7  |c8            |c9  |c10
    //         |    |    |              |    |
    //        tx2  tx3   |             tx4  tx5
    //                   |
    //                   |       c11  c12  c13
    //                   +- sw2 ----+----+---- tx6
    //                              |    |
    //                              |c14 |c15
    //                              |    |
    //                             tx7  tx8
    //
    // sw1: normally close, currently open
    // sw2: normally open, currently closed
    //
    public static NetworkService withUsagePointsNetwork() {
        NetworkService network = new NetworkService();

        EnergySource es = createSourceForConnecting(network, "es", 1, PhaseCode.A);
        PowerTransformer iso = createPowerTransformerForConnecting(network, "iso", 2, PhaseCode.A, 1, 2);
        Breaker sw1 = createSwitchForConnecting(network, "sw1", 2, PhaseCode.A);
        Breaker sw2 = createSwitchForConnecting(network, "sw2", 2, PhaseCode.A, true);
        PowerTransformer tx1 = createPowerTransformerForConnecting(network, "tx1", 1, PhaseCode.A, 3, 3);
        PowerTransformer tx2 = createPowerTransformerForConnecting(network, "tx2", 1, PhaseCode.A, 3, 1);
        PowerTransformer tx3 = createPowerTransformerForConnecting(network, "tx3", 1, PhaseCode.A, 3, 1);
        PowerTransformer tx4 = createPowerTransformerForConnecting(network, "tx4", 1, PhaseCode.A, 3, 1);
        PowerTransformer tx5 = createPowerTransformerForConnecting(network, "tx5", 1, PhaseCode.A, 1, 7);
        PowerTransformer tx6 = createPowerTransformerForConnecting(network, "tx6", 1, PhaseCode.A, 3, 1);
        PowerTransformer tx7 = createPowerTransformerForConnecting(network, "tx7", 1, PhaseCode.A, 2, 1);
        PowerTransformer tx8 = createPowerTransformerForConnecting(network, "tx8", 1, PhaseCode.A, 1, 1);
        AcLineSegment c0 = createAcLineSegmentForConnecting(network, "c0", PhaseCode.A);
        AcLineSegment c1 = createAcLineSegmentForConnecting(network, "c1", PhaseCode.A);
        AcLineSegment c2 = createAcLineSegmentForConnecting(network, "c2", PhaseCode.A);
        AcLineSegment c3 = createAcLineSegmentForConnecting(network, "c3", PhaseCode.A);
        AcLineSegment c4 = createAcLineSegmentForConnecting(network, "c4", PhaseCode.A);
        AcLineSegment c5 = createAcLineSegmentForConnecting(network, "c5", PhaseCode.A);
        AcLineSegment c6 = createAcLineSegmentForConnecting(network, "c6", PhaseCode.A);
        AcLineSegment c7 = createAcLineSegmentForConnecting(network, "c7", PhaseCode.A);
        AcLineSegment c8 = createAcLineSegmentForConnecting(network, "c8", PhaseCode.A);
        AcLineSegment c9 = createAcLineSegmentForConnecting(network, "c9", PhaseCode.A);
        AcLineSegment c10 = createAcLineSegmentForConnecting(network, "c10", PhaseCode.A);
        AcLineSegment c11 = createAcLineSegmentForConnecting(network, "c11", PhaseCode.A);
        AcLineSegment c12 = createAcLineSegmentForConnecting(network, "c12", PhaseCode.A);
        AcLineSegment c13 = createAcLineSegmentForConnecting(network, "c13", PhaseCode.A);
        AcLineSegment c14 = createAcLineSegmentForConnecting(network, "c14", PhaseCode.A);
        AcLineSegment c15 = createAcLineSegmentForConnecting(network, "c15", PhaseCode.A);

        sw1.setOpen(true, SinglePhaseKind.A);
        sw2.setOpen(false, SinglePhaseKind.A);

        network.connect(es.getTerminal(1), c0.getTerminal(1));
        network.connect(c0.getTerminal(2), c1.getTerminal(1));
        network.connect(c1.getTerminal(2), c2.getTerminal(1));
        network.connect(c2.getTerminal(2), sw1.getTerminal(1));
        network.connect(sw1.getTerminal(2), iso.getTerminal(1));
        network.connect(iso.getTerminal(2), c3.getTerminal(1));
        network.connect(c3.getTerminal(2), c4.getTerminal(1));
        network.connect(c4.getTerminal(2), c5.getTerminal(1));
        network.connect(c5.getTerminal(2), tx1.getTerminal(1));
        network.connect(c6.getTerminal(2), tx2.getTerminal(1));
        network.connect(c7.getTerminal(2), tx3.getTerminal(1));
        network.connect(c8.getTerminal(2), sw2.getTerminal(1));
        network.connect(c9.getTerminal(2), tx4.getTerminal(1));
        network.connect(c10.getTerminal(2), tx5.getTerminal(1));
        network.connect(sw2.getTerminal(2), c11.getTerminal(1));
        network.connect(c11.getTerminal(2), c12.getTerminal(1));
        network.connect(c12.getTerminal(2), c13.getTerminal(1));
        network.connect(c13.getTerminal(2), tx6.getTerminal(1));
        network.connect(c14.getTerminal(2), tx7.getTerminal(1));
        network.connect(c15.getTerminal(2), tx8.getTerminal(1));

        network.connect(c6.getTerminal(1), Objects.requireNonNull(c0.getTerminal(2).connectivityNodeId()));
        network.connect(c7.getTerminal(1), Objects.requireNonNull(c1.getTerminal(2).connectivityNodeId()));
        network.connect(c8.getTerminal(1), Objects.requireNonNull(c2.getTerminal(2).connectivityNodeId()));
        network.connect(c9.getTerminal(1), Objects.requireNonNull(c3.getTerminal(2).connectivityNodeId()));
        network.connect(c10.getTerminal(1), Objects.requireNonNull(c4.getTerminal(2).connectivityNodeId()));
        network.connect(c14.getTerminal(1), Objects.requireNonNull(c11.getTerminal(2).connectivityNodeId()));
        network.connect(c15.getTerminal(1), Objects.requireNonNull(c12.getTerminal(2).connectivityNodeId()));

        Tracing.setPhases().run(network);

        return network;
    }
}
