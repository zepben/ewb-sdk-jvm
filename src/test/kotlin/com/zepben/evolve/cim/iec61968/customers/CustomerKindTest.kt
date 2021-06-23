/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.customers

import com.zepben.evolve.cim.validateEnum
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.protobuf.cim.iec61968.customers.CustomerKind as PBCustomerKind

internal class CustomerKindTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun validateVsPb() {
        validateEnum(CustomerKind.values(), PBCustomerKind.values())
    }

}
