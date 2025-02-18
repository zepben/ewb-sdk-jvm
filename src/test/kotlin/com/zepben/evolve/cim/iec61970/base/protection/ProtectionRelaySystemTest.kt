/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61970.infiec61970.protection.ProtectionKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.evolve.utils.PrivateCollectionValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test

internal class ProtectionRelaySystemTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(ProtectionRelaySystem().mRID, not(equalTo("")))
        assertThat(ProtectionRelaySystem("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val protectionRelaySystem = ProtectionRelaySystem()

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
