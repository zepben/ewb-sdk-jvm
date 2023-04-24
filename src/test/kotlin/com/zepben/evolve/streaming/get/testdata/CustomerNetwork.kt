/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.testdata

import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.wires.EnergyConsumer
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.testing.TestNetworkBuilder

object CustomerNetwork {

    //     c1       c3
    // b0------tx2------ec4
    //
    fun create(): Pair<NetworkService, CustomerService> {
        val customerService = CustomerService()
        val builder = TestNetworkBuilder()
        builder.fromBreaker() // b0
            .toAcls() // c1
            .toPowerTransformer { addMeter(builder.network, customerService, this, Customer("customer1")) }  // tx2
            .toAcls() // c3
            .toOther({ EnergyConsumer("ec$it") }) { addMeter(builder.network, customerService, this, Customer("customer2")) }  // ec4
            .addFeeder("b0")
            .build()

        return Pair(builder.network, customerService)
    }

    private fun <T : Equipment> addMeter(networkService: NetworkService, customerService: CustomerService, obj: T, customer: Customer): T {
        customerService.add(customer)

        obj.addUsagePoint(
            UsagePoint("${obj.mRID}-up").also { usagePoint ->
                Meter("${obj.mRID}-m").apply {
                    customerMRID = customer.mRID
                    addUsagePoint(usagePoint)
                    usagePoint.addEndDevice(this)
                    networkService.add(this)
                }
                networkService.add(usagePoint)
            }
        )
        networkService.tryAdd(obj)

        return obj
    }


}
