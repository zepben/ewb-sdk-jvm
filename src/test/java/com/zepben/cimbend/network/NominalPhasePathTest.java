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
package com.zepben.cimbend.network;

import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind;
import com.zepben.cimbend.network.model.NominalPhasePath;
import com.zepben.test.util.junit.SystemLogExtension;
import kotlin.jvm.JvmField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class NominalPhasePathTest {

    @JvmField
    @RegisterExtension
    public SystemLogExtension systemOutRule = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess();

    private final NominalPhasePath nominalPhasePath1 = NominalPhasePath.between(SinglePhaseKind.A, SinglePhaseKind.B);
    private final NominalPhasePath nominalPhasePath2 = NominalPhasePath.between(SinglePhaseKind.C, SinglePhaseKind.N);

    @Test
    public void accessors() {
        assertThat(nominalPhasePath1.from(), equalTo(SinglePhaseKind.A));
        assertThat(nominalPhasePath1.to(), equalTo(SinglePhaseKind.B));
        assertThat(nominalPhasePath2.from(), equalTo(SinglePhaseKind.C));
        assertThat(nominalPhasePath2.to(), equalTo(SinglePhaseKind.N));
    }

    @Test
    public void coverage() {
        assertThat(nominalPhasePath1, equalTo(nominalPhasePath1));
        assertThat(nominalPhasePath1, equalTo(NominalPhasePath.between(SinglePhaseKind.A, SinglePhaseKind.B)));
        assertThat(nominalPhasePath1, not(equalTo(nominalPhasePath2)));
        assertThat(nominalPhasePath1, not(equalTo(null)));
        assertThat(nominalPhasePath1, not(equalTo("test")));
        assertThat(nominalPhasePath1, not(equalTo(NominalPhasePath.between(SinglePhaseKind.A, SinglePhaseKind.C))));
        assertThat(nominalPhasePath1, not(equalTo(NominalPhasePath.between(SinglePhaseKind.B, SinglePhaseKind.B))));

        assertThat(nominalPhasePath1.toString(), not(is(emptyString())));

        assertThat(nominalPhasePath1.hashCode(), not(equalTo(NominalPhasePath.between(SinglePhaseKind.B, SinglePhaseKind.A))));
    }

}
