/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.mutations

import java.time.Instant

/**
 * Class to hold details for setting a new switch state.
 *
 * @property mRID the MRID of the switch to be updated.
 * @property setOpen true if the switch should be opened, false if it should be closed.
 * @property timestamp the time recorded of the actual switch state change occurring. Defaults to now if not provided.
 */
data class SwitchStateUpdate @JvmOverloads constructor(
    val mRID: String,
    val setOpen: Boolean,
    val timestamp: Instant = Instant.now()
)
