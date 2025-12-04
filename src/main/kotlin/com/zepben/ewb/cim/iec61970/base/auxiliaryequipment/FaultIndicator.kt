/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.auxiliaryequipment

/**
 * A FaultIndicator is typically only an indicator (which may or may not be remotely monitored), and not a piece of equipment
 * that actually initiates a protection event. It is used for FLISR (Fault Location, Isolation and Restoration) purposes,
 * assisting with the dispatch of crews to "most likely" part of the network (i.e. assists with determining circuit section
 * where the fault most likely happened).
 */
class FaultIndicator(mRID: String) : AuxiliaryEquipment(mRID)
