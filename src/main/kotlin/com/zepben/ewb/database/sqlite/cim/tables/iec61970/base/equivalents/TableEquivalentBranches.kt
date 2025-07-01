/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.equivalents

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL

@Suppress("PropertyName")
class TableEquivalentBranches : TableEquivalentEquipment() {

    val NEGATIVE_R12: Column = Column(++columnIndex, "negative_r12", "NUMBER", NULL)
    val NEGATIVE_R21: Column = Column(++columnIndex, "negative_r21", "NUMBER", NULL)
    val NEGATIVE_X12: Column = Column(++columnIndex, "negative_x12", "NUMBER", NULL)
    val NEGATIVE_X21: Column = Column(++columnIndex, "negative_x21", "NUMBER", NULL)
    val POSITIVE_R12: Column = Column(++columnIndex, "positive_r12", "NUMBER", NULL)
    val POSITIVE_R21: Column = Column(++columnIndex, "positive_r21", "NUMBER", NULL)
    val POSITIVE_X12: Column = Column(++columnIndex, "positive_x12", "NUMBER", NULL)
    val POSITIVE_X21: Column = Column(++columnIndex, "positive_x21", "NUMBER", NULL)
    val R: Column = Column(++columnIndex, "r", "NUMBER", NULL)
    val R21: Column = Column(++columnIndex, "r21", "NUMBER", NULL)
    val X: Column = Column(++columnIndex, "x", "NUMBER", NULL)
    val X21: Column = Column(++columnIndex, "x21", "NUMBER", NULL)
    val ZERO_R12: Column = Column(++columnIndex, "zero_r12", "NUMBER", NULL)
    val ZERO_R21: Column = Column(++columnIndex, "zero_r21", "NUMBER", NULL)
    val ZERO_X12: Column = Column(++columnIndex, "zero_x12", "NUMBER", NULL)
    val ZERO_X21: Column = Column(++columnIndex, "zero_x21", "NUMBER", NULL)

    override val name: String = "equivalent_branches"

}
