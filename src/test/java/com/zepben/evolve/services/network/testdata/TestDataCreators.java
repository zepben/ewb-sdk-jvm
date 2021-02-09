/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.testdata;

import com.zepben.evolve.cim.iec61968.assetinfo.OverheadWireInfo;
import com.zepben.evolve.cim.iec61968.assetinfo.WireInfo;
import com.zepben.evolve.cim.iec61968.assets.AssetOwner;
import com.zepben.evolve.cim.iec61968.common.Organisation;
import com.zepben.evolve.cim.iec61968.metering.Meter;
import com.zepben.evolve.cim.iec61968.metering.UsagePoint;
import com.zepben.evolve.cim.iec61970.base.core.*;
import com.zepben.evolve.cim.iec61970.base.wires.*;
import com.zepben.evolve.services.customer.CustomerService;
import com.zepben.evolve.services.network.NetworkService;

import javax.annotation.Nullable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Class that generates some networks for testing with.
 */
@SuppressWarnings({"WeakerAccess", "SameParameterValue", "ConstantConditions"})
public class TestDataCreators {

    public static Junction createNodeForConnecting(NetworkService network, String id, int numTerminals) {
        return createNodeForConnecting(network, id, numTerminals, PhaseCode.ABCN);
    }

    public static EnergySource createSourceForConnecting(NetworkService network, String id, int numTerminals, PhaseCode phaseCode) {
        EnergySource node = new EnergySource(id);

        phaseCode.singlePhases().forEach(phase -> {
            EnergySourcePhase energySourcePhase = new EnergySourcePhase();
            energySourcePhase.setEnergySource(node);
            energySourcePhase.setPhase(phase);
            node.addPhase(energySourcePhase);
            network.add(energySourcePhase);
        });

        createTerminals(network, node, numTerminals, phaseCode);

        network.add(node);
        return node;
    }

    public static Junction createNodeForConnecting(NetworkService network, String id, int numTerminals, PhaseCode nominalPhases) {
        Junction node = new Junction(id);
        node.setName("test name");
        createTerminals(network, node, numTerminals, nominalPhases);

        network.add(node);
        return node;
    }

    public static Breaker createSwitchForConnecting(NetworkService network, String id, int numTerminals, PhaseCode nominalPhases, boolean... openStatus) {
        Breaker node = new Breaker(id);
        node.setName("test name");
        createTerminals(network, node, numTerminals, nominalPhases);

        for (int index = 0; index < openStatus.length; ++index) {
            node.setNormallyOpen(openStatus[index], nominalPhases.singlePhases().get(index));
            node.setOpen(openStatus[index], nominalPhases.singlePhases().get(index));
        }

        network.add(node);
        return node;
    }

    public static PowerTransformer createPowerTransformerForConnecting(NetworkService network, String id, int numTerminals, PhaseCode nominalPhases, int numUsagePoints, int numMeters) {
        PowerTransformer powerTransformer = new PowerTransformer(id);
        powerTransformer.setName(id + " name");
        createTerminals(network, powerTransformer, numTerminals, nominalPhases);

        network.add(powerTransformer);

        for (int i = 1; i <= numUsagePoints; ++i) {
            UsagePoint usagePoint = new UsagePoint(id + "-up" + i);

            powerTransformer.addUsagePoint(usagePoint);
            usagePoint.addEquipment(powerTransformer);

            for (int j = 1; j <= numMeters; ++j) {
                Meter meter = createMeter(network, id + "-up" + i + "-m" + j);

                usagePoint.addEndDevice(meter);
                meter.addUsagePoint(usagePoint);
            }
        }

        return powerTransformer;
    }

    public static AcLineSegment createAcLineSegmentForConnecting(NetworkService network, String id, PhaseCode nominalPhases) {
        return createAcLineSegmentForConnecting(network, id, nominalPhases, 0, "perLengthSequenceImpedanceId", "wireInfo");
    }

