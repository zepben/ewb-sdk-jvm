/*
* Copyright 2025 Zeppelin Bend Pty Ltd
*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.zepben.ewb.cim.iec61970.base.domain

import java.time.Instant

/**
 * Interval between two date and time points, where the interval includes the start time but excludes end time.
 *
 * @property end End date and time of this interval. The end date and time where the interval is defined up to, but excluded.
 * @property start Start date and time of this interval. The start date and time is included in the defined interval.
 */
data class DateTimeInterval(
    val end: Instant? = null,
    val start: Instant? = null
)
