/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sql

/**
 * This exception is thrown when you attempt to access a table class that is missing from a table collection ([BaseDatabaseTables]).
 * If this happens, you likely need to register it in the table collection via [BaseDatabaseTables.includedTables].
 */
class MissingTableConfigException(message: String?) : RuntimeException(message)
