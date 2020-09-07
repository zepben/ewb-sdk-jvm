/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61970.base.auxiliaryequipment

/**
 * A FaultIndicator is typically only an indicator (which may or may not be remotely monitored), and not a piece of equipment
 * that actually initiates a protection event. It is used for FLISR (Fault Location, Isolation and Restoration) purposes,
 * assisting with the dispatch of crews to "most likely" part of the network (i.e. assists with determining circuit section
 * where the fault most likely happened).
 */
class FaultIndicator @JvmOverloads constructor(mRID: String = "") : AuxiliaryEquipment(mRID)
