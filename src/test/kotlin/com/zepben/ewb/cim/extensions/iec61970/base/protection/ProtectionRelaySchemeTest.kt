/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.ewb.utils.PrivateCollectionValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ProtectionRelaySchemeTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(ProtectionRelayScheme("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val protectionRelayScheme = ProtectionRelayScheme(generateId())

        assertThat(protectionRelayScheme.system, nullValue())

        protectionRelayScheme.fillFields(NetworkService())

        assertThat(protectionRelayScheme.system, instanceOf(ProtectionRelaySystem::class.java))
    }

    @Test
    internal fun functions() {
        PrivateCollectionValidator.validateUnordered(
            ::ProtectionRelayScheme,
            { id -> object : ProtectionRelayFunction(id) {} },
            ProtectionRelayScheme::functions,
            ProtectionRelayScheme::numFunctions,
            ProtectionRelayScheme::getFunction,
            ProtectionRelayScheme::addFunction,
            ProtectionRelayScheme::removeFunction,
            ProtectionRelayScheme::clearFunctions
        )
    }

}
