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
import com.zepben.cimbend.cim.iec61968.customers.Customer
import com.zepben.cimbend.cim.iec61968.customers.CustomerAgreement
import com.zepben.cimbend.cim.iec61968.customers.PricingStructure
import com.zepben.cimbend.cim.iec61968.customers.Tariff
import com.zepben.cimbend.common.translator.BaseCimToProto
import com.zepben.cimbend.common.translator.toPb
import com.zepben.protobuf.cim.iec61968.common.Agreement as PBAgreement
import com.zepben.protobuf.cim.iec61968.customers.Customer as PBCustomer
import com.zepben.protobuf.cim.iec61968.customers.CustomerAgreement as PBCustomerAgreement
import com.zepben.protobuf.cim.iec61968.customers.CustomerKind as PBCustomerKind
import com.zepben.protobuf.cim.iec61968.customers.PricingStructure as PBPricingStructure
import com.zepben.protobuf.cim.iec61968.customers.Tariff as PBTariff

/************ IEC61968 COMMON ************/
fun toPb(cim: Agreement, pb: PBAgreement.Builder): PBAgreement.Builder =
    pb.apply { toPb(cim, docBuilder) }

/************ IEC61968 CUSTOMERS ************/
fun toPb(cim: Customer, pb: PBCustomer.Builder): PBCustomer.Builder =
    pb.apply {
        kind = PBCustomerKind.valueOf(cim.kind.name)
        clearCustomerAgreementMRIDs()
        cim.agreements.forEach { addCustomerAgreementMRIDs(it.mRID) }
        numEndDevices = cim.numEndDevices
        toPb(cim, orBuilder)
    }

fun toPb(cim: CustomerAgreement, pb: PBCustomerAgreement.Builder): PBCustomerAgreement.Builder =
    pb.apply {
        cim.customer?.let { customerMRID = it.mRID } ?: clearCustomerMRID()
        clearPricingStructureMRIDs()
        cim.pricingStructures.forEach { addPricingStructureMRIDs(it.mRID) }
        toPb(cim, agrBuilder)
    }

fun toPb(cim: PricingStructure, pb: PBPricingStructure.Builder): PBPricingStructure.Builder =
    pb.apply {
        clearTariffMRIDs()
        cim.tariffs.forEach { addTariffMRIDs(it.mRID) }
        toPb(cim, docBuilder)
    }

fun toPb(cim: Tariff, pb: PBTariff.Builder): PBTariff.Builder =
    pb.apply { toPb(cim, docBuilder) }

/************ Extension ************/

fun Customer.toPb(): PBCustomer = toPb(this, PBCustomer.newBuilder()).build()
fun CustomerAgreement.toPb(): PBCustomerAgreement = toPb(this, PBCustomerAgreement.newBuilder()).build()
fun PricingStructure.toPb(): PBPricingStructure = toPb(this, PBPricingStructure.newBuilder()).build()
fun Tariff.toPb(): PBTariff = toPb(this, PBTariff.newBuilder()).build()

/************ Class for Java friendly usage ************/

class CustomerCimToProto() : BaseCimToProto() {
    fun toPb(cim: Customer): PBCustomer = cim.toPb()
    fun toPb(cim: CustomerAgreement): PBCustomerAgreement = cim.toPb()
    fun toPb(cim: PricingStructure): PBPricingStructure = cim.toPb()
    fun toPb(cim: Tariff): PBTariff = cim.toPb()
}

