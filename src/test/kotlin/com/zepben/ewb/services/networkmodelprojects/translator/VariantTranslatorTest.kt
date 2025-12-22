/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.networkmodelprojects.translator

import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import com.zepben.ewb.services.common.translator.TranslatorTestBase
import com.zepben.ewb.services.networkmodelproject.NetworkModelProjectService
import com.zepben.ewb.services.networkmodelproject.translator.NetworkModelProjectCimToProto
import com.zepben.ewb.services.networkmodelproject.translator.addFromPb

internal class VariantTranslatorTest : TranslatorTestBase<NetworkModelProjectService>(
    ::NetworkModelProjectService,
    TODO()

){
    private val vsToPb = NetworkModelProjectCimToProto()

    override val validationInfo = listOf(
        ValidationInfo<DataSet>(::ChangeSet, { fillFields(it) }, { addFromPb(vsToPb.toPb(it)) }),  // TODO: datasets not being IO's bites me in the ass again.

    )
    TODO()
}