/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.common

import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.database.sql.ReaderException
import com.zepben.ewb.services.common.BaseService

/**
 * An exception indicating that an mRID could not be found in a [BaseService].
 */
class MRIDLookupException(message: String) : ReaderException(message)

/**
 * An exception indicating that a [NameType] could not be found in a [BaseService].
 */
class NameTypeLookupException(message: String) : ReaderException(message)

/**
 * An exception indicating that an mRID has already been used by a different objects in a [BaseService].
 */
class DuplicateMRIDException(message: String) : ReaderException(message)

/**
 * An exception indicating that an [NameType] has already been used in a [BaseService].
 */
class DuplicateNameTypeException(message: String) : ReaderException(message)
