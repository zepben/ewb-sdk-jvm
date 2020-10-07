/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.upgrade

import java.sql.Statement

abstract class ChangeSet {
    abstract val number: Int

    abstract fun preCommandsHook(statement: Statement)

    abstract fun postCommandsHook(statement: Statement)

    abstract fun commands(): List<String>

    companion object {
        operator fun invoke(
            number: Int,
            preCommandsHook: (Statement) -> Unit = { _ -> },
            postCommandsHook: (Statement) -> Unit = { _ -> },
            commands: () -> List<String>
        ): ChangeSet = object : ChangeSet() {
            override val number: Int get() = number
            override fun preCommandsHook(statement: Statement) = preCommandsHook(statement)
            override fun postCommandsHook(statement: Statement) = postCommandsHook(statement)
            override fun commands(): List<String> = commands()
        }
    }
}

