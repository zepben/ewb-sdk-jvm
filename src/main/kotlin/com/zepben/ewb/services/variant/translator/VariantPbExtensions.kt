/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant.translator

import com.zepben.protobuf.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.protobuf.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
import com.zepben.protobuf.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.protobuf.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet

fun NetworkModelProject.mRID(): String = nmpc.mRID()
fun NetworkModelProjectStage.mRID(): String = nmpc.mRID()
fun NetworkModelProjectComponent.mRID(): String = io.mrid
fun AnnotatedProjectDependency.mRID(): String = io.mrid

fun ChangeSet.mRID(): String = dataset.mrid