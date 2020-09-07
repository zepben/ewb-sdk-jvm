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
package com.zepben.cimbend.cim.iec61968.common

import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Instant

internal class DocumentTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : Document() {}.mRID, not(equalTo("")))
        assertThat(object : Document("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val document = object : Document() {}

        assertThat(document.title, equalTo(""))
        assertThat(document.createdDateTime, nullValue())
        assertThat(document.authorName, equalTo(""))
        assertThat(document.type, equalTo(""))
        assertThat(document.status, equalTo(""))
        assertThat(document.comment, equalTo(""))

        document.apply {
            title = "title"
            createdDateTime = Instant.ofEpochMilli(1234)
            authorName = "authorName"
            type = "type"
            status = "status"
            comment = "comment"
        }

        assertThat(document.title, equalTo("title"))
        assertThat(document.createdDateTime, equalTo(Instant.ofEpochMilli(1234)))
        assertThat(document.authorName, equalTo("authorName"))
        assertThat(document.type, equalTo("type"))
        assertThat(document.status, equalTo("status"))
        assertThat(document.comment, equalTo("comment"))
    }
}
