/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.utils

import io.mockk.every
import io.mockk.mockk
import java.sql.ResultSet

internal fun createMockResultSet(wasNull: Boolean = false, mock: (ResultSet) -> Unit): ResultSet =
    mockk {
        every { next() } returns true andThen false
        if (wasNull)
            every { wasNull() } returns true
        else
            every { wasNull() } returns false
        mock(this)
    }
