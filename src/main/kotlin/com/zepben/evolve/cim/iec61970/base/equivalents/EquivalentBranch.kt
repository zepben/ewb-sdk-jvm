/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.equivalents

/**
 * The class represents equivalent branches. In cases where a transformer phase shift is modelled and the EquivalentBranch
 * is spanning the same nodes, the impedance quantities for the EquivalentBranch shall consider the needed phase shift.
 *
 * @property negativeR12 Negative sequence series resistance from terminal sequence 1 to terminal sequence 2. Used for short circuit data exchange according
 *                       to IEC 60909. EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property negativeR21 Negative sequence series resistance from terminal sequence 2 to terminal sequence 1. Used for short circuit data exchange according
 *                       to IEC 60909. EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property negativeX12 Negative sequence series reactance from terminal sequence 1 to terminal sequence 2. Used for short circuit data exchange according
 *                       to IEC 60909. Usage : EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property negativeX21 Negative sequence series reactance from terminal sequence 2 to terminal sequence 1. Used for short circuit data exchange according
 *                       to IEC 60909. Usage: EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property positiveR12 Positive sequence series resistance from terminal sequence 1 to terminal sequence 2 . Used for short circuit data exchange according
 *                       to IEC 60909. EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property positiveR21 Positive sequence series resistance from terminal sequence 2 to terminal sequence 1. Used for short circuit data exchange according
 *                       to IEC 60909. EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property positiveX12 Positive sequence series reactance from terminal sequence 1 to terminal sequence 2. Used for short circuit data exchange according
 *                       to IEC 60909. Usage : EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property positiveX21 Positive sequence series reactance from terminal sequence 2 to terminal sequence 1. Used for short circuit data exchange according
 *                       to IEC 60909. Usage : EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property r Positive sequence series resistance of the reduced branch.
 * @property r21 Resistance from terminal sequence 2 to terminal sequence 1 .Used for steady state power flow. This attribute is optional and represent
 *               unbalanced network such as off-nominal phase shifter. If only EquivalentBranch.r is given, then EquivalentBranch.r21 is assumed equal
 *               to EquivalentBranch.r. Usage rule : EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property x Positive sequence series reactance of the reduced branch.
 * @property x21 Reactance from terminal sequence 2 to terminal sequence 1. Used for steady state power flow. This attribute is optional and represents
 *               an unbalanced network such as off-nominal phase shifter. If only EquivalentBranch.x is given, then EquivalentBranch.x21 is assumed
 *               equal to EquivalentBranch.x. Usage rule: EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property zeroR12 Zero sequence series resistance from terminal sequence 1 to terminal sequence 2. Used for short circuit data exchange according to
 *                   IEC 60909. EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property zeroR21 Zero sequence series resistance from terminal sequence 2 to terminal sequence 1. Used for short circuit data exchange according to
 *                   IEC 60909. Usage : EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property zeroX12 Zero sequence series reactance from terminal sequence 1 to terminal sequence 2. Used for short circuit data exchange according to
 *                   IEC 60909. Usage : EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property zeroX21 Zero sequence series reactance from terminal sequence 2 to terminal sequence 1. Used for short circuit data exchange according to
 *                   IEC 60909. Usage : EquivalentBranch is a result of network reduction prior to the data exchange.
 */
class EquivalentBranch @JvmOverloads constructor(mRID: String = "") : EquivalentEquipment(mRID) {

    var negativeR12: Double? = null
    var negativeR21: Double? = null
    var negativeX12: Double? = null
    var negativeX21: Double? = null
    var positiveR12: Double? = null
    var positiveR21: Double? = null
    var positiveX12: Double? = null
    var positiveX21: Double? = null
    var r: Double? = null
    var r21: Double? = null
    var x: Double? = null
    var x21: Double? = null
    var zeroR12: Double? = null
    var zeroR21: Double? = null
    var zeroX12: Double? = null
    var zeroX21: Double? = null

}
