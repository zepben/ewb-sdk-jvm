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
package com.zepben.cimbend.common;

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject;
import com.zepben.cimbend.cim.iec61970.base.wires.Junction;
import com.zepben.cimbend.cim.iec61970.base.wires.Recloser;
import com.zepben.cimbend.network.NetworkService;
import com.zepben.test.util.junit.SystemLogExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.zepben.cimbend.testdata.DifferenceNetworks.createSourceNetwork;
import static com.zepben.cimbend.testdata.DifferenceNetworks.createTargetNetwork;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ServiceDifferencesTest {

    @RegisterExtension
    public SystemLogExtension systemOutRule = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess();

    @Test
    public void accessors() {
        NetworkService source = createSourceNetwork();
        NetworkService target = createTargetNetwork();
        ServiceDifferences differences = new ServiceDifferences(mRID -> source.get(IdentifiedObject.class, mRID),
            mRID -> target.get(IdentifiedObject.class, mRID)
        );

        ObjectDifference<Junction> modification6 = new ObjectDifference<>(new Junction("1"), new Junction("1"));
        modification6.getDifferences().put("value", new ValueDifference(1, 2));

        ObjectDifference<Recloser> modification7 = new ObjectDifference<>(new Recloser("1"), new Recloser("1"));
        modification7.getDifferences().put("collection", new IndexedDifference(1, new ValueDifference("a", "b")));

        differences.addToMissingFromTarget("1");
        differences.addToMissingFromTarget("2");
        differences.addToMissingFromTarget("3");
        differences.addToMissingFromSource("4");
        differences.addToMissingFromSource("5");
        differences.addModifications("6", modification6);
        differences.addModifications("7", modification7);

        assertThat(differences.missingFromTarget(), contains("1", "2", "3"));
        assertThat(differences.missingFromSource(), contains("4", "5"));
        assertThat(differences.modifications(), hasEntry("6", modification6));
        assertThat(differences.modifications(), hasEntry("7", modification7));

        assertThat(differences.toString(), not(emptyString()));
    }

}
