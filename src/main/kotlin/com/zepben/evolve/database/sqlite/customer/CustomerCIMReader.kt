/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.customer

import com.zepben.evolve.cim.iec61968.common.Agreement
import com.zepben.evolve.cim.iec61968.customers.*
import com.zepben.evolve.database.sqlite.common.BaseCIMReader
import com.zepben.evolve.database.sqlite.extensions.getNullableInt
import com.zepben.evolve.database.sqlite.tables.associations.TableCustomerAgreementsPricingStructures
import com.zepben.evolve.database.sqlite.tables.associations.TablePricingStructuresTariffs
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableAgreements
import com.zepben.evolve.database.sqlite.tables.iec61968.customers.TableCustomerAgreements
import com.zepben.evolve.database.sqlite.tables.iec61968.customers.TableCustomers
import com.zepben.evolve.database.sqlite.tables.iec61968.customers.TablePricingStructures
import com.zepben.evolve.database.sqlite.tables.iec61968.customers.TableTariffs
import com.zepben.evolve.services.common.extensions.ensureGet
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.customer.CustomerService
import java.sql.ResultSet

@Suppress("SameParameterValue")
class CustomerCIMReader(private val customerService: CustomerService) : BaseCIMReader(customerService) {

    /************ IEC61968 COMMON ************/
    private fun loadAgreement(agreement: Agreement, table: TableAgreements, resultSet: ResultSet): Boolean {
        return loadDocument(agreement, table, resultSet)
    }

    /************ IEC61968 CUSTOMERS ************/
    fun load(table: TableCustomers, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val customer = Customer(setLastMRID(resultSet.getString(table.MRID.queryIndex))).apply {
            kind = CustomerKind.valueOf(resultSet.getString(table.KIND.queryIndex))
            numEndDevices = resultSet.getNullableInt(table.NUM_END_DEVICES.queryIndex)
        }

        return loadOrganisationRole(customer, table, resultSet) && customerService.addOrThrow(customer)
    }

    fun load(table: TableCustomerAgreements, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val mRID = setLastMRID(resultSet.getString(table.MRID.queryIndex))
        val customerAgreement = CustomerAgreement(mRID).apply {
            customer = customerService.ensureGet(resultSet.getString(table.CUSTOMER_MRID.queryIndex), typeNameAndMRID())
            customer?.addAgreement(this)
        }

        return loadAgreement(customerAgreement, table, resultSet) && customerService.addOrThrow(customerAgreement)
    }

    fun load(table: TablePricingStructures, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val pricingStructure = PricingStructure(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadDocument(pricingStructure, table, resultSet) && customerService.addOrThrow(pricingStructure)
    }

    fun load(table: TableTariffs, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val tariff = Tariff(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadDocument(tariff, table, resultSet) && customerService.addOrThrow(tariff)
    }

    /************ ASSOCIATIONS ************/
    fun load(table: TableCustomerAgreementsPricingStructures, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val customerAgreementMRID = setLastMRID(resultSet.getString(table.CUSTOMER_AGREEMENT_MRID.queryIndex))
        setLastMRID("${customerAgreementMRID}-to-UNKNOWN")

        val pricingStructureMRID = resultSet.getString(table.PRICING_STRUCTURE_MRID.queryIndex)
        val id = setLastMRID("${customerAgreementMRID}-to-${pricingStructureMRID}")

        val typeNameAndMRID = "customer agreement to pricing structure association $id"
        val customerAgreement = customerService.ensureGet<CustomerAgreement>(customerAgreementMRID, typeNameAndMRID)
        val pricingStructure = customerService.ensureGet<PricingStructure>(pricingStructureMRID, typeNameAndMRID)

        pricingStructure?.let { customerAgreement?.addPricingStructure(it) }

        return true
    }

    fun load(table: TablePricingStructuresTariffs, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val pricingStructureMRID = setLastMRID(resultSet.getString(table.PRICING_STRUCTURE_MRID.queryIndex))
        setLastMRID("${pricingStructureMRID}-to-UNKNOWN")

        val tariffMRID = resultSet.getString(table.TARIFF_MRID.queryIndex)
        val id = setLastMRID("${pricingStructureMRID}-to-${tariffMRID}")

        val typeNameAndMRID = "pricing structure to tariff association $id"
        val pricingStructure = customerService.ensureGet<PricingStructure>(pricingStructureMRID, typeNameAndMRID)
        val tariff = customerService.ensureGet<Tariff>(tariffMRID, typeNameAndMRID)

        tariff?.let { pricingStructure?.addTariff(it) }

        return true
    }

}
