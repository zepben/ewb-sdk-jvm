/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.equivalents

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment

/**
 * The class represents equivalent objects that are the result of a network reduction. The class is the base for equivalent objects of different types.
 */
abstract class EquivalentEquipment(mRID: String = "") : ConductingEquipment(mRID)
