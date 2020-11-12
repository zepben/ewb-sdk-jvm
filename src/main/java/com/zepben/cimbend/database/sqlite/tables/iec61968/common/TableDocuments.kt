/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61968.common

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL
import com.zepben.cimbend.database.Column.Nullable.NULL
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects

@Suppress("PropertyName")
abstract class TableDocuments : TableIdentifiedObjects() {

    val TITLE = Column(++columnIndex, "title", "TEXT", NOT_NULL)
    val CREATED_DATE_TIME = Column(++columnIndex, "created_date_time", "TEXT", NULL)
    val AUTHOR_NAME = Column(++columnIndex, "author_name", "TEXT", NOT_NULL)
    val TYPE = Column(++columnIndex, "type", "TEXT", NOT_NULL)
    val STATUS = Column(++columnIndex, "status", "TEXT", NOT_NULL)
    val COMMENT = Column(++columnIndex, "comment", "TEXT", NOT_NULL)

}
