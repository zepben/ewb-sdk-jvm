/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.cim.iec61968.assets.Asset
import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PowerSystemResourceTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : PowerSystemResource("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val powerSystemResource = object : PowerSystemResource(generateId()) {}
        val location = Location(generateId())

        assertThat(powerSystemResource.assetInfo, nullValue())
        assertThat(powerSystemResource.location, nullValue())
        assertThat(powerSystemResource.numControls, nullValue())
        assertThat(powerSystemResource.hasControls(), equalTo(false))

        powerSystemResource.location = location
        powerSystemResource.numControls = 4

        assertThat(powerSystemResource.hasControls(), equalTo(true))

        assertThat(powerSystemResource.location, equalTo(location))
        assertThat(powerSystemResource.numControls, equalTo(4))
        assertThat(powerSystemResource.hasControls(), equalTo(true))
    }

    @Test
    internal fun assets() {
        PrivateCollectionValidator.validateUnordered(
            { id -> object : PowerSystemResource(id) {} },
            { id -> object : Asset(id) {} },
            PowerSystemResource::assets,
            PowerSystemResource::numAssets,
            PowerSystemResource::getAsset,
            PowerSystemResource::addAsset,
            PowerSystemResource::removeAsset,
            PowerSystemResource::clearAssets
        )
    }

}
