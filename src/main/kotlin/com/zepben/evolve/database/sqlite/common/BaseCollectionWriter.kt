/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class BaseCollectionWriter {

    val logger: Logger = LoggerFactory.getLogger(javaClass)
    abstract fun save(): Boolean


}
