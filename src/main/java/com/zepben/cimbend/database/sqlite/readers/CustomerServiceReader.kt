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
package com.zepben.cimbend.database.sqlite.readers

import com.zepben.cimbend.cim.iec61968.common.Agreement
import com.zepben.cimbend.cim.iec61968.customers.*
import com.zepben.cimbend.common.extensions.ensureGet
import com.zepben.cimbend.common.extensions.typeNameAndMRID
import com.zepben.cimbend.customer.CustomerService
import com.zepben.cimbend.database.sqlite.tables.associations.TableCustomerAgreementsPricingStructures
import com.zepben.cimbend.database.sqlite.tables.associations.TablePricingStructuresTariffs
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableAgreements
import com.zepben.cimbend.database.sqlite.tables.iec61968.customers.TableCustomerAgreements
import com.zepben.cimbend.database.sqlite.tables.iec61968.customers.TableCustomers
import com.zepben.cimbend.database.sqlite.tables.iec61968.customers.TablePricingStructures
import com.zepben.cimbend.database.sqlite.tables.iec61968.customers.TableTariffs
import java.sql.ResultSet

@Suppress("SameParameterValue")
class CustomerServiceReader(private val customerService: CustomerService) : BaseServiceReader(customerService) {

    /************ IEC61968 COMMON ************/
    private fun loadAgreement(agreement: Agreement, table: TableAgreements, resultSet: ResultSet): Boolean {
        return loadDocument(agreement, table, resultSet)
    }

    /************ IEC61968 CUSTOMERS ************/
    fun load(table: TableCustomers, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val customer = Customer(setLastMRID(resultSet.getString(table.MRID.queryIndex()))).apply {
            kind = CustomerKind.valueOf(resultSet.getString(table.KIND.queryIndex()))
            numEndDevices = resultSet.getInt(table.NUM_END_DEVICES.queryIndex())
        }

        return loadOrganisationRole(customer, table, resultSet) && customerService.addOrThrow(customer)
    }

    fun load(table: TableCustomerAgreements, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val mRID = setLastMRID(resultSet.getString(table.MRID.queryIndex()))
        val customerAgreement = CustomerAgreement(mRID).apply {
            customer = customerService.ensureGet(resultSet.getString(table.CUSTOMER_MRID.queryIndex()), typeNameAndMRID())
            customer?.addAgreement(this)
        }

        return loadAgreement(customerAgreement, table, resultSet) && customerService.addOrThrow(customerAgreement)
    }

    fun load(table: TablePricingStructures, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val pricingStructure = PricingStructure(setLastMRID(resultSet.getString(table.MRID.queryIndex())))

        return loadDocument(pricingStructure, table, resultSet) && customerService.addOrThrow(pricingStructure)
    }

    fun load(table: TableTariffs, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val tariff = Tariff(setLastMRID(resultSet.getString(table.MRID.queryIndex())))

        return loadDocument(tariff, table, resultSet) && customerService.addOrThrow(tariff)
    }

    /************ ASSOCIATIONS ************/
    fun load(table: TableCustomerAgreementsPricingStructures, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val customerAgreementMRID = setLastMRID(resultSet.getString(table.CUSTOMER_AGREEMENT_MRID.queryIndex()))
        setLastMRID("${customerAgreementMRID}-to-UNKNOWN")

        val pricingStructureMRID = resultSet.getString(table.PRICING_STRUCTURE_MRID.queryIndex())
        val id = setLastMRID("${customerAgreementMRID}-to-${pricingStructureMRID}")

        val typeNameAndMRID = "customer agreement to pricing structure association $id";
        val customerAgreement = customerService.ensureGet<CustomerAgreement>(customerAgreementMRID, typeNameAndMRID)
        val pricingStructure = customerService.ensureGet<PricingStructure>(pricingStructureMRID, typeNameAndMRID)

        pricingStructure?.let { customerAgreement?.addPricingStructure(it) }

        return true
    }

    fun load(table: TablePricingStructuresTariffs, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val pricingStructureMRID = setLastMRID(resultSet.getString(table.PRICING_STRUCTURE_MRID.queryIndex()))
        setLastMRID("${pricingStructureMRID}-to-UNKNOWN")

        val tariffMRID = resultSet.getString(table.TARIFF_MRID.queryIndex())
        val id = setLastMRID("${pricingStructureMRID}-to-${tariffMRID}")

        val typeNameAndMRID = "pricing structure to tariff association $id";
        val pricingStructure = customerService.ensureGet<PricingStructure>(pricingStructureMRID, typeNameAndMRID)
        val tariff = customerService.ensureGet<Tariff>(tariffMRID, typeNameAndMRID)

        tariff?.let { pricingStructure?.addTariff(it) }

        return true
    }
}
