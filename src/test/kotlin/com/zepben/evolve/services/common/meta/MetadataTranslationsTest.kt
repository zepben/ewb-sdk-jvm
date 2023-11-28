/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.common.meta

import com.google.protobuf.Timestamp as PBTimestamp
import io.mockk.*
import com.zepben.protobuf.metadata.DataSource as PBDataSource
import com.zepben.protobuf.metadata.ServiceInfo as PBServiceInfo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.contains
import org.junit.jupiter.api.Test
import java.time.Instant

class MetadataTranslationsTest {

    @Test
    fun `DataSource from Protobuf`() {
        val timestampFromPb = mockk<PBTimestamp>().also {
            every { it.seconds } returns 123L
            every { it.nanos } returns 456
        }
        val dataSourceFromPb = mockk<PBDataSource>().also {
            every { it.source } returns "source one"
            every { it.version } returns "version one"
            every { it.timestamp } returns timestampFromPb
        }

        val result = dataSourceFromPb.fromPb()

        verifySequence {
            dataSourceFromPb.source
            dataSourceFromPb.version
            dataSourceFromPb.timestamp
            timestampFromPb.seconds
            dataSourceFromPb.timestamp
            timestampFromPb.nanos
        }

        assertThat(result, isA(DataSource::class.java))
        assertThat(result.source, equalTo("source one"))
        assertThat(result.version, equalTo("version one"))
        assertThat(result.timestamp, equalTo(Instant.ofEpochSecond(123L, 456L)))
    }

    @Test
    fun `DataSource to Protobuf`() {
        val timestamp = mockk<Instant> {
            every { epochSecond } returns 123L
            every { nano } returns 456
        }
        val dataSourceToPb = mockk<DataSource>().also {
            every { it.source } returns "test source"
            every { it.version } returns "test version"
            every { it.timestamp } returns timestamp
        }

        val result = dataSourceToPb.toPb()

        verifySequence {
            dataSourceToPb.source
            dataSourceToPb.version
            dataSourceToPb.timestamp
            timestamp.epochSecond
            dataSourceToPb.timestamp
            timestamp.nano
        }

        assertThat(result, isA(PBDataSource::class.java))
        assertThat(result.source, equalTo("test source"))
        assertThat(result.version, equalTo("test version"))
        assertThat(result.timestamp.seconds, equalTo(123L))
        assertThat(result.timestamp.nanos, equalTo(456))
    }

    @Test
    fun `ServiceInfo to Protobuf`() {
        val dataSourceOne = mockk<DataSource>()
        val dataSourceTwo = mockk<DataSource>()
        val dataSources = listOf(dataSourceOne, dataSourceTwo)

        val serviceInfo = mockk<ServiceInfo>().also {
            every { it.title } returns "test title"
            every { it.version } returns "test version"
            every { it.dataSources } returns dataSources
        }

        val pbServiceInfo = mockk<PBServiceInfo>()
        val pbServiceInfoBuilder = mockk<PBServiceInfo.Builder>().also {
            every { it.setTitle(any()) } returns it
            every { it.setVersion(any()) } returns it
            every { it.addAllDataSources(any()) } returns it
            every { it.build() } returns pbServiceInfo
        }

        try {
            mockkStatic(PBServiceInfo::class)
            mockkStatic(DataSource::toPb)
            excludeRecords {
                serviceInfo.toPb()
            }
            every { any<DataSource>().toPb() } returns PBDataSource.newBuilder().build()
            every { PBServiceInfo.newBuilder() } returns pbServiceInfoBuilder

            val result = serviceInfo.toPb()

            verifySequence {
                pbServiceInfoBuilder.setTitle("test title")
                pbServiceInfoBuilder.setVersion("test version")
                dataSourceOne.toPb()
                dataSourceTwo.toPb()
                pbServiceInfoBuilder.addAllDataSources(listOf(PBDataSource.newBuilder().build(), PBDataSource.newBuilder().build()))
                pbServiceInfoBuilder.build()
            }
            verify {
                pbServiceInfo wasNot called // verify here because the following assert will call equals against it
            }
            assertThat(result, equalTo(pbServiceInfo))
        } finally {
            unmockkStatic(PBServiceInfo::class)
            unmockkStatic(DataSource::toPb)
        }
    }

    @Test
    fun `ServiceInfo from protobuf`() {
        val dataSourceOne = mockk<PBDataSource>()
        val dataSourceTwo = mockk<PBDataSource>()
        val dataSourceList = listOf(dataSourceOne, dataSourceTwo)

        val pbServiceInfo = mockk<PBServiceInfo> {
            every { title } returns "mock title"
            every { version } returns "mock version"
            every { dataSourcesList } returns dataSourceList
        }
        mockkStatic(PBDataSource::fromPb) {
            excludeRecords {
                pbServiceInfo.fromPb()
            }
            val ds1 = mockk<DataSource>()
            val ds2 = mockk<DataSource>()
            every { dataSourceOne.fromPb() } returns ds1
            every { dataSourceTwo.fromPb() } returns ds2

            val result = pbServiceInfo.fromPb()

            verifySequence {
                pbServiceInfo.title
                pbServiceInfo.version
                pbServiceInfo.dataSourcesList
                dataSourceOne.fromPb()
                dataSourceTwo.fromPb()
            }

            assertThat(result, isA(ServiceInfo::class.java))
            assertThat(result.title, equalTo("mock title"))
            assertThat(result.version, equalTo("mock version"))
            assertThat(result.dataSources, contains(ds1, ds2))
        }
    }
}
