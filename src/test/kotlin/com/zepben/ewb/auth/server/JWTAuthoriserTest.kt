/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.auth.server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.zepben.ewb.auth.common.StatusCode
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

class JWTAuthoriserTest {
    @Test
    fun permissionsFieldTest() {
        checkPermissionsField("permissions", StatusCode.OK)
    }

    @Test
    fun rolesFieldTest() {
        checkPermissionsField("roles", StatusCode.OK)
    }

    @Test
    fun unknownFieldTest() {
        checkPermissionsField("unknown", StatusCode.UNAUTHENTICATED)
    }

    private fun checkPermissionsField(fieldName: String, expectedResponseCode: StatusCode) {
        //Token with permissions field
        val decodedToken = JWT.decode(
            JWT.create()
                .withClaim(fieldName, listOf("write:network", "newProduct:magic"))
                .sign(Algorithm.none())
        )

        var authResp = JWTAuthoriser.authorise(decodedToken, "write:network")
        MatcherAssert.assertThat(authResp.statusCode, Matchers.equalTo(expectedResponseCode))
        authResp = JWTAuthoriser.authorise(decodedToken, "newProduct:magic")
        MatcherAssert.assertThat(authResp.statusCode, Matchers.equalTo(expectedResponseCode))
        authResp = JWTAuthoriser.authorise(decodedToken, setOf("write:network", "newProduct:magic"))
        MatcherAssert.assertThat(authResp.statusCode, Matchers.equalTo(expectedResponseCode))
    }
}
