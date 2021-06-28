/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common

import com.zepben.evolve.cim.iec61970.base.core.NameType
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.cim.iec61970.base.wires.Recloser
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
        val s1 = Junction("s1").apply { name = "source-object-1" }
        val t1 = Junction("t1").apply { name = "target-object-1" }
        val s2 = NameType("s2").apply { description = "source-name-type-2" }
        val t2 = NameType("t2").apply { description = "target-name-type-2" }
        val modification1 = ObjectDifference(Junction("1"), Junction("1")).apply { differences["value"] = ValueDifference(1, 2) }
        val modification2 = ObjectDifference(Recloser("1"), Recloser("1")).apply { differences["collection"] = IndexedDifference(1, ValueDifference("a", "b")) }

        val differences = ServiceDifferences(
            { if (it == "s1") s1 else null },
            { if (it == "t1") t1 else null },
            { if (it == "s2") s2 else null },
            { if (it == "t2") t2 else null }
        )

        differences.addToMissingFromTarget("s1")
        differences.addToMissingFromTarget("s2")
        differences.addToMissingFromTarget("s3")
        differences.addToMissingFromSource("t1")
        differences.addToMissingFromSource("t2")
        differences.addToMissingFromSource("t3")
        differences.addModifications("m1", modification1)
        differences.addModifications("m2", modification2)

        assertThat(differences.missingFromTarget(), contains("s1", "s2", "s3"))
        assertThat(differences.missingFromSource(), contains("t1", "t2", "t3"))
        assertThat(differences.modifications(), hasEntry("m1", modification1))
        assertThat(differences.modifications(), hasEntry("m2", modification2))

        val expectedMessage =
            """
            Missing From Target:
               $s1
               $s2
               s3
            Missing From Source:
               $t1
               $t2
               t3
            Modifications:
               m1: $modification1
               m2: $modification2
            """.trimIndent()

        assertThat(differences.toString(), equalTo(expectedMessage))
    }

}
