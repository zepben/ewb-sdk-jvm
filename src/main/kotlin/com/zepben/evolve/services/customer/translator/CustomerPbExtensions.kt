/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.customer.translator

import com.zepben.evolve.services.common.translator.mRID
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
