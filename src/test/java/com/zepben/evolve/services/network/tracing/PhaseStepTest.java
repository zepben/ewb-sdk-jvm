/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing;

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.testutils.junit.SystemLogExtension;
import com.zepben.testutils.mockito.DefaultAnswer;
import kotlin.jvm.JvmField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Arrays;

import static com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class PhaseStepTest {

    @JvmField
    @RegisterExtension
    public SystemLogExtension systemOutRule = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess();

    @Test
    public void accessors() {
        ConductingEquipment asset = mock(ConductingEquipment.class);
        PhaseStep atoc = PhaseStep.startAt(asset, Arrays.asList(A, B, C, N, B, C, N));

        assertThat(atoc.conductingEquipment(), equalTo(asset));
        assertThat(atoc.phases(), containsInAnyOrder(A, B, C, N));
    }

    @Test
    public void coverage() {
        ConductingEquipment asset1 = mock(ConductingEquipment.class, DefaultAnswer.of(String.class, "asset1"));
        ConductingEquipment asset2 = mock(ConductingEquipment.class, DefaultAnswer.of(String.class, "asset2"));

        PhaseStep atoc1 = PhaseStep.startAt(asset1, Arrays.asList(A, B, C, N, B, C, N));
        PhaseStep atoc1Dup = PhaseStep.startAt(asset1, Arrays.asList(A, B, C, N, B, C, N));
        PhaseStep atoc2 = PhaseStep.startAt(asset2, Arrays.asList(A, B, C, N, B, C, N));
        PhaseStep atoc3 = PhaseStep.startAt(asset1, Arrays.asList(A, B));

        assertThat(atoc1, equalTo(atoc1));
        assertThat(atoc1, equalTo(atoc1Dup));
        assertThat(atoc1, not(equalTo(null)));
        assertThat(atoc1, not(equalTo(atoc2)));
        assertThat(atoc1, not(equalTo(atoc3)));

        assertThat(atoc1.hashCode(), equalTo(atoc1Dup.hashCode()));

        assertThat(atoc1.toString(), not(is(emptyString())));
    }

}