    public static AcLineSegment createAcLineSegmentForConnecting(NetworkService network, String id, PhaseCode nominalPhases, double length, String perLengthSequenceImpedanceId) {
        return createAcLineSegmentForConnecting(network, id, nominalPhases, length, perLengthSequenceImpedanceId, "wireInfo");
    }

    public static AcLineSegment createAcLineSegmentForConnecting(NetworkService network, String id, PhaseCode nominalPhases, double length, String perLengthSequenceImpedanceId, String wireInfoId) {
        PerLengthSequenceImpedance perLengthSequenceImpedance = network.get(PerLengthSequenceImpedance.class, perLengthSequenceImpedanceId);
        if (perLengthSequenceImpedance == null) {
            perLengthSequenceImpedance = new PerLengthSequenceImpedance(perLengthSequenceImpedanceId);
            network.add(perLengthSequenceImpedance);
        }

        WireInfo wireInfo = network.get(WireInfo.class, wireInfoId);
        if (wireInfo == null) {
            wireInfo = new OverheadWireInfo(wireInfoId);
            network.add((OverheadWireInfo) wireInfo);
        }

        AcLineSegment acLineSegment = new AcLineSegment(id);
        acLineSegment.setName(id + " name");
        acLineSegment.setPerLengthSequenceImpedance(perLengthSequenceImpedance);
        acLineSegment.setAssetInfo(wireInfo);
        acLineSegment.setLength(length);

        createTerminals(network, acLineSegment, 2, nominalPhases);

        network.add(acLineSegment);
        return acLineSegment;
    }

    @SuppressWarnings("SameParameterValue")
    public static EnergyConsumer createEnergyConsumer(NetworkService network, String id, int numTerminals, PhaseCode nominalPhases) {
        EnergyConsumer energyConsumer = new EnergyConsumer(id);
        energyConsumer.setName(id);
        createTerminals(network, energyConsumer, numTerminals, nominalPhases);

        network.add(energyConsumer);
        return energyConsumer;
    }

    @SuppressWarnings("SameParameterValue")
    public static Junction createHvMeterNode(NetworkService network, String id, PhaseCode nominalPhases) {
        Junction node = new Junction(id);
        node.setName(id);
        createTerminals(network, node, 1, nominalPhases);

        network.add(node);
        return node;
    }

    public static Meter createMeter(NetworkService network, String id) {
        Meter meter = new Meter(id);
        meter.setName("companyMeterId" + id);
        meter.addOrganisationRole(createAssetOwner(network, null, "company" + id));

        network.add(meter);
        return meter;
    }

    public static AssetOwner createAssetOwner(NetworkService network, @Nullable CustomerService customerService, String company) {
        AssetOwner assetOwner = new AssetOwner(company + "-owner-role");
        Organisation org = new Organisation(company);
        org.setName(company);
        assetOwner.setOrganisation(org);

        network.add(org);
        network.add(assetOwner);

        if (customerService != null) {
            customerService.add(org);
        }

        return assetOwner;
    }

    public static void createTerminals(NetworkService network, ConductingEquipment condEq, int numTerminals, PhaseCode nominalPhases) {
        for (int i = 1; i <= numTerminals; ++i)
            createTerminal(network, condEq, nominalPhases, i);
    }

    public static Terminal createTerminal(NetworkService network, ConductingEquipment conductingEquipment, PhaseCode phases) {
        Terminal terminal = new Terminal();
        terminal.setConductingEquipment(conductingEquipment);
        terminal.setPhases(phases);
        if (conductingEquipment != null)
            conductingEquipment.addTerminal(terminal);
        assertThat(network.add(terminal), equalTo(true));
        return terminal;
    }

