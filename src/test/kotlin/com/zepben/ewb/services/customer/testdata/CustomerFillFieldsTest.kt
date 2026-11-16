/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.customer.testdata

import com.zepben.ewb.services.BaseFillFieldsTest
import com.zepben.ewb.services.customer.CustomerService

class CustomerFillFieldsTest: BaseFillFieldsTest(CustomerService(), Class.forName("${CustomerFillFieldsTest::class.java.packageName}.FillFieldsKt"))
