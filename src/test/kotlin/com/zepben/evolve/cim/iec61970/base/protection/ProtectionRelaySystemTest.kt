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
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test

internal class ProtectionRelaySystemTest {

    @Test
    internal fun constructorCoverage() {
        MatcherAssert.assertThat(ProtectionRelaySystem().mRID, not(equalTo("")))
        MatcherAssert.assertThat(ProtectionRelaySystem("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val protectionRelaySystem = ProtectionRelaySystem()

        MatcherAssert.assertThat(protectionRelaySystem.protectionKind, equalTo(ProtectionKind.UNKNOWN))

        protectionRelaySystem.fillFields(NetworkService())

        MatcherAssert.assertThat(protectionRelaySystem.protectionKind, equalTo(ProtectionKind.DISTANCE))
    }

    @Test
    internal fun schemes() {
        PrivateCollectionValidator.validate(
            { ProtectionRelaySystem() },
            { id, _ -> ProtectionRelayScheme(id) },
            ProtectionRelaySystem::numSchemes,
            ProtectionRelaySystem::getScheme,
            ProtectionRelaySystem::schemes,
            ProtectionRelaySystem::addScheme,
            ProtectionRelaySystem::removeScheme,
            ProtectionRelaySystem::clearSchemes
        )
    }

}
