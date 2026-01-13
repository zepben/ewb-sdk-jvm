/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.common

import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `OrganisationRole` columns required for the database table.
 *
 * @property ORGANISATION_MRID [Organisation] having this role.
 */
@Suppress("PropertyName")
abstract class TableOrganisationRoles : TableIdentifiedObjects() {

    val ORGANISATION_MRID: Column = Column(++columnIndex, "organisation_mrid", Column.Type.STRING, NULL)

}
