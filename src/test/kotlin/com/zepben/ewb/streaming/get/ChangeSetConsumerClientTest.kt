/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.services.variant.ChangeSetServices
import com.zepben.ewb.services.variant.VariantService
import com.zepben.ewb.services.variant.VariantServiceComparator
import com.zepben.ewb.services.variant.testdata.fillFields
import com.zepben.ewb.services.variant.translator.toPb
import com.zepben.ewb.streaming.get.testservices.TestVariantConsumerService
import com.zepben.protobuf.vc.GetChangeSetRequest
import com.zepben.protobuf.vc.GetChangeSetResponse
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.isA
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify


class ChangeSetConsumerClientTest {



}