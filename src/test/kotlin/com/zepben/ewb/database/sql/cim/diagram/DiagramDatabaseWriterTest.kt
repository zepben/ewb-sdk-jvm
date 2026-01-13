/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.diagram

import io.mockk.mockk
import org.junit.jupiter.api.Test

class DiagramDatabaseWriterTest {

    @Test
    internal fun `constructor coverage`() {
        DiagramDatabaseWriter(databaseInitialiser = mockk(), sqlGenerator = mockk())
    }

}
