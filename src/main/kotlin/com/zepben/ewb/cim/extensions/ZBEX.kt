/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions

/**
 * Indicates that a member or class is a Zepben extension added to the CIM standard.
 *
 * All Zepben extensions should be annotated with @ZBEX, and have [ZBEX] linked into their docstring. Once this has been linked
 * everywhere, you will be able to use "find usages" to see all of our extension attributes/classes.
 */
@MustBeDocumented
annotation class ZBEX
