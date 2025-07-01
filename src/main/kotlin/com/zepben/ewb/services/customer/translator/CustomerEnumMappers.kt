/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.customer.translator

import com.zepben.ewb.cim.iec61968.customers.CustomerKind
import com.zepben.ewb.services.common.translator.EnumMapper
import com.zepben.protobuf.cim.iec61968.customers.CustomerKind as PBCustomerKind

internal val mapCustomerKind = EnumMapper(CustomerKind.entries, PBCustomerKind.entries)
