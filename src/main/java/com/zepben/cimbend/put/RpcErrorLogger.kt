/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.put

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Implementation of [RpcErrorHandler] that logs the passed in throwable on the provided [logger].
 */
class RpcErrorLogger(
    private val typesToHandle: Set<Class<out Throwable>>,
    private val logger: Logger = LoggerFactory.getLogger(RpcErrorLogger::class.java)
) : RpcErrorHandler {

    constructor(typesToHandle: Class<out Throwable>) : this(setOf(typesToHandle))

    override fun onError(t: Throwable) {
        logger.error("RPC error: {}", t.toString(), t)
    }

    override fun handles(t: Throwable): Boolean {
        return typesToHandle.contains(t::class.java)
    }
}
