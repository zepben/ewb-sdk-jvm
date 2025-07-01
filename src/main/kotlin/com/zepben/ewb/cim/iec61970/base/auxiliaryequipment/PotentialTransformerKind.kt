/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.auxiliaryequipment

/**
 * The construction kind of the potential transformer.
 */
@Suppress("EnumEntryName")
enum class PotentialTransformerKind {

    /**
     * The construction type of the potential transformer is unknown.
     */
    UNKNOWN,

    /**
     * The potential transformer is using induction coils to create secondary voltage.
     */
    inductive,

    /**
     * The potential transformer is using capacitive coupling to create secondary voltage.
     */
    capacitiveCoupling

}
