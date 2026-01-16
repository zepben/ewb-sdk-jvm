/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.common

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableIdentifiedObjects

/**
 * A class representing the `Organisation` columns required for the database table.
 */
class TableOrganisations : TableIdentifiedObjects() {

    override val name: String = "organisations"

}
