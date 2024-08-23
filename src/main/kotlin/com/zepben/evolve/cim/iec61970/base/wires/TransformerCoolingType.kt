/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires


/**
 * Transformer cooling types.
 */
enum class TransformerCoolingType {

    /**
     * Default
     */
    UNKNOWN_COOLING_TYPE,

    /**
     * Oil natural, air natural
     */
    ONAN,

    /**
     * Oil natural, air forced
     */
    ONAF,

    /**
     * Oil forced, air forced
     */
    OFAF,

    /**
     * Oil forced, water forced
     */
    OFWF,

    /**
     * Oil directed, air forced
     */
    ODAF,

    /**
     * Non-mineral oil natural, air natural
     */
    KNAN,

    /**
     * Non-mineral oil natural, air forced
     */
    KNAF,

    /**
     * Non-mineral oil forced, air forced
     */
    KFAF,

    /**
     * Non-mineral oil forced, water forced
     */
    KFWF,

    /**
     * Non-mineral oil directed, air forced
     */
    KDAF

}
