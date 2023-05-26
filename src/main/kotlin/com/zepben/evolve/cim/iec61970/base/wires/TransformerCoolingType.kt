/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires


/**
 * Transformer cooling types.
 *
 * @property UNKNOWN_COOLING_TYPE Default
 * @property ONAN Oil natural, air natural
 * @property ONAF Oil natural, air forced
 * @property OFAF Oil forced, air forced
 * @property OFWF Oil forced, water forced
 * @property ODAF Oil directed, air forced
 * @property KNAN Non-mineral oil natural, air natural
 * @property KNAF Non-mineral oil natural, air forced
 * @property KFAF Non-mineral oil forced, air forced
 * @property KFWF Non-mineral oil forced, water forced
 * @property KDAF Non-mineral oil directed, air forced
 */
enum class TransformerCoolingType {

    UNKNOWN_COOLING_TYPE,
    ONAN,
    ONAF,
    OFAF,
    OFWF,
    ODAF,
    KNAN,
    KNAF,
    KFAF,
    KFWF,
    KDAF

}
