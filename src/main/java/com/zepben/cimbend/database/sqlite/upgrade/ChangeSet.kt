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

