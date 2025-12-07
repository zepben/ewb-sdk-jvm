/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant.translator

import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.DependencyKind
import com.zepben.ewb.services.common.translator.EnumMapper
import com.zepben.protobuf.cim.iec61970.infiec61970.infpart303.networkmodelprojects.DependencyKind as PBDependencyKind

internal val mapDependencyKind = EnumMapper(DependencyKind.entries, PBDependencyKind.entries)