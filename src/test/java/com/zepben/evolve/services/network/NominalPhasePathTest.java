/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network;

import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind;
import com.zepben.evolve.services.network.model.NominalPhasePath;
import com.zepben.testutils.junit.SystemLogExtension;
import kotlin.jvm.JvmField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class NominalPhasePathTest {

    @JvmField
    @RegisterExtension
    public SystemLogExtension systemOutRule = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess();

    private final NominalPhasePath nominalPhasePath1 = new NominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.B);
    private final NominalPhasePath nominalPhasePath2 = new NominalPhasePath(SinglePhaseKind.C, SinglePhaseKind.N);

    @Test
    public void accessors() {
        assertThat(nominalPhasePath1.getFrom(), equalTo(SinglePhaseKind.A));
        assertThat(nominalPhasePath1.getTo(), equalTo(SinglePhaseKind.B));
        assertThat(nominalPhasePath2.getFrom(), equalTo(SinglePhaseKind.C));
        assertThat(nominalPhasePath2.getTo(), equalTo(SinglePhaseKind.N));
    }

    @Test
    public void coverage() {
        assertThat(nominalPhasePath1, equalTo(nominalPhasePath1));
        assertThat(nominalPhasePath1, equalTo(new NominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.B)));
        assertThat(nominalPhasePath1, not(equalTo(nominalPhasePath2)));
        assertThat(nominalPhasePath1, not(equalTo(null)));
        assertThat(nominalPhasePath1, not(equalTo("test")));
        assertThat(nominalPhasePath1, not(equalTo(new NominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.C))));
        assertThat(nominalPhasePath1, not(equalTo(new NominalPhasePath(SinglePhaseKind.B, SinglePhaseKind.B))));

        assertThat(nominalPhasePath1.toString(), not(is(emptyString())));

        assertThat(nominalPhasePath1.hashCode(), not(equalTo(new NominalPhasePath(SinglePhaseKind.B, SinglePhaseKind.A))));
    }

}
