/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.cim.iec61970.base.core.NameType
import com.zepben.evolve.services.common.BaseService

/**
 * An exception indicating that an mRID could not be found in a [BaseService].
 */
class MRIDLookupException(message: String) : Exception(message)

/**
 * An exception indicating that a [NameType] could not be found in a [BaseService].
 */
class NameTypeLookupException(message: String) : Exception(message)

/**
 * An exception indicating that an mRID has already been used by a different objects in a [BaseService].
 */
class DuplicateMRIDException(message: String) : Exception(message)

/**
 * An exception indicating that an [NameType] has already been used in a [BaseService].
 */
class DuplicateNameTypeException(message: String) : Exception(message)
