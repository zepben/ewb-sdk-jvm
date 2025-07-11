/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assetinfo

import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.ResistanceReactance
import com.zepben.ewb.services.network.ResistanceReactanceTest.Companion.validateResistanceReactance
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy

internal class TransformerTankInfoTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(TransformerTankInfo().mRID, not(equalTo("")))
        assertThat(TransformerTankInfo("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val transformerTankInfo = TransformerTankInfo()

        assertThat(transformerTankInfo.powerTransformerInfo, nullValue())

        transformerTankInfo.fillFields(NetworkService())

        assertThat(transformerTankInfo.powerTransformerInfo, notNullValue())
    }

    @Test
    internal fun transformerEndInfo() {
        PrivateCollectionValidator.validateUnordered(
            ::TransformerTankInfo,
            ::TransformerEndInfo,
            TransformerTankInfo::transformerEndInfos,
            TransformerTankInfo::numTransformerEndInfos,
            TransformerTankInfo::getTransformerEndInfo,
            TransformerTankInfo::addTransformerEndInfo,
            TransformerTankInfo::removeTransformerEndInfo,
            TransformerTankInfo::clearTransformerEndInfos
        )
    }

    @Test
    internal fun populatesResistanceReactanceFromEndsWithMatchingNumber() {
        val end1 = spy(TransformerEndInfo().apply { endNumber = 1 })
        val end2 = spy(TransformerEndInfo().apply { endNumber = 2 })
        val tankInfo = TransformerTankInfo().apply {
            addTransformerEndInfo(end1)
            addTransformerEndInfo(end2)
        }

        doReturn(ResistanceReactance(1.1, 1.2, 1.3, 1.4)).`when`(end1).resistanceReactance()
        doReturn(ResistanceReactance(2.1, 2.2, 2.3, 2.4)).`when`(end2).resistanceReactance()

        validateResistanceReactance(tankInfo.resistanceReactance(1)!!, 1.1, 1.2, 1.3, 1.4)
        validateResistanceReactance(tankInfo.resistanceReactance(2)!!, 2.1, 2.2, 2.3, 2.4)
        assertThat(tankInfo.resistanceReactance(3), nullValue())
    }

}