    public static Terminal createTerminal(NetworkService network, ConductingEquipment conductingEquipment, PhaseCode phases, int sequenceNumber) {
        Terminal terminal = null;
        if (conductingEquipment != null) {
            terminal = conductingEquipment.getTerminal(sequenceNumber);
        }

        if (terminal == null) {
            terminal = new Terminal();
            terminal.setConductingEquipment(conductingEquipment);
            terminal.setPhases(phases);
            terminal.setSequenceNumber(sequenceNumber);
            if (conductingEquipment != null)
                conductingEquipment.addTerminal(terminal);
            assertThat(network.add(terminal), equalTo(true));
        }

        return terminal;
    }

    public static GeographicalRegion createGeographicalRegion(NetworkService networkService, String mRID, String name) {
        GeographicalRegion geographicalRegion = new GeographicalRegion(mRID);
        geographicalRegion.setName(name);

        networkService.add(geographicalRegion);

        return geographicalRegion;
    }

    public static SubGeographicalRegion createSubGeographicalRegion(NetworkService networkService, String mRID, String name, @Nullable GeographicalRegion geographicalRegion) {
        SubGeographicalRegion subGeographicalRegion = new SubGeographicalRegion(mRID);
        subGeographicalRegion.setName(name);

        if (geographicalRegion != null) {
            subGeographicalRegion.setGeographicalRegion(geographicalRegion);
            geographicalRegion.addSubGeographicalRegion(subGeographicalRegion);
        }

        networkService.add(subGeographicalRegion);

        return subGeographicalRegion;
    }

    public static Substation createSubstation(NetworkService networkService, String mRID, String name, @Nullable SubGeographicalRegion subGeographicalRegion) {
        Substation substation = new Substation(mRID);
        substation.setName(name);

        if (subGeographicalRegion != null) {
            substation.setSubGeographicalRegion(subGeographicalRegion);
            subGeographicalRegion.addSubstation(substation);
        }

        networkService.add(substation);

        return substation;
    }

    public static Feeder createFeeder(NetworkService networkService, String mRID, String name, @Nullable Substation substation, @Nullable ConductingEquipment feederStartPoint) {
        return createFeeder(networkService, mRID, name, substation, feederStartPoint, feederStartPoint.getTerminal(1));
    }

    public static Feeder createFeeder(NetworkService networkService,
                                      String mRID,
                                      String name,
                                      @Nullable Substation substation,
                                      @Nullable ConductingEquipment feederStartPoint,
                                      Terminal headTerminal) {
        Feeder feeder = new Feeder(mRID);
        feeder.setName(name);
        if (feederStartPoint != null)
            feeder.setNormalHeadTerminal(headTerminal);

        if (substation != null) {
            feeder.setNormalEnergizingSubstation(substation);
            substation.addFeeder(feeder);
        }

        networkService.add(feeder);

        return feeder;
    }

    public static void createFeeder(NetworkService networkService, String mRID, String name, @Nullable Substation substation, String... equipmentMRIDs) {
        Feeder feeder = createFeeder(networkService, mRID, name, substation, networkService.get(ConductingEquipment.class, equipmentMRIDs[0]));

        for (String equipmentMRID : equipmentMRIDs) {
            ConductingEquipment conductingEquipment = networkService.get(ConductingEquipment.class, equipmentMRID);
            conductingEquipment.addContainer(feeder);

            feeder.addEquipment(conductingEquipment);
        }
    }

    public static void createEnd(NetworkService networkService, PowerTransformer tx, BaseVoltage bv, int endNumber) {
        PowerTransformerEnd end = new PowerTransformerEnd();
        end.setBaseVoltage(bv);
        end.setEndNumber(endNumber);

        end.setPowerTransformer(tx);
        tx.addEnd(end);

        networkService.add(end);
    }

    public static void createEnd(NetworkService networkService, PowerTransformer tx, int ratedU, int endNumber) {
        PowerTransformerEnd end = new PowerTransformerEnd();
        end.setRatedU(ratedU);
        end.setEndNumber(endNumber);

        end.setPowerTransformer(tx);
        tx.addEnd(end);

        networkService.add(end);
    }

}
