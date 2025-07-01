/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.data

/**
 * A collection of events that should be operated on as a batch.
 *
 * @property batchId A unique identifier for the batch of events being processed.
 * This allows to track or group multiple events under a single batch.
 *
 * @property events A list of [CurrentStateEvent] objects representing the state changes or
 * events that are being applied in the current batch.
 */
data class CurrentStateEventBatch(
    val batchId: Long,
    val events: List<CurrentStateEvent>
)
