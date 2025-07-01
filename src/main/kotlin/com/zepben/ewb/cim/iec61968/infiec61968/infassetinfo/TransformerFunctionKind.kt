/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo

/**
 * Function of a transformer.
 */
@Suppress("EnumEntryName")
enum class TransformerFunctionKind {

    /**
     * Unknown type of transformer.
     */
    UNKNOWN,

    /**
     * A transformer that changes the voltage magnitude at a certain point in the power system
     */
    voltageRegulator,

    /**
     * A transformer that provides the final voltage transformation in the electric power distribution system.
     */
    distributionTransformer,

    /**
     * A transformer whose primary purpose is to isolate circuits.
     */
    isolationTransformer,

    /**
     * A transformer with a special winding divided into several sections enabling the voltage to be varied at will. (IEC ref 811-26-04).
     */
    autotransformer,

    /**
     *
     */
    powerTransformer,

    /**
     *
     */
    secondaryTransformer,

    /**
     * Another type of transformer.
     */
    other,

}
