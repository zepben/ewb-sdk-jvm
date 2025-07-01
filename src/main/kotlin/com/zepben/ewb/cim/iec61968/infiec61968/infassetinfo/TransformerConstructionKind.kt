/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo

/**
 * Kind of transformer construction.
 */
@Suppress("EnumEntryName")
enum class TransformerConstructionKind {

    /**
     *
     */
    unknown,

    /**
     *
     */
    onePhase,

    /**
     *
     */
    threePhase,

    /**
     *
     */
    aerial,

    /**
     *
     */
    overhead,

    /**
     *
     */
    dryType,

    /**
     *
     */
    network,

    /**
     *
     */
    padmountDeadFront,

    /**
     *
     */
    padmountFeedThrough,

    /**
     *
     */
    padmountLiveFront,

    /**
     *
     */
    padmountLoopThrough,

    /**
     *
     */
    padmounted,

    /**
     *
     */
    subway,

    /**
     *
     */
    underground,

    /**
     *
     */
    vault,

    /**
     *
     */
    vaultThreePhase

}
