/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet28() = ChangeSet(28) {
    // Convert rated_e and stored_e from kWh (double) to Wh (integer)
    listOf(
        """            
            with es AS ( SELECT mrid as mrid, cast(1000*rated_e as INTEGER) as rated_e, cast(1000*stored_e as INTEGER) as stored_e FROM battery_unit )
            UPDATE battery_unit AS b SET rated_e = (SELECT es.rated_e FROM es WHERE es.mrid = b.mrid), stored_e = (SELECT es.stored_e FROM es WHERE es.mrid = b.mrid);
        """
    )
}
