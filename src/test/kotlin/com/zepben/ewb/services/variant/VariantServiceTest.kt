/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.reflect.full.primaryConstructor

internal class VariantServiceTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val service = VariantService()

    @Test
    internal fun `can add and remove supported types`() {
        service.supportedKClasses
            .asSequence()
            .map { it.primaryConstructor!!.call("id-${it.simpleName}") }
            .forEach {
                assertThat("Initial tryAdd should return true", service.tryAdd(it))
                assertThat(service[it.mRID], equalTo(it))
                assertThat("tryRemove should return true for previously-added object", service.tryRemove(it))
                assertThat(service[it.mRID], nullValue())
            }
    }

}
