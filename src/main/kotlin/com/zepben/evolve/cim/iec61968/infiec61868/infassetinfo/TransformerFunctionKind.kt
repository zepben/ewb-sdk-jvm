/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.infiec61868.infassetinfo

/**
 * Function of a transformer.
 *
 * @property other Another type of transformer.
 * @property voltageRegulator A transformer that changes the voltage magnitude at a certain point in the power system
 * @property distributionTransformer A transformer that provides the final voltage transformation in the electric power distribution system.
 * @property isolationTransformer A transformer whose primary purpose is to isolate circuits.
 * @property autotransformer A transformer with a special winding divided into several sections enabling the voltage to be varied at will. (IEC ref 811-26-04).
 * @property powerTransformer
 * @property secondaryTransformer

 */
@Suppress("EnumEntryName")
enum class TransformerFunctionKind {

    other,
    voltageRegulator,
    distributionTransformer,
    isolationTransformer,
    autotransformer,
    powerTransformer,
    secondaryTransformer

}
