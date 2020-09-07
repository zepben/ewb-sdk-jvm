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

import com.zepben.cimbend.common.translator.mRID
import com.zepben.cimbend.common.translator.nameAndMRID
import com.zepben.protobuf.cim.iec61968.common.Agreement
import com.zepben.protobuf.cim.iec61968.customers.Customer
import com.zepben.protobuf.cim.iec61968.customers.CustomerAgreement
import com.zepben.protobuf.cim.iec61968.customers.PricingStructure
import com.zepben.protobuf.cim.iec61968.customers.Tariff


fun Customer.mRID(): String = or.mRID()
fun CustomerAgreement.mRID(): String = agr.mRID()
fun Agreement.mRID(): String = doc.mRID()
fun Tariff.mRID(): String = doc.mRID()
fun PricingStructure.mRID(): String = doc.mRID()

fun CustomerAgreement.nameAndMRID(): String = agr.nameAndMRID()
fun Agreement.nameAndMRID(): String = doc.nameAndMRID()
