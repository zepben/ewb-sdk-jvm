/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.customer.translator

import com.zepben.evolve.cim.iec61968.common.Agreement
import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.customers.CustomerAgreement
import com.zepben.evolve.cim.iec61968.customers.PricingStructure
import com.zepben.evolve.cim.iec61968.customers.Tariff
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.services.common.UNKNOWN_INT
import com.zepben.evolve.services.common.translator.BaseCimToProto
import com.zepben.evolve.services.common.translator.toPb
import com.zepben.evolve.services.customer.whenCustomerServiceObject
import com.zepben.protobuf.cc.CustomerIdentifiedObject
import com.zepben.protobuf.cim.iec61968.common.Agreement as PBAgreement
import com.zepben.protobuf.cim.iec61968.customers.Customer as PBCustomer
import com.zepben.protobuf.cim.iec61968.customers.CustomerAgreement as PBCustomerAgreement
import com.zepben.protobuf.cim.iec61968.customers.CustomerKind as PBCustomerKind
import com.zepben.protobuf.cim.iec61968.customers.PricingStructure as PBPricingStructure
import com.zepben.protobuf.cim.iec61968.customers.Tariff as PBTariff

fun customerIdentifiedObject(identifiedObject: IdentifiedObject): CustomerIdentifiedObject =
    CustomerIdentifiedObject.newBuilder().apply {
        whenCustomerServiceObject(
            identifiedObject,
            isCustomer = { customer = it.toPb() },
            isCustomerAgreement = { customerAgreement = it.toPb() },
            isOrganisation = { organisation = it.toPb() },
            isPricingStructure = { pricingStructure = it.toPb() },
            isTariff = { tariff = it.toPb() },
        )
    }.build()

// ###################
// # IEC61968 COMMON #
// ###################

fun toPb(cim: Agreement, pb: PBAgreement.Builder): PBAgreement.Builder =
    pb.apply { toPb(cim, docBuilder) }

// ######################
// # IEC61968 CUSTOMERS #
// ######################

fun toPb(cim: Customer, pb: PBCustomer.Builder): PBCustomer.Builder =
    pb.apply {
        kind = PBCustomerKind.valueOf(cim.kind.name)
        clearCustomerAgreementMRIDs()
        cim.agreements.forEach { addCustomerAgreementMRIDs(it.mRID) }
        numEndDevices = cim.numEndDevices ?: UNKNOWN_INT
        cim.specialNeed?.let { specialNeed = it } ?: clearSpecialNeed()
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

fun Customer.toPb(): PBCustomer = toPb(this, PBCustomer.newBuilder()).build()
fun CustomerAgreement.toPb(): PBCustomerAgreement = toPb(this, PBCustomerAgreement.newBuilder()).build()
fun PricingStructure.toPb(): PBPricingStructure = toPb(this, PBPricingStructure.newBuilder()).build()
fun Tariff.toPb(): PBTariff = toPb(this, PBTariff.newBuilder()).build()

// #################################
// # Class for Java friendly usage #
// #################################

class CustomerCimToProto : BaseCimToProto() {

    // IEC61968 CUSTOMERS
    fun toPb(cim: Customer): PBCustomer = cim.toPb()
    fun toPb(cim: CustomerAgreement): PBCustomerAgreement = cim.toPb()
    fun toPb(cim: PricingStructure): PBPricingStructure = cim.toPb()
    fun toPb(cim: Tariff): PBTariff = cim.toPb()

}
