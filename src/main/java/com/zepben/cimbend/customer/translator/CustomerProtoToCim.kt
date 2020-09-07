/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.customer.translator

import com.zepben.cimbend.cim.iec61968.common.Agreement
import com.zepben.cimbend.cim.iec61968.common.Organisation
import com.zepben.cimbend.cim.iec61968.customers.*
import com.zepben.cimbend.common.Resolvers
import com.zepben.cimbend.common.translator.BaseProtoToCim
import com.zepben.cimbend.common.translator.toCim
import com.zepben.cimbend.customer.CustomerService
import com.zepben.protobuf.cim.iec61968.common.Agreement as PBAgreement
import com.zepben.protobuf.cim.iec61968.common.Organisation as PBOrganisation
import com.zepben.protobuf.cim.iec61968.customers.Customer as PBCustomer
import com.zepben.protobuf.cim.iec61968.customers.CustomerAgreement as PBCustomerAgreement
import com.zepben.protobuf.cim.iec61968.customers.PricingStructure as PBPricingStructure
import com.zepben.protobuf.cim.iec61968.customers.Tariff as PBTariff

/************ IEC61968 COMMON ************/
private fun toCim(pb: PBAgreement, cim: Agreement, customerService: CustomerService): Agreement =
    cim.also { toCim(pb.doc, it, customerService) }

/************ IEC61968 CUSTOMERS ************/
fun toCim(pb: PBCustomer, customerService: CustomerService): Customer =
    Customer(pb.mRID()).apply {
        kind = CustomerKind.valueOf(pb.kind.name)
        pb.customerAgreementMRIDsList.forEach { agreementMRID ->
            customerService.resolveOrDeferReference(Resolvers.agreements(this), agreementMRID)
        }
        numEndDevices = pb.numEndDevices
        toCim(pb.or, this, customerService)
    }

fun toCim(pb: PBCustomerAgreement, customerService: CustomerService): CustomerAgreement =
    CustomerAgreement(pb.mRID()).apply {
        customerService.resolveOrDeferReference(Resolvers.customer(this), pb.customerMRID)

        pb.pricingStructureMRIDsList.forEach {
            customerService.resolveOrDeferReference(Resolvers.pricingStructures(this), it)
        }
        toCim(pb.agr, this, customerService)
    }

fun toCim(pb: PBPricingStructure, customerService: CustomerService): PricingStructure =
    PricingStructure(pb.mRID()).apply {
        pb.tariffMRIDsList.forEach {
            customerService.resolveOrDeferReference(Resolvers.tariffs(this), it)
        }
        toCim(pb.doc, this, customerService)
    }

fun toCim(pb: PBTariff, customerService: CustomerService): Tariff =
    Tariff(pb.mRID()).apply {
        toCim(pb.doc, this, customerService)
    }

/************ Extensions ************/

fun CustomerService.addFromPb(pb: PBOrganisation): Organisation = toCim(pb, this).also { add(it) }
fun CustomerService.addFromPb(pb: PBCustomer): Customer = toCim(pb, this).also { add(it) }
fun CustomerService.addFromPb(pb: PBCustomerAgreement): CustomerAgreement = toCim(pb, this).also { add(it) }
fun CustomerService.addFromPb(pb: PBPricingStructure): PricingStructure = toCim(pb, this).also { add(it) }
fun CustomerService.addFromPb(pb: PBTariff): Tariff = toCim(pb, this).also { add(it) }

/************ Class for Java friendly usage ************/

class CustomerProtoToCim(private val customerService: CustomerService) : BaseProtoToCim(customerService) {
    fun addFromPb(pb: PBOrganisation): Organisation = customerService.addFromPb(pb)
    fun addFromPb(pb: PBCustomer): Customer = customerService.addFromPb(pb)
    fun addFromPb(pb: PBCustomerAgreement): CustomerAgreement = customerService.addFromPb(pb)
    fun addFromPb(pb: PBPricingStructure): PricingStructure = customerService.addFromPb(pb)
    fun addFromPb(pb: PBTariff): Tariff = customerService.addFromPb(pb)
}
