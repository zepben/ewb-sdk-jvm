/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade

import java.sql.Statement

abstract class ChangeSet {

    abstract val number: Int
    abstract val commands: List<String>

    abstract fun preCommandsHook(statement: Statement)

    abstract fun postCommandsHook(statement: Statement)

    companion object {

        operator fun invoke(
            number: Int,
            vararg commands: List<String>,
            preCommandsHook: (Statement) -> Unit = { },
            postCommandsHook: (Statement) -> Unit = { }
        ): ChangeSet = object : ChangeSet() {

            override val number = number
            override val commands: List<String> = commands.toList().flatten()
            override fun preCommandsHook(statement: Statement) = preCommandsHook(statement)
            override fun postCommandsHook(statement: Statement) = postCommandsHook(statement)

        }

    }

}
