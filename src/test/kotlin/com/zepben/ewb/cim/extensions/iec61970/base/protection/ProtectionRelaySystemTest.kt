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
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ProtectionRelaySystemTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(ProtectionRelaySystem("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val protectionRelaySystem = ProtectionRelaySystem(generateId())

        assertThat(protectionRelaySystem.protectionKind, equalTo(ProtectionKind.UNKNOWN))

        protectionRelaySystem.fillFields(NetworkService())

        assertThat(protectionRelaySystem.protectionKind, equalTo(ProtectionKind.DISTANCE))
    }

    @Test
    internal fun schemes() {
        PrivateCollectionValidator.validateUnordered(
            ::ProtectionRelaySystem,
            ::ProtectionRelayScheme,
            ProtectionRelaySystem::schemes,
            ProtectionRelaySystem::numSchemes,
            ProtectionRelaySystem::getScheme,
            ProtectionRelaySystem::addScheme,
            ProtectionRelaySystem::removeScheme,
            ProtectionRelaySystem::clearSchemes
        )
    }

}
