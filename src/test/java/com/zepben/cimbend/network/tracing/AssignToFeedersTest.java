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

import com.zepben.cimbend.cim.iec61970.base.core.Feeder;
import com.zepben.cimbend.network.NetworkService;
import com.zepben.cimbend.testdata.PowerTransformersWithEndsNetwork;
import com.zepben.test.util.junit.SystemLogExtension;
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
