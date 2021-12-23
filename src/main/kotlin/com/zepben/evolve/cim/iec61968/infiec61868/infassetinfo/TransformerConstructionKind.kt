/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.infiec61868.infassetinfo

/**
 * Kind of transformer construction.
 *
 * @property unknown
 * @property onePhase
 * @property threePhase
 * @property aerial
 * @property overhead
 * @property dryType
 * @property network
 * @property padmountDeadFront
 * @property padmountFeedThrough
 * @property padmountLiveFront
 * @property padmountLoopThrough
 * @property padmounted
 * @property subway
 * @property underground
 * @property vault
 * @property vaultThreePhase
 */
@Suppress("EnumEntryName")
enum class TransformerConstructionKind {

    unknown,
    onePhase,
    threePhase,
    aerial,
    overhead,
    dryType,
    network,
    padmountDeadFront,
    padmountFeedThrough,
    padmountLiveFront,
    padmountLoopThrough,
    padmounted,
    subway,
    underground,
    vault,
    vaultThreePhase

}
