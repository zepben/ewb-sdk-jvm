/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.evolve.utils.PrivateCollectionValidator
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class ProtectionRelaySchemeTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(ProtectionRelayScheme().mRID, not(equalTo("")))
        assertThat(ProtectionRelayScheme("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val protectionRelayScheme = ProtectionRelayScheme()

        assertThat(protectionRelayScheme.system, nullValue())

        protectionRelayScheme.fillFields(NetworkService())

        assertThat(protectionRelayScheme.system, instanceOf(ProtectionRelaySystem::class.java))
    }

    @Test
    internal fun functions() {
        PrivateCollectionValidator.validate(
            { ProtectionRelayScheme() },
            { id, _ -> object : ProtectionRelayFunction(id) {} },
            ProtectionRelayScheme::numFunctions,
            ProtectionRelayScheme::getFunction,
            ProtectionRelayScheme::functions,
            ProtectionRelayScheme::addFunction,
            ProtectionRelayScheme::removeFunction,
            ProtectionRelayScheme::clearFunctions
        )
    }

}
