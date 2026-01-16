/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.equivalents

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `EquivalentBranch` columns required for the database table.
 *
 * @property NEGATIVE_R12 Negative sequence series resistance from terminal sequence 1 to terminal sequence 2. Used for short circuit data exchange according
 *                       to IEC 60909. EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property NEGATIVE_R21 Negative sequence series resistance from terminal sequence 2 to terminal sequence 1. Used for short circuit data exchange according
 *                       to IEC 60909. EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property NEGATIVE_X12 Negative sequence series reactance from terminal sequence 1 to terminal sequence 2. Used for short circuit data exchange according
 *                       to IEC 60909. Usage : EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property NEGATIVE_X21 Negative sequence series reactance from terminal sequence 2 to terminal sequence 1. Used for short circuit data exchange according
 *                       to IEC 60909. Usage: EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property POSITIVE_R12 Positive sequence series resistance from terminal sequence 1 to terminal sequence 2 . Used for short circuit data exchange according
 *                       to IEC 60909. EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property POSITIVE_R21 Positive sequence series resistance from terminal sequence 2 to terminal sequence 1. Used for short circuit data exchange according
 *                       to IEC 60909. EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property POSITIVE_X12 Positive sequence series reactance from terminal sequence 1 to terminal sequence 2. Used for short circuit data exchange according
 *                       to IEC 60909. Usage : EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property POSITIVE_X21 Positive sequence series reactance from terminal sequence 2 to terminal sequence 1. Used for short circuit data exchange according
 *                       to IEC 60909. Usage : EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property R Positive sequence series resistance of the reduced branch.
 * @property R21 Resistance from terminal sequence 2 to terminal sequence 1 .Used for steady state power flow. This attribute is optional and represent
 *               unbalanced network such as off-nominal phase shifter. If only EquivalentBranch.r is given, then EquivalentBranch.r21 is assumed equal
 *               to EquivalentBranch.r. Usage rule : EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property X Positive sequence series reactance of the reduced branch.
 * @property X21 Reactance from terminal sequence 2 to terminal sequence 1. Used for steady state power flow. This attribute is optional and represents
 *               an unbalanced network such as off-nominal phase shifter. If only EquivalentBranch.x is given, then EquivalentBranch.x21 is assumed
 *               equal to EquivalentBranch.x. Usage rule: EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property ZERO_R12 Zero sequence series resistance from terminal sequence 1 to terminal sequence 2. Used for short circuit data exchange according to
 *                   IEC 60909. EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property ZERO_R21 Zero sequence series resistance from terminal sequence 2 to terminal sequence 1. Used for short circuit data exchange according to
 *                   IEC 60909. Usage : EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property ZERO_X12 Zero sequence series reactance from terminal sequence 1 to terminal sequence 2. Used for short circuit data exchange according to
 *                   IEC 60909. Usage : EquivalentBranch is a result of network reduction prior to the data exchange.
 * @property ZERO_X21 Zero sequence series reactance from terminal sequence 2 to terminal sequence 1. Used for short circuit data exchange according to
 *                   IEC 60909. Usage : EquivalentBranch is a result of network reduction prior to the data exchange.
 */
@Suppress("PropertyName")
class TableEquivalentBranches : TableEquivalentEquipment() {

    val NEGATIVE_R12: Column = Column(++columnIndex, "negative_r12", Column.Type.DOUBLE, NULL)
    val NEGATIVE_R21: Column = Column(++columnIndex, "negative_r21", Column.Type.DOUBLE, NULL)
    val NEGATIVE_X12: Column = Column(++columnIndex, "negative_x12", Column.Type.DOUBLE, NULL)
    val NEGATIVE_X21: Column = Column(++columnIndex, "negative_x21", Column.Type.DOUBLE, NULL)
    val POSITIVE_R12: Column = Column(++columnIndex, "positive_r12", Column.Type.DOUBLE, NULL)
    val POSITIVE_R21: Column = Column(++columnIndex, "positive_r21", Column.Type.DOUBLE, NULL)
    val POSITIVE_X12: Column = Column(++columnIndex, "positive_x12", Column.Type.DOUBLE, NULL)
    val POSITIVE_X21: Column = Column(++columnIndex, "positive_x21", Column.Type.DOUBLE, NULL)
    val R: Column = Column(++columnIndex, "r", Column.Type.DOUBLE, NULL)
    val R21: Column = Column(++columnIndex, "r21", Column.Type.DOUBLE, NULL)
    val X: Column = Column(++columnIndex, "x", Column.Type.DOUBLE, NULL)
    val X21: Column = Column(++columnIndex, "x21", Column.Type.DOUBLE, NULL)
    val ZERO_R12: Column = Column(++columnIndex, "zero_r12", Column.Type.DOUBLE, NULL)
    val ZERO_R21: Column = Column(++columnIndex, "zero_r21", Column.Type.DOUBLE, NULL)
    val ZERO_X12: Column = Column(++columnIndex, "zero_x12", Column.Type.DOUBLE, NULL)
    val ZERO_X21: Column = Column(++columnIndex, "zero_x21", Column.Type.DOUBLE, NULL)

    override val name: String = "equivalent_branches"

}
