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

import com.zepben.cimbend.cim.iec61968.metering.UsagePoint;
import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.cimbend.cim.iec61970.base.wires.PowerTransformer;
import com.zepben.cimbend.network.NetworkService;
import com.zepben.cimbend.testdata.TestNetworks;
import com.zepben.test.util.junit.SystemLogExtension;
import kotlin.jvm.JvmField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.network.tracing.FindWithUsagePoints.Result.Status.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FindWithUsagePointsTest {

    @JvmField
    @RegisterExtension
    public SystemLogExtension systemOutRule = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess();

    private final FindWithUsagePoints findWithUsagePoints = new FindWithUsagePoints();

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
    private final NetworkService network = TestNetworks.withUsagePointsNetwork();

    private final ConductingEquipment es = ce("es");
    private final ConductingEquipment c1 = ce("c1");
    private final ConductingEquipment c3 = ce("c3");
    private final ConductingEquipment c5 = ce("c5");
    private final ConductingEquipment c11 = ce("c11");
    private final ConductingEquipment c12 = ce("c12");
    private final ConductingEquipment tx6 = ce("tx6");

    @Test
    public void normalStateSingleTrace() {
        validate(findWithUsagePoints.runNormal(es, null), NO_ERROR, "tx1", "tx2", "tx3", "tx4", "tx5", "iso");
        validate(findWithUsagePoints.runNormal(c3, c5), NO_ERROR, "tx4", "tx5");
        validate(findWithUsagePoints.runNormal(c5, c3), NO_ERROR, "tx4", "tx5");
        validate(findWithUsagePoints.runNormal(es, c11), NO_PATH);
    }

    @Test
    public void normalStateMultiTrace() {
        List<FindWithUsagePoints.Result> results = findWithUsagePoints.runNormal(Arrays.asList(es, c3, c5, es), Arrays.asList(null, c5, c3, c11));

        assertThat(results.size(), equalTo(4));
        validate(results.get(0), NO_ERROR, "tx1", "tx2", "tx3", "tx4", "tx5", "iso");
        validate(results.get(1), NO_ERROR, "tx4", "tx5");
        validate(results.get(2), NO_ERROR, "tx4", "tx5");
        validate(results.get(3), NO_PATH);

        validateMismatch(findWithUsagePoints.runNormal(Arrays.asList(es, es), Collections.singletonList(null)), 2);
        validateMismatch(findWithUsagePoints.runNormal(Arrays.asList(es, es), Arrays.asList(c1, c11, c3)), 3);
    }

    @Test
    public void currentStateSingleTrace() {
        validate(findWithUsagePoints.runCurrent(es, null), NO_ERROR, "tx2", "tx3", "tx6", "tx7", "tx8");
        validate(findWithUsagePoints.runCurrent(c1, c12), NO_ERROR, "tx3", "tx7");
        validate(findWithUsagePoints.runCurrent(c12, c1), NO_ERROR, "tx3", "tx7");
        validate(findWithUsagePoints.runCurrent(es, c5), NO_PATH);
    }

    @Test
    public void currentStateMultiTrace() {
        List<FindWithUsagePoints.Result> results = findWithUsagePoints.runCurrent(Arrays.asList(es, c1, c12, es), Arrays.asList(null, c12, c1, c5));

        assertThat(results.size(), equalTo(4));
        validate(results.get(0), NO_ERROR, "tx2", "tx3", "tx6", "tx7", "tx8");
        validate(results.get(1), NO_ERROR, "tx3", "tx7");
        validate(results.get(2), NO_ERROR, "tx3", "tx7");
        validate(results.get(3), NO_PATH);

        validateMismatch(findWithUsagePoints.runCurrent(Arrays.asList(es, es), Collections.singletonList(null)), 2);
        validateMismatch(findWithUsagePoints.runCurrent(Arrays.asList(es, es), Arrays.asList(c1, c11, c3)), 3);
    }

    @Test
    public void sameFromAndTo() {
        validate(findWithUsagePoints.runNormal(c3, c3), NO_ERROR);
        validate(findWithUsagePoints.runNormal(tx6, tx6), NO_ERROR, "tx6");
    }

    @Test
    public void worksWithNoTerminals() {
        PowerTransformer tx1 = new PowerTransformer();
        PowerTransformer tx2 = new PowerTransformer();
        UsagePoint usagePoint = new UsagePoint();

        usagePoint.addEquipment(tx1);
        tx1.addUsagePoint(usagePoint);

        validate(findWithUsagePoints.runNormal(tx1, null), NO_ERROR, tx1.getMRID());
        validate(findWithUsagePoints.runNormal(tx1, tx1), NO_ERROR, tx1.getMRID());
        validate(findWithUsagePoints.runNormal(tx2, null), NO_ERROR);
        validate(findWithUsagePoints.runNormal(tx2, tx2), NO_ERROR);
    }

    private ConductingEquipment ce(String mRID) {
        return network.get(ConductingEquipment.class, mRID);
    }

    private void validate(FindWithUsagePoints.Result result, FindWithUsagePoints.Result.Status expectedStatus, String... expectedMRIDs) {
        assertThat(result.status(), equalTo(expectedStatus));
        assertThat(result.conductingEquipment().keySet(), containsInAnyOrder(expectedMRIDs));
    }

    private void validateMismatch(List<FindWithUsagePoints.Result> results, int expectedResults) {
        assertThat(results.size(), equalTo(expectedResults));

        results.forEach(result -> {
            assertThat(result.status(), equalTo(MISMATCHED_FROM_TO));
            assertThat(result.conductingEquipment().keySet(), empty());
        });
    }

}
