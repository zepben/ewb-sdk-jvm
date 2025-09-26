/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.common

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class OrganisationRoleTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : OrganisationRole() {}.mRID, not(equalTo("")))
        assertThat(object : OrganisationRole("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val organisationRole = object : OrganisationRole() {}
        val organisation = Organisation()

        assertThat(organisationRole.organisation, nullValue())

        organisationRole.organisation = organisation
        assertThat(organisationRole.organisation, equalTo(organisation))
    }

}
