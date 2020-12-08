/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common;

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject;
import com.zepben.evolve.cim.iec61970.base.wires.Junction;
import com.zepben.evolve.cim.iec61970.base.wires.Recloser;
import com.zepben.evolve.services.network.NetworkService;
import com.zepben.testutils.junit.SystemLogExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.zepben.evolve.services.network.testdata.DifferenceNetworks.createSourceNetwork;
import static com.zepben.evolve.services.network.testdata.DifferenceNetworks.createTargetNetwork;
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
