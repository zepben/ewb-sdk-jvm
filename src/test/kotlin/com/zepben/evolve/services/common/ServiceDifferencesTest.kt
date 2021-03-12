/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common

import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.cim.iec61970.base.wires.Recloser
import com.zepben.evolve.services.network.testdata.DifferenceNetworks.createSourceNetwork
import com.zepben.evolve.services.network.testdata.DifferenceNetworks.createTargetNetwork
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ServiceDifferencesTest {

    @JvmField
    @RegisterExtension
    var systemOutRule: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    fun accessors() {
        val source = createSourceNetwork()
        val target = createTargetNetwork()
        val differences = ServiceDifferences({ source[it] }) { target[it] }

        val modification6 = ObjectDifference(Junction("1"), Junction("1"))
        modification6.differences["value"] = ValueDifference(1, 2)

        val modification7 = ObjectDifference(Recloser("1"), Recloser("1"))
        modification7.differences["collection"] = IndexedDifference(1, ValueDifference("a", "b"))

        differences.addToMissingFromTarget("1")
        differences.addToMissingFromTarget("2")
        differences.addToMissingFromTarget("3")
        differences.addToMissingFromSource("4")
        differences.addToMissingFromSource("5")
        differences.addModifications("6", modification6)
        differences.addModifications("7", modification7)

        assertThat(differences.missingFromTarget(), contains("1", "2", "3"))
        assertThat(differences.missingFromSource(), contains("4", "5"))
        assertThat(differences.modifications(), hasEntry("6", modification6))
        assertThat(differences.modifications(), hasEntry("7", modification7))
        assertThat(differences.toString(), not(emptyString()))
    }

}
