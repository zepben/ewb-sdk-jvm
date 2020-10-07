/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network.tracing;

import com.zepben.cimbend.cim.iec61970.base.core.Feeder;
import com.zepben.cimbend.network.NetworkService;
import com.zepben.cimbend.testdata.PowerTransformersWithEndsNetwork;
import com.zepben.testutils.junit.SystemLogExtension;
import kotlin.jvm.JvmField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.ArrayList;
import java.util.List;

import static com.zepben.cimbend.testdata.TestNetworks.getNetwork;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

@SuppressWarnings("ConstantConditions")
public class AssignToFeedersTest {

    @JvmField
    @RegisterExtension
    public SystemLogExtension systemOutRule = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess();

    @Test
    public void canRunFromFeederCb() {
        NetworkService network = getNetwork(7);

        Tracing.assignEquipmentContainersToFeeders().run(network);

        Feeder feeder = network.get(Feeder.class, "f");

        List<String> mRIDs = new ArrayList<>();
        feeder.getCurrentEquipment().forEach(equipment -> mRIDs.add(equipment.getMRID()));
        assertThat(mRIDs, containsInAnyOrder("fcb", "c2", "n1"));
    }

    @Test
    public void appliesWithTransformersWithEndsWithBaseVoltage() {
        NetworkService network = PowerTransformersWithEndsNetwork.createWithBaseVoltage();

        Tracing.assignEquipmentContainersToFeeders().run(network);

        Feeder feeder = network.get(Feeder.class, "f");

        List<String> mRIDs = new ArrayList<>();
        feeder.getCurrentEquipment().forEach(equipment -> mRIDs.add(equipment.getMRID()));
        assertThat(mRIDs, containsInAnyOrder("ztx", "c1", "fsp", "c2", "reg", "c3", "iso", "c4", "tx", "c5"));
    }

    @Test
    public void appliesWithTransformersWithEndsWithRatedVoltage() {
        NetworkService network = PowerTransformersWithEndsNetwork.createWithRatedVoltage();

        Tracing.assignEquipmentContainersToFeeders().run(network);

        Feeder feeder = network.get(Feeder.class, "f");

        List<String> mRIDs = new ArrayList<>();
        feeder.getCurrentEquipment().forEach(equipment -> mRIDs.add(equipment.getMRID()));
        assertThat(mRIDs, containsInAnyOrder("ztx", "c1", "fsp", "c2", "reg", "c3", "iso", "c4", "tx", "c5"));
    }

}
