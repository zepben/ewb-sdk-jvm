/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

import java.sql.Connection


interface ChangeSetValidator {

    /**
     * Set up prior to applying the ChangeSet.
     * @param connection The connection to the database that the ChangeSet will be applied
     */
    fun setUp(connection: Connection)

    /**
     * Validation after applying the ChangeSet
     * @param connection The connection to the database that the ChangeSet will be applied
     */
    fun validate(connection: Connection)

    /**
     * Tear down after validating the ChangeSet.
     * @param connection The connection to the database that the ChangeSet will be applied
     */
    fun tearDown(connection: Connection)
}


// Please do not use this, it's only to cover legacy cases where we've missed tests.
internal object TodoValidator: ChangeSetValidator {
    override fun setUp(connection: Connection) {
    }

    override fun validate(connection: Connection) {
    }

    override fun tearDown(connection: Connection) {
    }
}