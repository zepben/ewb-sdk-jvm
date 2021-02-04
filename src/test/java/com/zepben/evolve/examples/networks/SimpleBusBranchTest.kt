/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.examples.networks
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.network.NetworkService
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test



class SimpleBusBranchTest {
    val net = simpleBusBranchNetwork()

    @Test
    internal fun basicServiceCreated() {
        assertThat(net, instanceOf(BaseService::class.java))
    }

}