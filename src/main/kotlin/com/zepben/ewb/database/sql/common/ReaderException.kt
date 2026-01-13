/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.common

/**
 * Exception that may be thrown in the `processRow` callback in [BaseCollectionReader.readEach]. Such exceptions will be handled gracefully instead of
 * resulting in an uncaught exception.
 *
 * @param message The error message for this exception.
 */
abstract class ReaderException(message: String?) : Exception(message)
