/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.auxiliaryequipment

/**
 * This class describes devices that transform a measured quantity into signals that can be presented at displays,
 * used in control or be recorded.
 */
class Sensor @JvmOverloads constructor(mRID: String = "") : AuxiliaryEquipment(mRID)
