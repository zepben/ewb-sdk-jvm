/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61968.assets

import com.zepben.cimbend.cim.iec61968.common.Location
import com.zepben.cimbend.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AssetTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : Asset() {}.mRID, not(equalTo("")))
        assertThat(object : Asset("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val asset = object : Asset() {}
        val location = Location()

        assertThat(asset.location, nullValue())

        asset.location = location

        assertThat(asset.location, equalTo(location))
    }

    @Test
    internal fun organisationRoles() {
        PrivateCollectionValidator.validate(
            { object : Asset() {} },
            { id, _ -> object : AssetOrganisationRole(id) {} },
            Asset::numOrganisationRoles,
            Asset::getOrganisationRole,
            Asset::organisationRoles,
            Asset::addOrganisationRole,
            Asset::removeOrganisationRole,
            Asset::clearOrganisationRoles
        )
    }
}
    
