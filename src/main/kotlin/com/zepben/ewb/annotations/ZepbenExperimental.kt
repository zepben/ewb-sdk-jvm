/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.annotations

/**
 * Used to mark public API as experimental.
 * It would generally be used when, but not limited to, API is being assessed for feasibility, utility, gathering feedback, prototyping, testing outside the SDK etc.
 * Any API marked with this annotation is subject to change, or even removal, until such a time the annotation is removed from the specific functionality.
 *
 * In short: use items marked with this annotation at your own risk.
 */
@RequiresOptIn
@MustBeDocumented
annotation class ZepbenExperimental
