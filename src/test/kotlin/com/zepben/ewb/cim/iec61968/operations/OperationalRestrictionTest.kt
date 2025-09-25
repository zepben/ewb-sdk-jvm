/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.operations

import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class OperationalRestrictionTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(OperationalRestriction().mRID, not(equalTo("")))
        assertThat(OperationalRestriction("id").mRID, equalTo("id"))
    }

    @Test
    internal fun equipment() {
        PrivateCollectionValidator.validateUnordered(
            ::OperationalRestriction,
            { id -> object : Equipment(id) {} },
            OperationalRestriction::equipment,
            OperationalRestriction::numEquipment,
            OperationalRestriction::getEquipment,
            OperationalRestriction::addEquipment,
            OperationalRestriction::removeEquipment,
            OperationalRestriction::clearEquipment
        )
    }

}
