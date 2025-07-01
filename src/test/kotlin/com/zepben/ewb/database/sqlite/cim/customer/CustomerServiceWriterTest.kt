/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.customer

import com.zepben.ewb.cim.iec61968.customers.Customer
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CustomerServiceWriterTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val customerService = CustomerService()
    private val cimWriter = mockk<CustomerCimWriter> { every { write(any<Customer>()) } returns true }
    private val customerServiceWriter = CustomerServiceWriter(mockk(), cimWriter)

    //
    // NOTE: We don't do an exhaustive test of saving objects as this is done via the schema test.
    //

    @Test
    internal fun `passes objects through to the cim writer`() {
        val customer = Customer().also { customerService.add(it) }

        // NOTE: the write method will fail due to the relaxed mock returning false for all write operations,
        //       but a `write` should still be attempted on every object
        customerServiceWriter.write(customerService)

        verify(exactly = 1) { cimWriter.write(customer) }
    }

}
