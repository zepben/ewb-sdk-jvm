/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.customer.translator

import com.google.protobuf.NullValue
import com.zepben.ewb.cim.iec61968.common.Agreement
import com.zepben.ewb.cim.iec61968.customers.Customer
import com.zepben.ewb.cim.iec61968.customers.CustomerAgreement
import com.zepben.ewb.cim.iec61968.customers.PricingStructure
import com.zepben.ewb.cim.iec61968.customers.Tariff
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.services.common.translator.BaseCimToProto
import com.zepben.ewb.services.common.translator.toPb
import com.zepben.ewb.services.customer.whenCustomerServiceObject
import com.zepben.protobuf.cc.CustomerIdentifiedObject
import com.zepben.protobuf.cim.iec61968.common.Agreement as PBAgreement
import com.zepben.protobuf.cim.iec61968.customers.Customer as PBCustomer
import com.zepben.protobuf.cim.iec61968.customers.CustomerAgreement as PBCustomerAgreement
import com.zepben.protobuf.cim.iec61968.customers.PricingStructure as PBPricingStructure
import com.zepben.protobuf.cim.iec61968.customers.Tariff as PBTariff

/**
 * Convert the [IdentifiedObject] to a [CustomerIdentifiedObject] representation.
 */
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
// # IEC61968 Common #
// ###################

/**
 * Convert the [Agreement] into its protobuf counterpart.
 *
 * @param cim The [Agreement] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Agreement, pb: PBAgreement.Builder): PBAgreement.Builder =
    pb.apply { toPb(cim, docBuilder) }

// ######################
// # IEC61968 Customers #
// ######################

/**
 * Convert the [Customer] into its protobuf counterpart.
 *
 * @param cim The [Customer] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Customer, pb: PBCustomer.Builder): PBCustomer.Builder =
    pb.apply {
        kind = mapCustomerKind.toPb(cim.kind)
        clearCustomerAgreementMRIDs()
        cim.agreements.forEach { addCustomerAgreementMRIDs(it.mRID) }
        cim.numEndDevices?.also { numEndDevicesSet = it } ?: run { numEndDevicesNull = NullValue.NULL_VALUE }
        cim.specialNeed?.let { specialNeedSet = it } ?: run { specialNeedNull = NullValue.NULL_VALUE }
        toPb(cim, orBuilder)
    }

/**
 * Convert the [CustomerAgreement] into its protobuf counterpart.
 *
 * @param cim The [CustomerAgreement] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: CustomerAgreement, pb: PBCustomerAgreement.Builder): PBCustomerAgreement.Builder =
    pb.apply {
        cim.customer?.let { customerMRID = it.mRID } ?: clearCustomerMRID()
        clearPricingStructureMRIDs()
        cim.pricingStructures.forEach { addPricingStructureMRIDs(it.mRID) }
        toPb(cim, agrBuilder)
    }

/**
 * Convert the [PricingStructure] into its protobuf counterpart.
 *
 * @param cim The [PricingStructure] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PricingStructure, pb: PBPricingStructure.Builder): PBPricingStructure.Builder =
    pb.apply {
        clearTariffMRIDs()
        cim.tariffs.forEach { addTariffMRIDs(it.mRID) }
        toPb(cim, docBuilder)
    }

/**
 * Convert the [Tariff] into its protobuf counterpart.
 *
 * @param cim The [Tariff] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Tariff, pb: PBTariff.Builder): PBTariff.Builder =
    pb.apply { toPb(cim, docBuilder) }

/**
 * An extension for converting any [Customer] into its protobuf counterpart.
 */
fun Customer.toPb(): PBCustomer = toPb(this, PBCustomer.newBuilder()).build()

/**
 * An extension for converting any [CustomerAgreement] into its protobuf counterpart.
 */
fun CustomerAgreement.toPb(): PBCustomerAgreement = toPb(this, PBCustomerAgreement.newBuilder()).build()

/**
 * An extension for converting any [PricingStructure] into its protobuf counterpart.
 */
fun PricingStructure.toPb(): PBPricingStructure = toPb(this, PBPricingStructure.newBuilder()).build()

/**
 * An extension for converting any [Tariff] into its protobuf counterpart.
 */
fun Tariff.toPb(): PBTariff = toPb(this, PBTariff.newBuilder()).build()

// #################################
// # Class for Java friendly usage #
// #################################

/**
 * A helper class for Java friendly convertion from CIM objects to their protobuf counterparts.
 */
class CustomerCimToProto : BaseCimToProto() {

    // ######################
    // # IEC61968 Customers #
    // ######################

    /**
     * Convert the [Customer] into its protobuf counterpart.
     *
     * @param cim The [Customer] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Customer): PBCustomer = cim.toPb()

    /**
     * Convert the [CustomerAgreement] into its protobuf counterpart.
     *
     * @param cim The [CustomerAgreement] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: CustomerAgreement): PBCustomerAgreement = cim.toPb()

    /**
     * Convert the [PricingStructure] into its protobuf counterpart.
     *
     * @param cim The [PricingStructure] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PricingStructure): PBPricingStructure = cim.toPb()

    /**
     * Convert the [Tariff] into its protobuf counterpart.
     *
     * @param cim The [Tariff] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Tariff): PBTariff = cim.toPb()

}
