/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.common

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `Document` columns required for the database table.
 *
 * @property TITLE Document title.
 * @property CREATED_DATE_TIME Date and time that this document was created.
 * @property AUTHOR_NAME Name of the author of this document.
 * @property TYPE Utility-specific classification of this document, according to its corporate standards, practices, and existing IT systems (e.g., for management of assets, maintenance, work, outage, customers, etc.).
 * @property STATUS Status of subject matter (e.g., Agreement, Work) this document represents.
 * @property COMMENT Free text comment.
 */
@Suppress("PropertyName")
abstract class TableDocuments : TableIdentifiedObjects() {

    val TITLE: Column = Column(++columnIndex, "title", Column.Type.STRING, NULL)
    val CREATED_DATE_TIME: Column = Column(++columnIndex, "created_date_time", Column.Type.STRING, NULL)
    val AUTHOR_NAME: Column = Column(++columnIndex, "author_name", Column.Type.STRING, NULL)
    val TYPE: Column = Column(++columnIndex, "type", Column.Type.STRING, NULL)
    val STATUS: Column = Column(++columnIndex, "status", Column.Type.STRING, NULL)
    val COMMENT: Column = Column(++columnIndex, "comment", Column.Type.STRING, NULL)

}
