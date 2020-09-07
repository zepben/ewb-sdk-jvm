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
package com.zepben.cimbend.cim.iec61968.assets

import com.zepben.cimbend.cim.iec61968.common.Location
import com.zepben.cimbend.utils.PrivateCollectionValidator
import com.zepben.test.util.junit.SystemLogExtension
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
    
