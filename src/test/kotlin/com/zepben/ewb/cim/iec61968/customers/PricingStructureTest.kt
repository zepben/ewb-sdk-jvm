/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.customers

import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PricingStructureTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PricingStructure("id").mRID, equalTo("id"))
    }

    @Test
    internal fun tariffs() {
        PrivateCollectionValidator.validateUnordered(
            ::PricingStructure,
            ::Tariff,
            PricingStructure::tariffs,
            PricingStructure::numTariffs,
            PricingStructure::getTariff,
            PricingStructure::addTariff,
            PricingStructure::removeTariff,
            PricingStructure::clearTariffs
        )
    }

}
