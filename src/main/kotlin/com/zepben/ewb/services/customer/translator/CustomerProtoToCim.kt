/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.customer.translator

import com.zepben.ewb.cim.iec61968.common.Agreement
import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.customers.*
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.services.common.Resolvers
import com.zepben.ewb.services.common.translator.BaseProtoToCim
import com.zepben.ewb.services.common.translator.toCim
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.protobuf.cim.iec61968.common.Agreement as PBAgreement
import com.zepben.protobuf.cim.iec61968.common.Organisation as PBOrganisation
import com.zepben.protobuf.cim.iec61968.customers.Customer as PBCustomer
import com.zepben.protobuf.cim.iec61968.customers.CustomerAgreement as PBCustomerAgreement
import com.zepben.protobuf.cim.iec61968.customers.PricingStructure as PBPricingStructure
import com.zepben.protobuf.cim.iec61968.customers.Tariff as PBTariff
import com.zepben.protobuf.cim.iec61970.base.core.NameType as PBNameType

// ###################
// # IEC61968 Common #
// ###################

private fun toCim(pb: PBAgreement, cim: Agreement, customerService: CustomerService): Agreement =
    cim.also { toCim(pb.doc, it, customerService) }

/**
 * An extension to add a converted copy of the protobuf [PBOrganisation] to the [CustomerService].
 */
fun CustomerService.addFromPb(pb: PBOrganisation): Organisation? = tryAddOrNull(toCim(pb, this))

// ######################
// # IEC61968 Customers #
// ######################

/**
 * Convert the protobuf [PBCustomer] into its CIM counterpart.
 *
 * @param pb The protobuf [PBCustomer] to convert.
 * @param customerService The [CustomerService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Customer].
 */
fun toCim(pb: PBCustomer, customerService: CustomerService): Customer =
    Customer(pb.mRID()).apply {
        kind = mapCustomerKind.toCim(pb.kind)
        pb.customerAgreementMRIDsList.forEach { agreementMRID ->
            customerService.resolveOrDeferReference(Resolvers.agreements(this), agreementMRID)
        }
        numEndDevices = pb.numEndDevicesSet.takeUnless { pb.hasNumEndDevicesNull() }
        specialNeed = pb.specialNeedSet.takeUnless { pb.hasSpecialNeedNull() }
        toCim(pb.or, this, customerService)
    }

/**
 * Convert the protobuf [PBCustomerAgreement] into its CIM counterpart.
 *
 * @param pb The protobuf [PBCustomerAgreement] to convert.
 * @param customerService The [CustomerService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [CustomerAgreement].
 */
fun toCim(pb: PBCustomerAgreement, customerService: CustomerService): CustomerAgreement =
    CustomerAgreement(pb.mRID()).apply {
        customerService.resolveOrDeferReference(Resolvers.customer(this), pb.customerMRID)

        pb.pricingStructureMRIDsList.forEach {
            customerService.resolveOrDeferReference(Resolvers.pricingStructures(this), it)
        }
        toCim(pb.agr, this, customerService)
    }

/**
 * Convert the protobuf [PBPricingStructure] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPricingStructure] to convert.
 * @param customerService The [CustomerService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PricingStructure].
 */
fun toCim(pb: PBPricingStructure, customerService: CustomerService): PricingStructure =
    PricingStructure(pb.mRID()).apply {
        pb.tariffMRIDsList.forEach {
            customerService.resolveOrDeferReference(Resolvers.tariffs(this), it)
        }
        toCim(pb.doc, this, customerService)
    }

/**
 * Convert the protobuf [PBTariff] into its CIM counterpart.
 *
 * @param pb The protobuf [PBTariff] to convert.
 * @param customerService The [CustomerService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Tariff].
 */
fun toCim(pb: PBTariff, customerService: CustomerService): Tariff =
    Tariff(pb.mRID()).apply {
        toCim(pb.doc, this, customerService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBCustomer] to the [CustomerService].
 */
fun CustomerService.addFromPb(pb: PBCustomer): Customer? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBCustomerAgreement] to the [CustomerService].
 */
fun CustomerService.addFromPb(pb: PBCustomerAgreement): CustomerAgreement? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBPricingStructure] to the [CustomerService].
 */
fun CustomerService.addFromPb(pb: PBPricingStructure): PricingStructure? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBTariff] to the [CustomerService].
 */
fun CustomerService.addFromPb(pb: PBTariff): Tariff? = tryAddOrNull(toCim(pb, this))

// ######################
// # IEC61970 Base Core #
// ######################

/**
 * An extension to add a converted copy of the protobuf [PBNameType] to the [CustomerService].
 */
fun CustomerService.addFromPb(pb: PBNameType): NameType = toCim(pb, this)

// #################################
// # Class for Java friendly usage #
// #################################

/**
 * A helper class for Java friendly convertion from protobuf objects to their CIM counterparts.
 *
 * @property customerService The [CustomerService] all converted objects should be added to.
 */
class CustomerProtoToCim(private val customerService: CustomerService) : BaseProtoToCim() {

    // ###################
    // # IEC61968 Common #
    // ###################

    /**
     * Add a converted copy of the protobuf [PBOrganisation] to the [CustomerService].
     *
     * @param pb The [PBOrganisation] to convert.
     * @return The converted [Organisation]
     */
    fun addFromPb(pb: PBOrganisation): Organisation? = customerService.addFromPb(pb)

    // ######################
    // # IEC61968 Customers #
    // ######################

    /**
     * Add a converted copy of the protobuf [PBCustomer] to the [CustomerService].
     *
     * @param pb The [PBCustomer] to convert.
     * @return The converted [Customer]
     */
    fun addFromPb(pb: PBCustomer): Customer? = customerService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBCustomerAgreement] to the [CustomerService].
     *
     * @param pb The [PBCustomerAgreement] to convert.
     * @return The converted [CustomerAgreement]
     */
    fun addFromPb(pb: PBCustomerAgreement): CustomerAgreement? = customerService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBPricingStructure] to the [CustomerService].
     *
     * @param pb The [PBPricingStructure] to convert.
     * @return The converted [PricingStructure]
     */
    fun addFromPb(pb: PBPricingStructure): PricingStructure? = customerService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBTariff] to the [CustomerService].
     *
     * @param pb The [PBTariff] to convert.
     * @return The converted [Tariff]
     */
    fun addFromPb(pb: PBTariff): Tariff? = customerService.addFromPb(pb)

    // ######################
    // # IEC61970 Base Core #
    // ######################

    /**
     * Add a converted copy of the protobuf [PBNameType] to the [CustomerService].
     *
     * @param pb The [PBNameType] to convert.
     * @return The converted [NameType]
     */
    fun addFromPb(pb: PBNameType): NameType = customerService.addFromPb(pb)

}
