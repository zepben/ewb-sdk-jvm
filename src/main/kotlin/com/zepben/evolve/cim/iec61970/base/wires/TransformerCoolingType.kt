/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires


/**
 * @property
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